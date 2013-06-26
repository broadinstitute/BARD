package bard.db

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role

class PersonService {

    Person update(Person person, String userName, String fullName, String emailAddress, Role newObjectRole, Collection<Role> roles) {
        person.userName = userName
        person.fullName = fullName
        person.emailAddress = emailAddress
        person.newObjectRole = newObjectRole

        def oldRoles = PersonRole.findAllByPerson(person)
        def newRoles = new HashSet(roles)
        for(old in oldRoles) {
            if(newRoles.contains(old.role)) {
                newRoles.remove(old.role)
            } else {
                old.delete()
            }
        }

        for(newRole in newRoles) {
            PersonRole newPersonRole = new PersonRole(person:  person, role: newRole)
            newPersonRole.save(failOnError: true)
        }

        person.save(failOnError: true, flush: true)

        return person
    }
}
