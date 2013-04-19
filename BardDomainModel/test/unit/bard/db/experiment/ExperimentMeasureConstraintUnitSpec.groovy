package bard.db.experiment

import bard.db.registration.Assay
import bard.db.registration.Measure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.experiment.ExperimentMeasure.MODIFIED_BY_MAX_SIZE
import static bard.db.experiment.ExperimentMeasure.PARENT_CHILD_RELATIONSHIP_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Experiment, ExperimentMeasure, Measure])
@Mock([Assay, Experiment, ExperimentMeasure, Measure])
@Unroll
class ExperimentMeasureConstraintUnitSpec extends Specification {

    ExperimentMeasure domainInstance

    @Before
    void doSetup() {
        domainInstance = ExperimentMeasure.buildWithoutSave()
    }

    void "test parent constraints #desc parent: '#valueUnderTest'"() {

        final String field = 'parent'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc           | valueUnderTest                | valid | errorCode
        'null valid'   | { null }                      | true  | null
        'valid parent' | { ExperimentMeasure.build() } | true  | null

    }

    void "test parentChildRelationship constraints #desc parentChildRelationship: '#valueUnderTest'"() {

        final String field = 'parentChildRelationship'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest                                       | valid | errorCode
        'too long'    | createString(PARENT_CHILD_RELATIONSHIP_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid' | ''                                                   | false | 'blank'
        'blank valid' | '  '                                                 | false | 'blank'
        'not inList'  | createString(PARENT_CHILD_RELATIONSHIP_MAX_SIZE)     | false | 'not.inList'


        'null valid'  | null                                                 | true  | null
        'valid value' | 'is calculated from'                                 | true  | null
        'valid value' | 'is related to'                                      | true  | null
    }

    void "test experiment constraints #desc experiment: '#valueUnderTest'"() {

        final String field = 'experiment'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest         | valid | errorCode
        'null not valid'   | { null }               | false | 'nullable'
        'valid experiment' | { Experiment.build() } | true  | null

    }

    void "test measure constraints #desc measure: '#valueUnderTest'"() {

        final String field = 'measure'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest      | valid | errorCode
        'null not valid' | { null }            | false | 'nullable'
        'valid measure'  | { Measure.build() } | true  | null

    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
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

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
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

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

}
