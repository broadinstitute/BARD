package bard.db.people

import bard.db.enums.TeamRole
import bard.db.project.InlineEditableCommand
import bard.db.registration.AbstractInlineEditingControllerUnitSpec
import bard.db.registration.EditingHelper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Before
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static bard.db.enums.TeamRole.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 12/3/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(RoleController)
@Build([Role, PersonRole, Person])
@Mock([Role, PersonRole, Person])
@Unroll
class RoleControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {
    @Before
    void setup() {

        controller.metaClass.mixin(EditingHelper)
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.personService = Mock(PersonService)

    }

    void "test list"() {
        given:
        Role.build()
        Role.build()
        when:
        def model = controller.list()
        then:
        assert model.roleInstanceList
        List<Role> roles = model.roleInstanceList
        assert roles.size() == 2
    }

    void "test index"() {
        when:
        controller.index()
        then:
        assert response.redirectedUrl == '/role/list'
    }

    void "test create"() {
        when:
        def model = controller.create()
        then:
        assert model.roleInstance
        assert !model.roleInstance.id
    }

    void "test show success #desc"() {
        PersonService personService = controller.personService

        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)

        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)
        numTeamMembers.times { PersonRole.build(person: Person.build(), role: teamC, teamRole: TeamRole.MEMBER) }

        when:
        controller.show(teamC.id)
        final Map<String, Object> model = controller.modelAndView?.model

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        notThrown(AccessDeniedException)

        model.roleInstance == teamC
        model.editable == "canedit"
        model.teamMembers?.size() == numTeamMembers + 1
        model.isTeamManager == isTeamManager
        model.isAdmin == isAdmin

        where:
        desc          | isTeamManager | isAdmin | numTeamMembers
        'isManager'   | true          | false   | 3
        'isBardAdmin' | false         | true    | 3

    }

    void "test show accessDenied #desc"() {
        PersonService personService = controller.personService

        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)

        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)
        numTeamMembers.times { PersonRole.build(person: Person.build(), role: teamC, teamRole: TeamRole.MEMBER) }

        when:
        controller.show(teamC.id)
        final Map<String, Object> model = controller.modelAndView?.model

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        thrown(AccessDeniedException)

        where:
        desc                   | isTeamManager | isAdmin | numTeamMembers
        'not manager or admin' | false         | false   | 3
    }

    void "test show - non-existing role"() {
        when:
        controller.show(20002)
        then:
        assert response.redirectedUrl == '/role/list'
        assert flash.message == "default.not.found.message"
    }

    void "test successfull addUserToTeam #desc"() {
        PersonService personService = controller.personService
        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)

        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)
        Person.build(emailAddress: email)

        when:
        controller.addUserToTeam(email, teamC.id)

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        PersonRole.findAllByRole(teamC).find { it.person.emailAddress == email }

        where:
        desc                     | isTeamManager | isAdmin | email
        'addUser when manager'   | true          | false   | 'foo@foo.com'
        'addUser when bardAdmin' | false         | true    | 'foo@foo.com'
    }

    void "test accessDenied addUserToTeam #desc"() {
        PersonService personService = controller.personService
        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)

        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)
        Person.build(emailAddress: email)

        when:
        controller.addUserToTeam(email, teamC.id)

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        thrown(AccessDeniedException)

        where:
        desc                   | isTeamManager | isAdmin | email
        'not manager or admin' | false         | false   | 'foo@foo.com'
    }


    void "test myTeams "() {
        PersonService personService = controller.personService
        Person currentPerson = Person.build()
        given:
        namesAndTeamRoles.each {
            def (String teamName, TeamRole teamRole) = it
            Role teamC = Role.build(authority: "ROLE_TEAM_${teamName}")
            PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: teamRole)
        }

        when:
        def model = controller.myTeams()

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        model.teams.role.authority == expectedTeamsManaged

        where:
        desc              | namesAndTeamRoles                | expectedTeamsManaged
        'manager 0 teams' | [['A', MEMBER]]                  | []
        'manager 0 teams' | [['A', MEMBER], ['B', MEMBER]]   | []
        'manager 1 team'  | [['A', MEMBER], ['B', MANAGER]]  | ['ROLE_TEAM_B']
        'manager 2 teams' | [['A', MANAGER], ['B', MANAGER]] | ['ROLE_TEAM_A', 'ROLE_TEAM_B']
    }

    void "test successful modifyTeamRoles #desc"() {
        PersonService personService = controller.personService
        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)
        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)
        final List<PersonRole> personRoles = []
        teamEmailsAndTeamRoles.each {
            def (String email, TeamRole teamRole) = it
            Person person = Person.build(emailAddress: email)
            personRoles.add(PersonRole.build(person: person, role: teamC, teamRole: teamRole))
        }

        controller.params.checkboxes = personRoles.collect { it.id }

        when:
        controller.modifyTeamRoles(teamC.id, setAllToTeamRole.id)

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        notThrown(AccessDeniedException)
        personRoles.teamRole.every { it == setAllToTeamRole }

        where:
        desc                                 | isTeamManager | isAdmin | setAllToTeamRole | teamEmailsAndTeamRoles
        'as manager set 1 person to Manager' | true          | false   | MANAGER          | [['a@a.com', MEMBER]]
        'as manager set 2 people to Manager' | true          | false   | MANAGER          | [['a@a.com', MEMBER], ['b@b.com', MEMBER]]
        'as admin set 1 person to Manager'   | false         | true    | MANAGER          | [['a@a.com', MEMBER]]
        'as admin set 1 person to Manager'   | false         | true    | MANAGER          | [['a@a.com', MEMBER], ['b@b.com', MEMBER]]
        'as manager set 1 person to Member'  | true          | false   | MEMBER           | [['a@a.com', MANAGER]]
        'as manager set 2 people to Member'  | true          | false   | MEMBER           | [['a@a.com', MANAGER], ['b@b.com', MANAGER]]
        'as admin set 1 person to Member'    | false         | true    | MEMBER           | [['a@a.com', MANAGER]]
        'as admin set 1 person to Member'    | false         | true    | MEMBER           | [['a@a.com', MANAGER], ['b@b.com', MANAGER]]
    }

    void "test failure of modifyTeamRoles for user on another team #desc"() {
        PersonService personService = controller.personService
        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)
        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)

        Role teamA = Role.build(authority: "ROLE_TEAM_A")
        final List<PersonRole> personRoles = []
        teamEmailsAndTeamRolesForOtherTeam.each {
            def (String email, TeamRole teamRole) = it
            Person person = Person.build(emailAddress: email)
            personRoles.add(PersonRole.build(person: person, role: teamA, teamRole: teamRole))
        }
        controller.params.checkboxes = personRoles.collect { it.id }

        when:
        controller.modifyTeamRoles(teamC.id, setAllToTeamRole.id)

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        notThrown(AccessDeniedException)
        personRoles.teamRole.every { it != setAllToTeamRole }
        controller.flash.error == 'ERROR: Unable to set team role Manager. No record found.'

        where:
        desc                                       | isTeamManager | isAdmin | setAllToTeamRole | teamEmailsAndTeamRolesForOtherTeam
        'try and change teamRole for another team' | true          | false   | MANAGER          | [['a@a.com', MEMBER]]
    }

    void "test accessDenied modifyTeamRoles #desc"() {
        PersonService personService = controller.personService
        given:
        Person currentPerson = Person.build()
        Role teamC = Role.build(authority: "ROLE_TEAM_C")
        PersonRole personRole = PersonRole.build(person: currentPerson, role: teamC, teamRole: TeamRole.MEMBER)
        setupManagerAndAdmin(personRole, currentPerson, isTeamManager, isAdmin)

        when:
        controller.modifyTeamRoles(teamC.id, MANAGER.id)

        then:
        (1.._) * personService.findCurrentPerson() >> currentPerson
        (1.._) * personService.isTeamManager(teamC.id) >> isTeamManager
        thrown(AccessDeniedException)

        where:
        desc                   | isTeamManager | isAdmin
        'not manager or admin' | false         | false
    }

    void "test save #desc #authority"() {
        given:
        params.authority = authority
        params.displayName = displayName
        when:
        controller.save()
        then:
        assert response.redirectedUrl.startsWith(expectedRedirectUrl)
        where:
        desc          | authority       | displayName         | expectedRedirectUrl
        "Starts with" | "ROLE_TEAM_CCC" | "Some display Name" | "/role/show"
        "Starts with" | "role_team_vvv" | "Some display Name" | "/role/show"
        "Starts with" | "team_yyyy"     | "Some display Name" | "/role/show"
    }

    void "test save fail #desc #authority"() {
        given:
        params.authority = authority
        params.displayName = displayName
        when:
        controller.save()
        then:
        assert model.roleInstance
        assert model.roleInstance.authority == authority
        assert model.roleInstance.displayName == displayName
        assert view == '/role/create'
        where:
        desc           | authority | displayName
        "No authority" | ""        | "Some display Name"
    }

    void 'test edit authority success'() {
        given:
        Role newRole = Role.build(version: 0, authority: "ROLE_TEAM_M", displayName: "Some display")
        final String newAuthority = "BARD"
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newRole.id,
                version: newRole.version, name: newRole.authority, value: newAuthority)
        when:
        controller.editAuthority(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "1"
        assert responseJSON.get("data").asText() == "ROLE_TEAM_" + newAuthority
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit display Name success'() {
        given:
        Role newRole = Role.build(version: 0, authority: "ROLE_TEAM_M", displayName: "Some display")
        final String newDisplayName = "BARD"
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newRole.id,
                version: newRole.version, name: newRole.authority, value: newDisplayName)
        when:
        controller.editDisplayName(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "1"
        assert responseJSON.get("data").asText() == newDisplayName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    private void setupManagerAndAdmin(PersonRole personRole, Person person, boolean manager, boolean admin) {
        if (manager) {
            personRole.teamRole = TeamRole.MANAGER
        }
        if (admin) {
            PersonRole.build(person: person, role: Role.build(authority: 'ROLE_BARD_ADMINISTRATOR'))
        }
    }

}
