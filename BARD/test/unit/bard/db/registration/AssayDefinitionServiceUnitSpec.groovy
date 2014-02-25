package bard.db.registration

import acl.CapPermissionService
import bard.db.enums.AssayType
import bard.db.enums.Status
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.people.Person
import bard.db.people.Role
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.junit.Before
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import spock.lang.Specification
import spock.lang.Unroll
import test.TestUtils
import util.BardUser

import static bard.db.enums.Status.*
import static bard.db.enums.ValueType.NONE
import static bard.db.registration.AttributeType.Fixed

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Experiment, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument, Role, AssayContextExperimentMeasure, Person])
@Mock([Assay, Experiment, ExperimentMeasure, AssayContext, AssayContextItem, AssayDocument, Role, AssayContextExperimentMeasure, Person])
@TestMixin(ServiceUnitTestMixin)
@TestFor(AssayDefinitionService)
@Unroll
public class AssayDefinitionServiceUnitSpec extends Specification {

    @Before
    void setup() {
        service.capPermissionService = Mock(CapPermissionService)
    }

    void 'test generateAssayComparisonReport'() {
        setup:
        Assay assayOne = Assay.build()
        Experiment experimentOne = Experiment.build(assay: assayOne)
        AssayContext contextOne = AssayContext.build(assay: assayOne, contextName: "alpha")
        AssayContextItem.build(assayContext: contextOne)
        ExperimentMeasure measureOne = ExperimentMeasure.build(experiment: experimentOne)
        AssayContextExperimentMeasure.build(assayContext: contextOne, experimentMeasure: measureOne)

        Assay assayTwo = Assay.build()
        Experiment experimentTwo = Experiment.build(assay: assayTwo)
        AssayContext contextTwo = AssayContext.build(assay: assayTwo, contextName: "alpha2")
        AssayContextItem.build(assayContext: contextTwo)
        ExperimentMeasure measureTwo = ExperimentMeasure.build(experiment: experimentTwo)
        AssayContextExperimentMeasure.build(assayContext: contextTwo, experimentMeasure: measureTwo)

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

    void "test update owner role"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName20', designedBy: "BARD", capPermissionService: Mock(CapPermissionService), ownerRole: Role.build())
        final Role role = Role.build(authority: "ROLE_TEMA_ZZ")
        when:
        final Assay updatedAssay = service.updateOwnerRole(assay.id, role)
        then:
        assert role == updatedAssay.ownerRole
    }

    void "test update assay name"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName20', assayStatus: Status.DRAFT, capPermissionService: Mock(CapPermissionService))
        final String newAssayName = "New Assay Name"
        when:
        final Assay updatedAssay = service.updateAssayName(assay.id, newAssayName)
        then:
        assert newAssayName == updatedAssay.assayName
    }

    void "test update assay status #desc "() {
        service.springSecurityService = Mock(SpringSecurityService)

        given:

        final AssayContext assayContext = AssayContext.build()
        if (itemMap) {
            itemMap.put('assayContext', assayContext)
            final AssayContextItem assayContextItem = AssayContextItem.build(itemMap)
        }
        Person.build(userName: username)

        when:
        final Assay updatedAssay = service.updateAssayStatus(assayContext.assay.id, updatedStatus)

        then:
        service.springSecurityService.getAuthentication() >> new UsernamePasswordAuthenticationToken(new BardUser(username: username), null)

        updatedAssay.assayStatus == expectedStatus
        updatedAssay.approvedBy == expectedApprovedBy.call()
        (updatedAssay.approvedDate == null) == expectedApprovedByDateNull

        TestUtils.assertFieldValidationExpectations(updatedAssay, 'assayStatus', valid, errorCode)

        where:
        desc                                         | updatedStatus | username | itemMap                                                          | expectedStatus |expectedApprovedBy            | expectedApprovedByDateNull |valid | errorCode
        'valid DRAFT with null contextItem values'   | DRAFT         | 'foo'    | [attributeType: Fixed, valueType: NONE, valueDisplay: null]      | DRAFT          |{null}                        | true                       |true | null
        'valid RETIRED with null contextItem values' | RETIRED       | 'foo'    | [attributeType: Fixed, valueType: NONE, valueDisplay: null]      | RETIRED        |{null}                        | true                       |true | null
        'invalid APPROVED with null item values'     | APPROVED      | 'foo'    | [attributeType: Fixed, valueType: NONE, valueDisplay: null]      | APPROVED       |{null}                        | true                       |false | 'assay.assayStatus.requires.items.with.values'
        'valid APPROVED with non null item values'   | APPROVED      | 'foo'    | [attributeType: Fixed, valueType: NONE, valueDisplay: 'someVal'] | APPROVED       |{Person.findByUserName('foo')}| false                      |true | null
        'valid APPROVED with no items'               | APPROVED      | 'foo'    | [:]                                                              | APPROVED       |{Person.findByUserName('foo')}| false                      |true | null
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
