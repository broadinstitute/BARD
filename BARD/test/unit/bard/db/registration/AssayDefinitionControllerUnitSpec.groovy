package bard.db.registration

import acl.CapPermissionService
import bard.db.ContextService
import bard.db.dictionary.Element
import bard.db.enums.AssayType
import bard.db.enums.Status
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.people.Role
import bard.db.project.InlineEditableCommand
import bardqueryapi.QueryService
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.validation.ValidationException
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockErrors
import org.junit.Before
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static bard.db.registration.AssayCommand.SMALL_MOLECULE_FORMAT_LABEL

/**
 */


@TestFor(AssayDefinitionController)
@Build([Assay, Element, AssayContext, Role, Experiment, ExperimentMeasure, AssayContextExperimentMeasure])
@Mock([Assay, Element, AssayContext, Role, Experiment, ExperimentMeasure, AssayContextExperimentMeasure])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class AssayDefinitionControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {

    Assay assay
    Role role

    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.metaClass.mixin(EditingHelper)
        AssayContextService assayContextService = Mock(AssayContextService)
        AssayDefinitionService assayDefinitionService = Mock(AssayDefinitionService)
        CapPermissionService capPermissionService = Mock(CapPermissionService)
        controller.springSecurityService = Mock(SpringSecurityService)

        controller.assayContextService = assayContextService
        controller.assayDefinitionService = assayDefinitionService
        controller.contextService = Mock(ContextService)
        controller.capPermissionService = capPermissionService
        controller.queryService = Mock(QueryService)

        this.role = Role.build(authority: "ROLE_TEAM_Y", displayName: "TEAM Y")
        assay = Assay.build(assayName: 'Test', ownerRole: this.role)
        assert assay.validate()
    }

    void 'test save success'() {
        given:
        final Element assayFormatValue = Element.build(label: SMALL_MOLECULE_FORMAT_LABEL)
        Role role = Role.build()
        AssayCommand assayCommand = new AssayCommand(assayName: "Some Name", assayFormatValueId: assayFormatValue.id.longValue(), springSecurityService: controller.springSecurityService, ownerRole: role.authority)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        controller.save(assayCommand)
        then:
        assert controller.response.redirectedUrl.startsWith("/assayDefinition/show/")
    }

    void 'test save failure'() {
        given:
        AssayCommand assayCommand = new AssayCommand(assayType: AssayType.TEMPLATE, springSecurityService: controller.springSecurityService)
        when:
        def model = controller.save(assayCommand)
        then:
        assert response.status == 200
    }

    void 'test create assay definition success'() {
        when:
        controller.create()
        then:
        assert response.status == 200
    }

    void 'test edit Assay Type success'() {
        given:
        Assay newAssay = Assay.build(version: 0, assayType: AssayType.TEMPLATE)  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer", assayType: AssayType.REGULAR)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.assayType.id)
        when:
        controller.editAssayType(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayType(_, _) >> { return updatedAssay }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedAssay.assayType.id
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Assay Type access denied'() {
        given:
        accessDeniedRoleMock()
        Assay newAssay = Assay.build(version: 0, assayType: AssayType.TEMPLATE)  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer", assayType: AssayType.REGULAR)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.assayType.id)
        when:
        controller.editAssayType(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayType(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Assay Type with errors'() {
        given:
        Assay newAssay = Assay.build(version: 0, assayType: AssayType.TEMPLATE)  //no designer
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: newAssay.assayType.id)
        controller.metaClass.message = { Map p -> return "foo" }
        when:
        controller.editAssayType(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayType(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Assay Status success'() {
        given:
        Assay newAssay = Assay.build(version: 0, assayStatus: Status.DRAFT)  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer", assayStatus: Status.APPROVED)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.assayStatus.id)
        when:
        controller.editAssayStatus(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayStatus(_, _) >> { return updatedAssay }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedAssay.assayStatus.id
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test assayStatus only has Approved and Retired '() {
        when:
        controller.assayStatus()

        then:
        final String responseAsString = response.contentAsString
        responseAsString == '["Approved","Retired"]'
    }

    void 'test edit Assay Status access denied'() {
        given:
        accessDeniedRoleMock()
        Assay newAssay = Assay.build(version: 0, assayStatus: Status.DRAFT)  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer", assayStatus: Status.APPROVED)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.assayStatus.id)
        when:
        controller.editAssayStatus(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayStatus(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Assay Status with errors'() {
        given:
        Assay newAssay = Assay.build(version: 0, assayStatus: Status.APPROVED)
        InlineEditableCommand inlineEditableCommand =
                new InlineEditableCommand(pk: newAssay.id, version: newAssay.version, name: newAssay.assayName, value: Status.APPROVED.id)
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editAssayStatus(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayStatus(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Assay Name success'() {
        given:
        Assay newAssay = Assay.build(version: 0, assayName: "My Name")  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.assayName)
        when:
        controller.editAssayName(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayName(_, _) >> { return updatedAssay }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedAssay.assayName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Assay Name - access denied'() {
        given:
        accessDeniedRoleMock()
        Assay newAssay = Assay.build(version: 0, assayName: "My Name")  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.assayName)
        when:
        controller.editAssayName(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayName(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Assay Name with errors'() {
        given:
        Assay newAssay = Assay.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id, version: newAssay.version, name: newAssay.assayName, value: "Designer")
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editAssayName(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateAssayName(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit DesignedBy success'() {
        given:
        Assay newAssay = Assay.build(version: 0)  //no designer
        Assay updatedAssay = Assay.build(version: 1, lastUpdated: new Date(), designedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.designedBy)
        when:
        controller.editDesignedBy(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateDesignedBy(_, _) >> { return updatedAssay }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedAssay.designedBy
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit DesignedBy -access denied'() {
        given:
        accessDeniedRoleMock()
        Assay newAssay = Assay.build(version: 0)  //no designer
        Assay updatedAssay = Assay.build(version: 1, lastUpdated: new Date(), designedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id,
                version: newAssay.version, name: newAssay.assayName, value: updatedAssay.designedBy)
        when:
        controller.editDesignedBy(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateDesignedBy(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit optimistic lock failure'() {
        given:
        Assay newAssay = Assay.build()
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id, version: newAssay.version, name: newAssay.assayName, value: "Designer")
        when:
        controller.editDesignedBy(inlineEditableCommand)
        then:
        inlineEditableCommand.validateVersions(_, _) >> { "Some error message" }
        assertOptimisticLockFailure()
    }


    void 'test edit DesignedBy with errors'() {
        given:
        Assay newAssay = Assay.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newAssay.id, version: newAssay.version, name: newAssay.assayName, value: "Designer")
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editDesignedBy(inlineEditableCommand)
        then:
        controller.assayDefinitionService.updateDesignedBy(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test clone assay'() {
        given:
        Assay newAssay = Assay.build()
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayDefinitionService.cloneAssayForEditing(_, _) >> { return newAssay }
        controller.assayDefinitionService.recomputeAssayShortName(_) >> { return newAssay }
        assert controller.response.redirectedUrl.startsWith("/assayDefinition/show/")
    }

    void 'test clone assay fail validation error'() {
        given:
        Role.metaClass.'static'.findByAuthority = {
            return this.role
        }
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [this.role]
        }
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayDefinitionService.cloneAssayForEditing(_, _) >> { throw new ValidationException("message", new GrailsMockErrors(assay)) }
        assert flash.message.contains("Please email the BARD team at bard-users@broadinstitute.org to fix this assay")
        assert controller.response.redirectedUrl.startsWith("/assayDefinition/show/")
    }

    void 'test clone assay fail users role, not in role list error'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayDefinitionService.cloneAssayForEditing(_, _) >> { throw new ValidationException("message", new GrailsMockErrors(assay)) }
        assert flash.message == "You need to be a member of at least one team to clone any assay"
        assert controller.response.redirectedUrl.startsWith("/assayDefinition/show/")
    }

    void 'test show'() {
        given:
        CapPermissionService capPermissionService = Mock(CapPermissionService)
        controller.capPermissionService = capPermissionService
        when:
        params.id = assay.id
        def model = controller.show()

        then:
        controller.queryService.findActiveVsTestedForExperiments(_) >> { [:] }
        capPermissionService.getOwner(_) >> { 'owner' }
        model.assayInstance == assay
    }

    def 'test show - fail #label'() {
        given:
        params.id = id
        when:
        def model = controller.show()
        then:
        assert model.message == message
        where:
        label                   | id    | message
        "No Assay Id"           | null  | "A Valid Assay Definition ID is required"
        "Non Existing Assay ID" | 10000 | "default.not.found.message"
    }

    def 'test generateAndRenderJSONResponse #desc'() {

        controller.metaClass.mixin(EditingHelper)
        expect:
        def result = controller.generateAndRenderJSONResponse(version, modifiedBy, lastUpdated, newValue)
        response.contentAsString == expectedJson

        where:
        desc                                   | version | modifiedBy    | lastUpdated | newValue   | expectedJson
        "pass thru"                            | 1L      | 'foo'         | new Date()  | 'newValue' | '{"version":"1","modifiedBy":"foo","lastUpdated":"03/11/2014","data":"newValue"}'
        "ensure modifiedBy doesn't show email" | 1L      | 'foo@foo.com' | new Date()  | 'newValue' | '{"version":"1","modifiedBy":"foo","lastUpdated":"03/11/2014","data":"newValue"}'

    }
}