package bard.db.registration

import bard.db.ContextService
import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.HierarchyType
import bard.db.project.InlineEditableCommand
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

/**
 */


@TestFor(AssayDefinitionController)
@Build([Assay, Element, AssayContext, AssayContextMeasure, Measure])
@Mock([Assay, Element, AssayContext, AssayContextMeasure, Measure])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class AssayDefinitionControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {

    Assay assay



    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.metaClass.mixin(EditingHelper)
        MeasureTreeService measureTreeService = Mock(MeasureTreeService)
        AssayContextService assayContextService = Mock(AssayContextService)
        AssayDefinitionService assayDefinitionService = Mock(AssayDefinitionService)
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.measureTreeService = measureTreeService
        controller.assayContextService = assayContextService
        controller.assayDefinitionService = assayDefinitionService
        controller.contextService = Mock(ContextService)
        assay = Assay.build(assayName: 'Test')
        assert assay.validate()
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
        assert responseJSON.get("shortName").asText()
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
        Assay newAssay = Assay.build(version: 0, assayStatus: AssayStatus.DRAFT)  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer", assayStatus: AssayStatus.APPROVED)
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
        assert responseJSON.get("shortName").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Assay Status access denied'() {
        given:
        accessDeniedRoleMock()
        Assay newAssay = Assay.build(version: 0, assayStatus: AssayStatus.DRAFT)  //no designer
        Assay updatedAssay = Assay.build(assayName: "My New Name", version: 1, lastUpdated: new Date(), designedBy: "Designer", assayStatus: AssayStatus.APPROVED)
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
        Assay newAssay = Assay.build(version: 0, assayStatus: AssayStatus.APPROVED)
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newAssay.id, version: newAssay.version, name: newAssay.assayName, value: AssayStatus.APPROVED.id)
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
        assert responseJSON.get("shortName").asText()
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
        assert responseJSON.get("shortName").asText()
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
        controller.measureTreeService.createMeasureTree(_, _) >> []
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayDefinitionService.cloneAssayForEditing(_, _) >> { return newAssay }
        controller.assayDefinitionService.recomputeAssayShortName(_) >> { return newAssay }
        assert view == "/assayDefinition/show"
        assert model.assayInstance == newAssay
    }

    void 'test clone assay fail'() {
        given:
        controller.measureTreeService.createMeasureTree(_, _) >> []
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayDefinitionService.cloneAssayForEditing(_, _) >> { throw new ValidationException("message", new GrailsMockErrors(assay)) }
        assert flash.message == "Cannot clone assay definition with id \"${assay.id}\" probably because of data migration issues. Please email the BARD team at bard-users@broadinstitute.org to fix this assay"
        assert view == "/assayDefinition/show"
    }

    void 'test show'() {
        given:
        controller.measureTreeService.createMeasureTree(_, _) >> []

        when:
        params.id = assay.id
        def model = controller.show()

        then:
        model.assayInstance == assay
    }

    void 'testFindById()'() {

        when:
        params.assayId = "${assay.id}"
        controller.findById()

        then:
        "/assayDefinition/show/${assay.id}" == controller.response.redirectedUrl
    }

    void 'testFindByName'() {
        when:
        params.assayName = assay.assayName
        controller.findByName()

        then:
        "/assayDefinition/show/${assay.id}" == controller.response.redirectedUrl
    }

    void 'test editMeasure'() {
        given:
        controller.measureTreeService.createMeasureTree(_, _) >> []

        when:
        params.id = assay.id
        def model = controller.show()

        then:
        model.assayInstance == assay
    }

    void 'test add measure'() {
        when:
        mockDomain(Element)
        Element resultType = Element.build()
        Element statistic = Element.build()
        Element.saveAll([resultType, statistic])

        params.id = assay.id
        params.resultTypeId = resultType.id
        params.statisticId = statistic.id
        Measure newMeasure = Measure.build()
        def assayContextService = mockFor(AssayContextService)
        assayContextService.demand.addMeasure(1) { assayInstance, parentMeasure, rt, sm, entryUnit, hierarchyType ->
            assert assayInstance == assay
            assert parentMeasure == null
            assert rt == resultType
            assert sm == statistic
            assert entryUnit == null
            assert hierarchyType == null

            return newMeasure
        }
        controller.setAssayContextService(assayContextService.createMock())
        controller.addMeasure()

        then:
        assert response.text == "Successfully added measure ${newMeasure.displayLabel}"
        assert response.status == HttpServletResponse.SC_OK
        when:
        assayContextService.verify()

        then:
        notThrown(Exception.class)
    }

    void 'test add measure - access denied'() {
        given:
        accessDeniedRoleMock()
        when:
        mockDomain(Element)
        Element resultType = Element.build()
        Element statistic = Element.build()
        Element.saveAll([resultType, statistic])

        params.id = assay.id
        params.resultTypeId = resultType.id
        params.statisticId = statistic.id
        Measure newMeasure = Measure.build()
        controller.addMeasure()

        then:
        controller.assayContextService.addMeasure(_, _, _, _, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test add measure Fail'() {
        when:
        params.id = assay.id
        controller.addMeasure()

        then:
        assert response.text == "Result Type is Required!"
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void 'test delete measure with #desc'() {
        when:
        mockDomain(Measure)
        def measure = Measure.build(assay: assay)
        if (hasChild) {
            Measure.build(assay: assay, parentMeasure: measure)
        }

        then:
        Measure.count == beforeExpectedCount

        when:
        params.id = assay.id
        params.measureId = measure.id
        controller.deleteMeasure()

        then:
        response.redirectedUrl == '/assayDefinition/editMeasure/' + assay.id
        Measure.count == afterExpectedCount

        where:
        desc       | hasChild | beforeExpectedCount | afterExpectedCount
        "a child"  | true     | 2                   | 2
        "no child" | false    | 1                   | 0
    }


    void 'test delete measure - access denied #desc'() {
        given:
        accessDeniedRoleMock()
        def measure = Measure.build(assay: assay)
        params.id = assay.id
        params.measureId = measure.id
        when:
        controller.deleteMeasure()

        then:
        assertAccesDeniedErrorMessage()
    }

    void 'test associate context'() {
        when:
        assay = Assay.build(assayName: 'Test')
        def context = AssayContext.build(assay: assay)
        def measure = Measure.build(assay: assay)
        params.id = assay.id
        params.measureId = measure.id
        params.assayContextId = context.id

        def assayContextService = mockFor(AssayContextService)
        assayContextService.demand.associateContext(1) { Measure measureParam, AssayContext contextParam ->
            assert measureParam == measure
            assert contextParam == context
        }
        controller.setAssayContextService(assayContextService.createMock())
        controller.associateContext()

        then:
        assert response.redirectedUrl == '/assayDefinition/editMeasure/' + assay.id
        assert flash.message

        when:
        assayContextService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test associate context - access denied'() {
        given:
        accessDeniedRoleMock()

        assay = Assay.build(assayName: 'Test')
        def context = AssayContext.build(assay: assay)
        def measure = Measure.build(assay: assay)
        params.id = assay.id
        params.measureId = measure.id
        params.assayContextId = context.id
        when:
        controller.associateContext()

        then:
        controller.assayContextService.associateContext(_, _, _) >> { throw new AccessDeniedException("msg") }

        assertAccesDeniedErrorMessage()
    }

    void 'test disassociate context'() {

        when:
        assay = Assay.build(assayName: 'Test')
        AssayContext context = AssayContext.build(assay: assay)
        Measure measure = Measure.build(assay: assay)
        def contextMeasure = AssayContextMeasure.build(measure: measure, assayContext: context)
        context.addToAssayContextMeasures(contextMeasure)
        measure.addToAssayContextMeasures(contextMeasure)

        params.id = assay.id
        params.measureId = measure.id
        params.assayContextId = context.id
        def assayContextService = mockFor(AssayContextService)
        assayContextService.demand.disassociateContext(1) { Measure measureParam, AssayContext contextParam ->
            assert measureParam == measure
            assert contextParam == context
        }
        controller.setAssayContextService(assayContextService.createMock())
        controller.disassociateContext()

        then:
        response.redirectedUrl == '/assayDefinition/editMeasure/' + assay.id

        when:
        assayContextService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test disassociate context - access denied'() {

        given:
        accessDeniedRoleMock()
        assay = Assay.build(assayName: 'Test')
        AssayContext context = AssayContext.build(assay: assay)
        Measure measure = Measure.build(assay: assay)
        def contextMeasure = AssayContextMeasure.build(measure: measure, assayContext: context)
        context.addToAssayContextMeasures(contextMeasure)
        measure.addToAssayContextMeasures(contextMeasure)

        params.id = assay.id
        params.measureId = measure.id
        params.assayContextId = context.id
        when:
        controller.disassociateContext()

        then:
        controller.assayContextService.disassociateContext(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()

    }

    void "test changeRelationship"() {
        given:
        def assayContextService = mockFor(AssayContextService)
        assay = Assay.build(assayName: 'Test')
        Measure parent = Measure.build(assay: assay)
        parent.parentChildRelationship = HierarchyType.CALCULATED_FROM
        params.measureId = parent.id
        params.relationship = HierarchyType.SUPPORTED_BY.getId()
        when:
        assayContextService.demand.changeParentChildRelationship(1) { Measure measureParam, HierarchyType hierarchyType ->
            assert measureParam == parent
            assert hierarchyType == HierarchyType.SUPPORTED_BY
        }
        controller.changeRelationship()
        then:
        response.redirectedUrl == '/assayDefinition/editMeasure'

    }

    void "test changeRelationship - access denied"() {
        given:
        accessDeniedRoleMock()
        assay = Assay.build(assayName: 'Test')
        Measure parent = Measure.build(assay: assay)
        Measure child = Measure.build(assay: assay, parentMeasure: parent)
        child.parentChildRelationship = HierarchyType.CALCULATED_FROM
        params.measureId = child.id
        params.relationship = HierarchyType.SUPPORTED_BY.getId()
        when:
        controller.changeRelationship()
        then:
        controller.assayContextService.changeParentChildRelationship(_, _, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()

    }


    void 'test move measure #desc'() {
        given:
        Measure child = Measure.build(assay: assay)
        Measure parent = null
        if (parentMeasure) {
            parent = Measure.build(assay: assay)
            child.parentChildRelationship = relationshipType
        }

        when:
        controller.moveMeasureNode(child.id, parent?.id)
        then:
        controller.assayDefinitionService.moveMeasure(_, _, _) >> { arg1, arg2, arg3 ->
            arg2.parentMeasure = arg3
        }
        assert parent == child.parentMeasure
        assert expectedRelationshipType == child.parentChildRelationship

        where:
        desc                                                          | parentMeasure | relationshipType              | expectedRelationshipType
        "has both parent and child measures and relationship type"    | true          | HierarchyType.CALCULATED_FROM | HierarchyType.CALCULATED_FROM
        "has both parent and child measures but no relationship type" | true          | HierarchyType.SUPPORTED_BY    | HierarchyType.SUPPORTED_BY
        "has no parent measure"                                       | false         | null                          | null
    }
}