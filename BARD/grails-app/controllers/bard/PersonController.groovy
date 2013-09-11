package bard

import bard.db.command.BardCommand
import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class PersonController {
    def index() {
        redirect action: "list"
    }

    def list() {
        final List<Person> people = []
        params.max = Math.min(params.max ? params.int('max') : 100, 200)
        people.addAll(Person.list(params))
        render(view: 'list', model: [people: people, roles: Role.all, peopleTotal: Person.count(), personCommand: new PersonCommand()])
    }

    def edit() {
        Person person = Person.findById(params.id)
        if (person) {
            PersonCommand personCommand = new PersonCommand(person)
            render(view: 'edit', model: [person: person, roles: Role.all, personCommand: personCommand])
        } else {
            flash.message = "Person not found. Try again"
            redirect action: "list"
            return
        }
    }

    def save(PersonCommand personCommand) {
        if (!personCommand.validate()) {
            flash.message = "Person not successfully added. Try again"
            redirect action: "list"
            return
        }

        final Person person = personCommand.createNewPerson()
        if (!person) {
            flash.message = "Person not successfully added. Try again"
        } else {
            flash.message = "Person successfully added"
        }
        redirect action: "list"
    }

    def update(PersonCommand personCommand) {
        if (personCommand.updateExistingPerson()) {
            flash.message = "Successfully edited object"
        } else {
            flash.message = "Could not edit object"
        }
        redirect action: "list"
    }
}
@InheritConstructors
@Validateable
class PersonCommand extends BardCommand {
    String username
    String email
    String displayName
    Role primaryGroup
    List<Role> roles = []
    Long version
    //We turn this off to allow user sign ups, since we want only admins to assign primary groups
    boolean validate = true

    SpringSecurityService springSecurityService

    PersonCommand() {}

    PersonCommand(Person person) {
        copyFromDomainToCmd(person)
    }

    static constraints = {
        username blank: false, nullable: false,
                validator: { value, command ->
                    if (value) {
                        if (Person.findByUserName(value)) {
                            return 'registerCommand.username.unique'
                        }
                    }
                }
        email blank: false, nullable: false, email: true,
                validator: { value, command ->
                    if (value) {
                        if (Person.findByEmailAddress(value)) {
                            return 'registerCommand.email.unique'
                        }
                    }
                }
        displayName blank: false, nullable: false
        primaryGroup nullable: true, validator: { value, command ->
            if (command.validate) {
                if (!value) {
                    return 'registerCommand.primaryGroup.nullable'
                }
            }
        }

    }

    Person createNewPerson() {
        Person personToReturn = null
        if (validate()) {
            Person person = new Person()
            copyFromCmdToDomain(person)
            if (attemptSave(person)) {
                personToReturn = person
                //insert roles
                addPersonRoles(person)
            }
        }
        return personToReturn
    }

    void addPersonRoles(Person person) {
        if (!this.primaryGroup) {
            return
        }
        if (!PersonRole.findAllByPersonAndRole(person, this.primaryGroup)) {

            PersonRole.create(person, person.newObjectRole, springSecurityService.principal?.username, true)
        }
        for (Role role : this.roles) {
            if (role != primaryGroup) {
                PersonRole.create(person, Role.findByAuthority(role.authority), springSecurityService.principal?.username, true)
            }
        }
    }

    Person updateExistingPerson() {
        Person person = Person.findByUserName(this.username)
        if (!person) {
            throw new RuntimeException("Could not find Person with user name ${username}")
        }

        if (this.version?.longValue() != person.version?.longValue()) {
            throw new RuntimeException("Optimistic lock error")
        } else {
            copyFromCmdToDomain(person)
            person = person.save(flush: true)

            roles.add(this.primaryGroup)
            //delete all the roles,then add them again
            PersonRole.removeAll(person)
            person = person.save(flush: true)
            if (roles) {
                addPersonRoles(person)
            }
        }
        return person
    }

    void copyFromCmdToDomain(Person person) {
        person.userName = this.username
        person.emailAddress = this.email
        person.fullName = this.displayName
        if (this.primaryGroup) {
            person.newObjectRole = Role.findByAuthority(primaryGroup.authority)
        }
    }

    void copyFromDomainToCmd(Person person) {
        this.username = person.userName
        this.email = person.emailAddress
        this.displayName = person.fullName
        this.primaryGroup = person.newObjectRole
        this.roles = person.roles as List<Role>
        this.version = person.version
    }
}
