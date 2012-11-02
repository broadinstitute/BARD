package bard.db.experiment

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.experiment.ExperimentContext.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import spock.lang.Shared

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/17/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Experiment,ExperimentContext])
@Unroll
class ExperimentContextConstraintUnitSpec extends Specification {

    ExperimentContext domainInstance

    @Shared Experiment validExperiment

    @Before
    void doSetup() {
        domainInstance = ExperimentContext.buildWithoutSave()
        validExperiment = Experiment.build()
    }

    void "test experiment constraints #desc experiment: '#valueUnderTest'"() {
        final String field = 'experiment'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        println("field : $field")
        println("valueUnderTest : $valueUnderTest")
        println("validExperiment : $validExperiment")
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest  | valid | errorCode
        'null not valid'   | null            | false | 'nullable'
//        'valid experiment' | validExperiment | true  | null
        // TODO valueUnderTest is null for the @Shared validExperiment

    }

    void "test contextName constraints #desc contextName: '#valueUnderTest'"() {
        final String field = 'contextName'

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
        desc               | valueUnderTest                          | valid | errorCode
        'blank not valid'  | ''                                      | false | 'blank'
        'blank not valid'  | '   '                                   | false | 'blank'
        'too long'         | createString(CONTEXT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(CONTEXT_NAME_MAX_SIZE)     | true  | null
        'null valid'       | null                                    | true  | null
    }

    void "test contextGroup constraints #desc contextGroup: '#valueUnderTest'"() {
        final String field = 'contextGroup'

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
        desc               | valueUnderTest                           | valid | errorCode
        'blank not valid'  | ''                                       | false | 'blank'
        'blank not valid'  | '   '                                    | false | 'blank'
        'too long'         | createString(CONTEXT_GROUP_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(CONTEXT_GROUP_MAX_SIZE)     | true  | null
        'null valid'       | null                                     | true  | null
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