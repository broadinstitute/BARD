package bard.db.registration

import acl.CapPermissionService
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Measure, Experiment, AssayContextMeasure, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument])
@Mock([Assay, Measure, Experiment, ExperimentMeasure, AssayContext, AssayContextMeasure, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)
@TestFor(AssayDefinitionService)
public class AssayDefinitionServiceUnitSpec extends Specification {

    void 'test generateAssayComparisonReport'() {
        setup:
        Assay assayOne = Assay.build()
        AssayContext contextOne = AssayContext.build(assay: assayOne, contextName: "alpha")
        AssayContextItem.build(assayContext: contextOne)
        Measure measureOne = Measure.build(assay: assayOne)
        AssayContextMeasure.build(assayContext: contextOne, measure: measureOne)

        Assay assayTwo = Assay.build()
        AssayContext contextTwo = AssayContext.build(assay: assayTwo, contextName: "alpha2")
        AssayContextItem.build(assayContext: contextTwo)
        Measure measureTwo = Measure.build(assay: assayTwo)
        AssayContextMeasure.build(assayContext: contextTwo, measure: measureTwo)

        when:
        Map m = service.generateAssayComparisonReport(assayOne, assayTwo)

        then:
        assert m.exclusiveToAssayOne
        assert m.exclusiveToAssayTwo
        assert m.assayOneName
        assert m.assayOneADID
        assert m.assayTwoName
        assert m.assayTwoADID
    }

    void "test update designed By"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName20', designedBy: "BARD", capPermissionService: Mock(CapPermissionService))
        final String newDesignedBy = "CAP"
        when:
        final Assay updatedAssay = service.updateDesignedBy(assay.id, newDesignedBy)
        then:
        assert newDesignedBy == updatedAssay.designedBy
    }

    void "test update assay name"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName20', assayStatus: AssayStatus.DRAFT, capPermissionService: Mock(CapPermissionService))
        final String newAssayName = "New Assay Name"
        when:
        final Assay updatedAssay = service.updateAssayName(assay.id, newAssayName)
        then:
        assert newAssayName == updatedAssay.assayName
    }

    void "test update assay status"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName10', assayStatus: AssayStatus.DRAFT, capPermissionService: Mock(CapPermissionService))
        when:
        final Assay updatedAssay = service.updateAssayStatus(assay.id, AssayStatus.APPROVED)
        then:
        assert AssayStatus.APPROVED == updatedAssay.assayStatus
    }

    void "test update assay type"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName10', assayType: AssayType.PANEL_GROUP, capPermissionService: Mock(CapPermissionService))
        when:
        final Assay updatedAssay = service.updateAssayType(assay.id, AssayType.TEMPLATE)
        then:
        assert AssayType.TEMPLATE == updatedAssay.assayType
    }
}
