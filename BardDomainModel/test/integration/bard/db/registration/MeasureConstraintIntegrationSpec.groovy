package bard.db.registration

import bard.db.BardIntegrationSpec
import bard.db.dictionary.Element
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.registration.Measure.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class MeasureConstraintIntegrationSpec extends BardIntegrationSpec {

    def domainInstance

    @Before
    void doSetup() {
        domainInstance = buildWithoutSaveMeasure()
    }

    private Measure buildWithoutSaveMeasure() {
        Measure.buildWithoutSave(assay: Assay.build(), resultType: Element.build())
    }

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance.save(flush: true)
        }
    }

    void "test assay constraints #desc assay: '#valueUnderTest'"() {

        final String field = 'assay'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest    | valid | errorCode
        'null not valid' | { null }          | false | 'nullable'
        'valid assay'    | { Assay.build() } | true  | null

    }

    void "test resultType constraints #desc resultType: '#valueUnderTest'"() {

        final String field = 'resultType'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest      | valid | errorCode
        'null not valid'   | { null }            | false | 'nullable'
        'valid resultType' | { Element.build() } | true  | null

    }

    void "test parentMeasure constraints #desc parentMeasure: '#valueUnderTest'"() {

        final String field = 'parentMeasure'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                  | valueUnderTest      | valid | errorCode
        'null valid'          | { null }            | true  | null
        'valid parentMeasure' | { Measure.build() } | true  | null

    }

    void "test entryUnit constraints #desc entryUnit: '#valueUnderTest'"() {

        final String field = 'entryUnit'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc              | valueUnderTest      | valid | errorCode
        'null valid'      | { null }            | true  | null
        'valid entryUnit' | { Element.build() } | true  | null

    }

    void "test statsModifier constraints #desc statsModifier: '#valueUnderTest'"() {

        final String field = 'statsModifier'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                  | valueUnderTest      | valid | errorCode
        'null valid'          | { null }            | true  | null
        'valid statsModifier' | { Element.build() } | true  | null

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
