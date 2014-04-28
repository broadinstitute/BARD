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
