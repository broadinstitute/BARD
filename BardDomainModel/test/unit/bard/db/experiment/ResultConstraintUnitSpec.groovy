package bard.db.experiment

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.experiment.Result.MODIFIED_BY_MAX_SIZE
import static bard.db.experiment.Result.VALUE_DISPLAY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/14/12
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@Build([Result])
class ResultConstraintUnitSpec extends Specification {

    def domainInstance

    @Before
    void doSetup() {
        domainInstance = Result.buildWithoutSave()
    }

    void "test experiment constraints #desc experiment: '#valueUnderTest'"() {

        final String field = 'experiment'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest       | valid | errorCode
        'null not valid'   | {null}               | false | 'nullable'
        'valid experiment' | {Experiment.build()} | true  | null

    }

    void "test resultType constraints #desc resultType: '#valueUnderTest'"() {

        final String field = 'resultType'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest    | valid | errorCode
        'null not valid'   | {null}            | false | 'nullable'
        'valid resultType' | {Element.build()} | true  | null

    }

    void "test substance constraints #desc substance: '#valueUnderTest'"() {

        final String field = 'substance'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc              | valueUnderTest      | valid | errorCode
        'null not valid'  | {null}              | false | 'nullable'
        'valid substance' | {Substance.build()} | true  | null

    }

    void "test statsModifier constraints #desc statsModifier: '#valueUnderTest'"() {

        final String field = 'statsModifier'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                  | valueUnderTest    | valid | errorCode
        'null valid'          | {null}            | true  | null
        'valid statsModifier' | {Element.build()} | true  | null

    }

    void "test replicateNumber constraints #desc replicateNumber: '#valueUnderTest'"() {

        final String field = 'replicateNumber'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                    | valueUnderTest | valid | errorCode
        'null valid'            | null           | true  | null
        'valid replicateNumber' | 1              | true  | null

        'zero ?'                | 0              | true  | null
        'negative ?'            | -1             | true  | null

    }

    void "test qualifier constraints #desc qualifier: '#valueUnderTest'"() {

        final String field = 'qualifier'

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
        desc                   | valueUnderTest | valid | errorCode
        'blank'                | ''             | false | 'blank'
        'blank'                | '  '           | false | 'blank'
        'bad val'              | 'aa'           | false | 'not.inList'

        'equals'               | '= '           | true  | null
        'less than'            | '< '           | true  | null
        'less than or equal'   | '<='           | true  | null
        'greter than'          | '> '           | true  | null
        'greter than or equal' | '>='           | true  | null
        '<<'                   | '<<'           | true  | null
        '>>'                   | '>>'           | true  | null
        '~ '                   | '~ '           | true  | null

        'null'                 | null           | true  | null

    }

    void "test valueDisplay constraints #desc valueDisplay: '#valueUnderTest'"() {

        final String field = 'valueDisplay'

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
        'too long'         | createString(VALUE_DISPLAY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                       | false | 'blank'
        'blank valid'      | '  '                                     | false | 'blank'

        'exactly at limit' | createString(VALUE_DISPLAY_MAX_SIZE)     | true  | null
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
