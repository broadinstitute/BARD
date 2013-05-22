package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.HierarchyType
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.validation.ValidationException
import org.codehaus.groovy.grails.plugins.testing.GrailsMockErrors
import org.junit.Before
import registration.AssayService
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

/**
 */


@TestFor(AssayDefinitionController)
@Build([Assay, Element, AssayContext, AssayContextMeasure])
@Mock([Assay, Element, AssayContext, AssayContextMeasure])
@Unroll
class AssayDefinitionControllerUnitSpec extends Specification {

    Assay assay

    @Before
    void setup() {
        MeasureTreeService measureTreeService = Mock(MeasureTreeService)
        AssayService assayService = Mock(AssayService)
        AssayContextService assayContextService = Mock(AssayContextService)
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.measureTreeService = measureTreeService
        controller.assayService = assayService
        controller.assayContextService = assayContextService
        assay = Assay.build(assayName: 'Test')
        assert assay.validate()
    }


    void 'test clone assay'() {
        given:
        Assay newAssay = Assay.build()
        controller.measureTreeService.createMeasureTree(_, _) >> []
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayService.cloneAssayForEditing(_, _) >> { return newAssay }
        controller.assayService.recomputeAssayShortName(_) >> { return newAssay }
        assert response.redirectedUrl == "/assayDefinition/show/${newAssay.id}"
    }

    void 'test clone assay fail'() {
        given:
        controller.measureTreeService.createMeasureTree(_, _) >> []
        when:
        controller.cloneAssay(assay.id)
        then:
        controller.assayService.cloneAssayForEditing(_, _) >> { throw new ValidationException("message", new GrailsMockErrors(assay)) }
        assert flash.message == "Cannot clone assay definition with id \"${assay.id}\" probably because of data migration issues. Please email the BARD team to fix this assay"
        assert response.redirectedUrl == "/assayDefinition/show/${assay.id}"

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

        def assayContextService = mockFor(AssayContextService)
        assayContextService.demand.addMeasure(1) { assayInstance, parentMeasure, rt, sm, entryUnit, hierarchyType ->
            assert assayInstance == assay
            assert parentMeasure == null
            assert rt == resultType
            assert sm == statistic
            assert entryUnit == null
            assert hierarchyType == null

            return Measure.build()
        }
        controller.setAssayContextService(assayContextService.createMock())
        controller.addMeasure()

        then:
        response.redirectedUrl == '/assayDefinition/editMeasure/' + assay.id

        when:
        assayContextService.verify()

        then:
        notThrown(Exception.class)
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
        response.redirectedUrl == '/assayDefinition/editMeasure/' + assay.id

        when:
        assayContextService.verify()

        then:
        notThrown(Exception.class)
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

    void "test moveCard"() {
        given:
        assay = Assay.build(assayName: 'Test')
        AssayContext context = AssayContext.build(assay: assay, contextGroup: "context_group")
        String new_context_group = 'context_group2'
        when:
        controller.moveCard(new_context_group, context.id)
        then:
        new_context_group == context.contextGroup

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

    void 'test edit summary'() {
        given:
        assay = Assay.build()
        when:
        controller.editSummary(assay.id, AssayStatus.DRAFT.id, "assayName", "designedBy", AssayType.REGULAR.id)

        then:
        assay.assayStatus == AssayStatus.DRAFT
    }

    @Unroll
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
        assert parent == child.parentMeasure
        assert expectedRelationshipType == child.parentChildRelationship

        where:
        desc                                                          | parentMeasure | relationshipType              | expectedRelationshipType
        "has both parent and child measures and relationship type"    | true          | HierarchyType.CALCULATED_FROM | HierarchyType.CALCULATED_FROM
        "has both parent and child measures but no relationship type" | true          | null                          | HierarchyType.SUPPORTED_BY
        "has no parent measure"                                       | false         | null                          | null
    }

}