package bard.db.dictionary

import bard.db.BardIntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.dictionary.UnitConversion.FORMULA_MAX_SIZE
import static bard.db.dictionary.UnitConversion.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/27/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class UnitConversionConstraintIntegrationSpec extends BardIntegrationSpec {

    UnitConversion domainInstance

    @Before
    void doSetup() {
        domainInstance = UnitConversion.buildWithoutSave()
        domainInstance.fromUnit.save()
        domainInstance.toUnit.save()
    }

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance.save(flush: true)
        }
    }

    void "test fromUnit constraints #desc fromUnit: '#valueUnderTest'"() {

        final String field = 'fromUnit'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest      | valid | errorCode
        'null not valid' | { null }            | false | 'nullable'
        'valid fromUnit' | { Element.build() } | true  | null

    }

    void "test toUnit constraints #desc toUnit: '#valueUnderTest'"() {

        final String field = 'toUnit'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest      | valid | errorCode
        'null not valid' | { null }            | false | 'nullable'
        'valid toUnit'   | { Element.build() } | true  | null

    }

    void "test multiplier constraints #desc multiplier: '#valueUnderTest'"() {

        final String field = 'multiplier'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc              | valueUnderTest | valid | errorCode
        'null valid'      | null           | true  | null
        'negative number' | -1             | true  | null
        'zero'            | 0              | true  | null
        'float'           | 1.1            | true  | null
    }

    void "test offset constraints #desc offset: '#valueUnderTest'"() {

        final String field = 'offset'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc              | valueUnderTest | valid | errorCode
        'null valid'      | null           | true  | null
        'negative number' | -1             | true  | null
        'zero'            | 0              | true  | null
        'float'           | 1.1            | true  | null
    }

    void "test formula constraints #desc formula: '#valueUnderTest'"() {

        final String field = 'formula'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                     | valid | errorCode
        'too long'         | createString(FORMULA_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                 | false | 'blank'
        'blank valid'      | '  '                               | false | 'blank'

        'exactly at limit' | createString(FORMULA_MAX_SIZE)     | true  | null
        'null valid'       | null                               | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

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

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}