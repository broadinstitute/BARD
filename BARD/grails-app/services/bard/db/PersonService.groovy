package bard.db

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService

class PersonService {

    SpringSecurityService springSecurityService

    Person update(Person person, String userName, String fullName, String emailAddress,Collection<Role> roles) {
        person.userName = userName
        person.fullName = fullName
        person.emailAddress = emailAddress

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

    PersonRole getPersonRole(Long roleId){
        String username = springSecurityService.principal?.username
        Person user = Person.findByUserName(username)
        Role role = Role.get(roleId)
        PersonRole pr = PersonRole.findByPersonAndRole(user, role)
        return pr
    }

    boolean isTeamManager(Long roleId){
        PersonRole pr = getPersonRole(roleId)
        String personRole = pr != null ? pr.teamRole : null
        boolean isManager = personRole != null ? personRole.equals("Manager") : false
        return isManager
    }

    boolean isInTeam(Long roleId){
        PersonRole pr = getPersonRole(roleId)
        return pr != null ? true : false
    }
}
