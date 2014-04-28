/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

        for (Role role : this.roles) {
            PersonRole.create(person, Role.findByAuthority(role.authority), springSecurityService.principal?.username, true)
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
            if(person.save(flush: true)){
                //delete all the roles,then add them again
                PersonRole.removeAll(person)
                person.save(flush: true)
                if (roles) {
                    addPersonRoles(person)
                }
            }
        }
        return person
    }

    void copyFromCmdToDomain(Person person) {
        person.userName = this.username
        person.emailAddress = this.email
        person.fullName = this.displayName
    }

    void copyFromDomainToCmd(Person person) {
        this.username = person.userName
        this.email = person.emailAddress
        this.displayName = person.fullName
        this.roles = person.roles as List<Role>
        this.version = person.version
    }
}
