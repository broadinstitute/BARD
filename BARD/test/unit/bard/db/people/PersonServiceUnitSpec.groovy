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
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import util.BardUser

import static bard.db.enums.TeamRole.*

/**
 * Created by ddurkin on 3/4/14.
 */
@TestFor(PersonService)
@Build([Role, PersonRole, Person])
@Mock([Role, PersonRole, Person])
@Unroll
class PersonServiceUnitSpec extends Specification {

    SpringSecurityService springSecurityService = Mock()

    void setup() {
        this.service.springSecurityService = this.springSecurityService
    }

    void "test findCurrentPerson() #desc"() {

        given:
        personClosure.call()

        when:
        final Person foundPerson = service.findCurrentPerson()

        then:
        1 * springSecurityService.getPrincipal() >> { mockReturn }
        foundPerson?.userName == expectedUserName

        where:
        desc                 | expectedUserName | personClosure                        | mockReturn
        'principal not null' | 'foo'            | { -> Person.build(userName: 'foo') } | new BardUser(username: 'foo')
        'principal null'     | null             | { -> Person.build(userName: 'foo') } | null
    }

    void "test isTeamManager(person, roleId)"() {
        given:
        final Person person = Person.build()
        final Role myTeam = Role.build(displayName: 'myTeam')
        PersonRole.build(person: person, role: myTeam, teamRole: teamRole)

        Role.build(displayName: 'anotherTeam')

        final Role teamToCheck = Role.findByDisplayName(teamNameToCheck)

        when:
        final boolean actualIsTeamManager = service.isTeamManager(person, teamToCheck.id)

        then:
        actualIsTeamManager == expectedIsTeamManager

        where:
        teamRole | expectedIsTeamManager | teamNameToCheck
        MEMBER   | false                 | 'myTeam'
        MANAGER  | true                  | 'myTeam'

    }

    void "test isTeamManager(person, roleId) when person not on team"() {
        given:
        final Person person = Person.build()
        final Role teamPersonIsNotOn = Role.build()

        when:
        final boolean actualIsTeamManager = service.isTeamManager(person, teamPersonIsNotOn.id)

        then:
        actualIsTeamManager == false
    }

    void "test isTeamManagerOfAnyTeam #desc expectedIsManagerOfAnyTeam: #expectedIsManagerOfAnyTeam() "() {

        given:
        final Person person = personRoleClosure.call()
        if (person) {
            for (TeamRole teamRole in teamRoles) {
                PersonRole.build(person: person, role: Role.build(), teamRole: teamRole)
            }
        }

        when:
        final boolean isManagerOfAnyTeam = service.isTeamManagerOfAnyTeam(person)

        then:
        isManagerOfAnyTeam == expectedIsManagerOfAnyTeam

        where:
        desc                    | expectedIsManagerOfAnyTeam | personRoleClosure  | teamRoles
        'null person'           | false                      | { null }           | [MEMBER]
        'person member 1 team'  | false                      | { Person.build() } | [MEMBER]
        'person member 2 teams' | false                      | { Person.build() } | [MEMBER, MEMBER]
        'person manager 1 team' | true                       | { Person.build() } | [MANAGER]
        'person manager 2 team' | true                       | { Person.build() } | [MANAGER, MANAGER
        ]
    }

}
