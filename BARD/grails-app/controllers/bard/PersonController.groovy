package bard

import bard.db.PersonService
import bard.db.people.Person
import bard.db.people.Role
import grails.plugins.springsecurity.Secured

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class PersonController {
    PersonService personService

    def index() { redirect action: "list" }

    def list() {
        return [people: Person.all, roles: Role.all]
    }

    def edit() {
        Person person = Person.get(params.id)
        def roles = Role.list()
        return [person: person, roles: roles]
    }

    def save() {
        println("params: ${params}")
        Person person = new Person()
        personService.update(person, params.userName, params.fullName, params.emailAddress, Role.get(params.primaryGroup), params.list("roles").collect {Role.get(it)})

        redirect action: "list"
    }

    def update() {
        println("params: ${params}")
        Person person = Person.get(params.id)
        personService.update(person, params.userName, params.fullName, params.emailAddress, Role.get(params.primaryGroup), params.list("roles").collect {Role.get(it)})

        redirect action: "list"
    }
}
