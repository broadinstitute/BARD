package bard.register.crowd

import bard.db.command.BardCommand
import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import register.crowd.CrowdGroupMembership
import register.crowd.CrowdPassword
import register.crowd.CrowdRegisterUserService
import register.crowd.CrowdRegistrationUser

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class RegisterController {
    static defaultAction = 'index'
    // override default value from base class
    static allowedMethods = [register: 'POST']
    CrowdRegisterUserService crowdRegisterUserService

    def index() {
        def copy = [:] + (flash.chainedParams ?: [:])
        copy.remove 'controller'
        copy.remove 'action'
        [registerCommand: new RegisterCommand(copy)]
    }

    def listUsersAndGroups() {
        final CrowdGroupMembership groupMembership = this.crowdRegisterUserService.listUsersAndGroups();
        render(view: 'listUsersAndGroups',
                model: [groupMembership: groupMembership])


    }

    def register(RegisterCommand registerCommand) {

        if (!registerCommand.validate()) {
            render view: 'index', model: [registerCommand: registerCommand]
            return
        }
        Person person = registerCommand.createNewPerson()
        if (person) {
            CrowdRegistrationUser registrationUser =
                new CrowdRegistrationUser(email: registerCommand.email,
                        name: registerCommand.username,
                        password: new CrowdPassword(value: registerCommand.password),
                        first_name: registerCommand.firstName, last_name: registerCommand.lastName,
                        display_name: registerCommand.displayName, active: true)

            crowdRegisterUserService.registerUser(registrationUser)
            //add to role table
            //add to db
            flash.message = "Successfully added to database"
        } else {
            flash.message = "Failed to create user"
        }

        redirect(controller: "person", action: "list")
    }
}
class RegisterCommand extends BardCommand {

    String username
    String email
    String password
    String password2
    String firstName
    String lastName
    String displayName
    Role primaryGroup

    CrowdRegisterUserService crowdRegisterUserService
    SpringSecurityService springSecurityService

    static constraints = {
        username blank: false, nullable: false, validator: { value, command ->
            if (value) {
                if (command.crowdRegisterUserService.findUserByUserName(value)) {
                    return 'registerCommand.username.unique'
                }
            }
        }
        email blank: false, nullable: false, email: true, validator: { value, command ->
            if (value) {
                if (command.crowdRegisterUserService.findUserByEmail(value)) {
                    return 'registerCommand.email.unique'
                }
            }
        }
        primaryGroup nullable: false, validator: { value, command ->
            if (!Role.findByAuthority(value?.authority)) {
                return "Role with id ${value.authority} does not exist"
            }
        }
        password blank: false, nullable: false, validator: passwordValidator
        password2 validator: password2Validator
    }

    static private final passwordValidator = { String password, command ->
        if (command.username && command.username.equals(password)) {
            return 'command.password.error.username'
        }

        if (!checkPasswordMinLength(password, command) ||
                !checkPasswordMaxLength(password, command) ||
                !checkPasswordRegex(password, command)) {
            return 'command.password.error.strength'
        }
    }

    static private boolean checkPasswordMinLength(String password, command) {

        int minLength = 8

        password && password.length() >= minLength
    }

    static private boolean checkPasswordMaxLength(String password, command) {
        int maxLength = 64

        password && password.length() <= maxLength
    }

    static private boolean checkPasswordRegex(String password, command) {
        String passValidationRegex = '^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$'

        password && password.matches(passValidationRegex)
    }

    static final password2Validator = { value, command ->
        if (command.password != command.password2) {
            return 'command.password2.error.mismatch'
        }
    }

    Person createNewPerson() {
        Person personToReturn = null
        if (validate()) {
            Person person = new Person()
            copyFromCmdToDomain(person)
            if (attemptSave(person)) {
                personToReturn = person
                if (!PersonRole.findByPersonAndRole(person, person.newObjectRole)) {
                    PersonRole.create(person, person.newObjectRole, springSecurityService.principal?.username, true)
                }
            }

        }
        return personToReturn
    }

    void copyFromCmdToDomain(Person person) {
        person.userName = this.username
        person.emailAddress = this.email
        person.fullName = this.displayName
        person.newObjectRole = primaryGroup
    }

}

