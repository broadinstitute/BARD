package bard.db.people

import bard.db.PersonService
import bard.db.project.InlineEditableCommand
import bard.db.registration.AbstractInlineEditingControllerUnitSpec
import bard.db.registration.EditingHelper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 12/3/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(RoleController)
@Build([Role])
@Mock([Role,PersonRole])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class RoleControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {
    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
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

    void "test show"() {
        given:
        final String authority = "ROLE_TEAM_C"
        final String displayName = "Team C"
        Role role = Role.build(authority: authority, displayName: displayName)
        when:
        def model = controller.show(role.id)
        then:
        assert model.roleInstance
        assert model.roleInstance.authority == authority
        assert model.roleInstance.displayName == displayName
        assert model.editable == "canedit"
    }

    void "test show - non-existing role"() {
        when:
        controller.show(20002)
        then:
        assert response.redirectedUrl == '/role/list'
        assert flash.message == "default.not.found.message"
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
}
