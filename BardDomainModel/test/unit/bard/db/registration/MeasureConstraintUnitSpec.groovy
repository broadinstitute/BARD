package bard.db.registration

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.Measure.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import static bard.db.experiment.ExperimentMeasure.PARENT_CHILD_RELATIONSHIP_MAX_SIZE
import static bard.db.experiment.ExperimentMeasure.PARENT_CHILD_RELATIONSHIP_MAX_SIZE

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/9/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Element, Measure])
@Mock([Assay, Element, Measure])
@Unroll
class MeasureConstraintUnitSpec extends Specification {

    Measure domainInstance

    @Before
    void doSetup() {
        domainInstance = Measure.buildWithoutSave(assay: Assay.build(), resultType: Element.build())
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

    void "test displayLabel logic"() {
        when:
        domainInstance.resultType = Element.build(label: resultTypeLabel)
        domainInstance.statsModifier = statsModifier.call()

        then:
        domainInstance.displayLabel == displayLabel

        where:
        desc                   | resultTypeLabel   | statsModifier                                  | displayLabel
        'just resultTypeLabel' | 'resultTypeLabel' | { null }                                       | 'resultTypeLabel'
        'both labels'          | 'resultTypeLabel' | { Element.build(label: 'statsModifierLabel') } | 'resultTypeLabel (statsModifierLabel)'

    }

    void "test getChildrenSorted by displayLabel case insensitive input:#input expected: #expected"() {
        when:
        for (String label in input) {
            domainInstance.addToChildMeasures(Measure.build(resultType: Element.build(label: label),
                    assay: domainInstance.assay))
        }

        then:
        domainInstance.childrenMeasuresSorted*.displayLabel == expected

        where:
        input           | expected
        ['b', 'a']      | ['a', 'b']
        ['B', 'a']      | ['a', 'B']
        ['c', 'B', 'a'] | ['a', 'B', 'c']
    }
}