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

    String getTeamRole(Long roleId){
        String username = springSecurityService.principal?.username
        Person user = Person.findByUserName(username)
        PersonRole pr = PersonRole.findByPersonAndRole(user, Role.get(roleId))
        return (pr != null ? pr.teamRole : null)

    }

    boolean isTeamManager(Long roleId){
        String personRole = getTeamRole(roleId)
        boolean isManager = personRole != null ? personRole.equals("Manager") : false
        return isManager
    }
}
