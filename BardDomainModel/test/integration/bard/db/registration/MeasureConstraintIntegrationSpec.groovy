package bard.db.registration

import bard.db.dictionary.Element
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before

import static bard.db.registration.Measure.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/13/12
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class MeasureConstraintIntegrationSpec extends IntegrationSpec {

    def domainInstance

    @Before
    void doSetup() {
        domainInstance = buildWithoutSaveMeasure()
    }

    private Measure buildWithoutSaveMeasure() {
        Measure.buildWithoutSave(assay: Assay.build(), resultTypeElement: Element.build())
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
        desc             | valueUnderTest  | valid | errorCode
        'null not valid' | {null}          | false | 'nullable'
        'valid assay'    | {Assay.build()} | true  | null

    }

    void "test resultTypeElement constraints #desc resultTypeElement: '#valueUnderTest'"() {

        final String field = 'resultTypeElement'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                      | valueUnderTest    | valid | errorCode
        'null not valid'          | {null}            | false | 'nullable'
        'valid resultTypeElement' | {Element.build()} | true  | null

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

    void "test childMeasures cascade delete "() {
        final String field = 'lastUpdated'

        when:
        domainInstance.addToChildMeasures(buildWithoutSaveMeasure())

        then:
        domainInstance.childMeasures.first().parentMeasure == domainInstance
    }
}
