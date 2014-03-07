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
