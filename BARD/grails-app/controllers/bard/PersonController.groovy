package bard

import bard.db.people.Person
import bard.db.people.Role
import grails.plugins.springsecurity.Secured

@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class PersonController {
    def index() { redirect action: "list" }

    def list() {
        return [people: Person.all, roles: Role.all]
    }

    def edit() {
        Person person = Person.get(params.id)
        return [person: person, roles: Role.all]
    }

    def save() {
        Person person = new Person()
        person.properties["userName", "fullName", "emailAddress"] = params
        person.save()

        redirect action: "list"
    }

    def update() {
        Person person = Person.get(params.id)
        person.properties["userName", "fullName", "emailAddress"] = params
        person.save()

        redirect action: "list"
    }
}
