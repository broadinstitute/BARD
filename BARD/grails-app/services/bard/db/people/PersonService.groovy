package bard.db.people

import bard.db.enums.TeamRole
import grails.plugins.springsecurity.SpringSecurityService

class PersonService {

    SpringSecurityService springSecurityService

    Person update(Person person, String userName, String fullName, String emailAddress, Collection<Role> roles) {
        person.userName = userName
        person.fullName = fullName
        person.emailAddress = emailAddress

        def oldRoles = PersonRole.findAllByPerson(person)
        def newRoles = new HashSet(roles)
        for (old in oldRoles) {
            if (newRoles.contains(old.role)) {
                newRoles.remove(old.role)
            } else {
                old.delete()
            }
        }

        for (newRole in newRoles) {
            PersonRole newPersonRole = new PersonRole(person: person, role: newRole)
            newPersonRole.save(failOnError: true)
        }

        person.save(failOnError: true, flush: true)

        return person
    }

    /**
     *
     * @return the person currently logged in or null
     */
    Person findCurrentPerson() {
        String username = springSecurityService.principal?.username
        Person.findByUserName(username)
    }
    /**
     *
     * @param person
     * @param roleId id for the role/team
     * @return true if person is a manager for this team/role
     */
    boolean isTeamManager(Person person, Long roleId) {
        final Role role = Role.findById(roleId)
        final PersonRole pr = PersonRole.findByPersonAndRole(person, role)
        return TeamRole.MANAGER.equals(pr?.teamRole)
    }

    /**
     *
     * @param roleId  roleId id for the role/team
     * @return true if the authenticated person is a manager the role/team
     */
    boolean isTeamManager(Long roleId) {
        final Person person = findCurrentPerson()
        return isTeamManager(person, roleId)
    }

    /**
     *
     * @return true if the person manages any teams
     */
    boolean isTeamManagerOfAnyTeam(Person person) {
        if (person) {
            for (PersonRole personRole in PersonRole.findAllByPerson(person)) {
                if (TeamRole.MANAGER == personRole.teamRole) {
                    return true
                }
            }
        }
        return false
    }
}
