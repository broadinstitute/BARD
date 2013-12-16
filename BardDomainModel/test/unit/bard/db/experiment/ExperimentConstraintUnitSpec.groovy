package bard.db.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.enums.Status
import bard.db.people.Role
import bard.db.registration.Assay
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.experiment.Experiment.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Experiment, Role, ExperimentMeasure])
@Mock([Assay, Experiment, Role, ExperimentMeasure])
@Unroll
class ExperimentConstraintUnitSpec extends Specification {


    Experiment domainInstance

    @Shared Assay validAssay

    @Before
    void doSetup() {
        domainInstance = Experiment.buildWithoutSave()
        validAssay = Assay.build()
    }

    void "test measuresHaveAtLeastOnePriorityElement #desc"() {
        given:
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(priorityElement: priorityElement)
        Experiment experiment = Experiment.build(experimentMeasures: [experimentMeasure] as Set<ExperimentMeasure>)
        when:
        boolean found = experiment.measuresHaveAtLeastOnePriorityElement()
        then:
        assert expected == found
        where:
        desc                      | priorityElement | expected
        "Has no Priority Element" | false           | false
        "Has Priority Element"    | true            | true
    }

    void "test experimentName constraints #desc experimentName: '#valueUnderTest'"() {
        final String field = 'experimentName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                             | valid | errorCode
        'null '            | null                                       | false | 'nullable'
        'blank value'      | ''                                         | false | 'blank'
        'blank value'      | '  '                                       | false | 'blank'
        'too long'         | createString(EXPERIMENT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(EXPERIMENT_NAME_MAX_SIZE)     | true  | null
    }

    void "test experimentStatus constraints #desc experimentStatus: '#valueUnderTest'"() {
        final String field = 'experimentStatus'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest            | valid | errorCode
        'null'        | null                      | false | 'nullable'

        'valid value' | Status.DRAFT    | true  | null
        'valid value' | Status.APPROVED | true  | null
        'valid value' | Status.RETIRED  | true  | null
    }

    void "test readyForExtraction constraints #desc readyForExtraction: '#valueUnderTest'"() {
        final String field = 'readyForExtraction'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest               | valid | errorCode
        'null not valid' | null                         | false | 'nullable'

        'valid value'    | ReadyForExtraction.NOT_READY | true  | null
        'valid value'    | ReadyForExtraction.READY     | true  | null
        'valid value'    | ReadyForExtraction.STARTED   | true  | null
        'valid value'    | ReadyForExtraction.COMPLETE  | true  | null
    }

    void "test assay constraints #desc assay: '#valueUnderTest'"() {
        final String field = 'assay'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'valid assay'    | validAssay     | true  | null
    }

    void "test runDateFrom constraints #desc runDateFrom: '#valueUnderTest'"() {
        final String field = 'runDateFrom'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

    void "test runDateTo constraints #desc runDateTo: '#valueUnderTest'"() {
        final String field = 'runDateTo'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

    void "test holdUntilDate constraints #desc holdUntilDate: '#valueUnderTest'"() {
        final String field = 'holdUntilDate'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

    void "test description constraints #desc description: '#valueUnderTest'"() {
        final String field = 'description'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'blank value'      | ''                                     | false | 'blank'
        'blank value'      | '  '                                   | false | 'blank'
        'too long'         | createString(DESCRIPTION_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(DESCRIPTION_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                     | false | 'blank'
        'blank valid'      | '  '                                   | false | 'blank'

        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test dateCreated constraints #desc dateCreated: '#valueUnderTest'"() {
        final String field = 'dateCreated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test ownerRole constraints #desc ownerRole: '#valueUnderTest'"() {
        final String field = 'ownerRole'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest   | valid | errorCode
        'null not valid'   | { null }         | false | 'nullable'
        'owner role valid' | { Role.build() } | true  | null
    }

}
