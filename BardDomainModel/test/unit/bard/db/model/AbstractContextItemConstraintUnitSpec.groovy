package bard.db.model

import bard.db.dictionary.Element
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.model.AbstractContextItem.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractContextItemConstraintUnitSpec<T extends AbstractContextItem> extends Specification {

    T domainInstance

    @Before
    abstract void doSetup()

    void "test attributeElement constraints #desc"() {
        final String field = 'attributeElement'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                     | valueUnderTest      | valid | errorCode
        'null is not valid'      | { null }            | false | 'nullable'
        'valid attributeElement' | { Element.build() } | true  | null

    }

    void "test valueElement constraints #desc"() {

        final String field = 'valueElement'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                 | valueUnderTest      | valid | errorCode
        'null is valid'      | { null }            | true  | null
        'valid valueElement' | { Element.build() } | true  | null
    }

    void "test extValueId constraints #desc extValueId: '#valueUnderTest'"() {


        given:
        final String field = 'extValueId'
        domainInstance.attributeElement.externalURL = 'http://bard.org' // if the attribute has an eternalUrl this is an externalOntology reference
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                          | valid | errorCode
        'too long'         | createString(EXT_VALUE_ID_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                      | false | 'contextItem.extValueId.blank'
        'blank valid'      | '  '                                    | false | 'contextItem.extValueId.blank'
        'null valid'       | null                                    | false | 'contextItem.extValueId.blank'

        'exactly at limit' | createString(EXT_VALUE_ID_MAX_SIZE)     | true  | null
    }

    void "test qualifier constraints #desc qualifier: '#valueUnderTest'"() {

        final String field = 'qualifier'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.valueNum = valueNum
        domainInstance.attributeElement.unit = unit.call()
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                   | valueUnderTest | valueNum | unit                | valid | errorCode
        'blank'                | ''             | 1.0      | { Element.build() } | false | 'contextItem.qualifier.blank'
        'blank'                | '  '           | 1.0      | { Element.build() } | false | 'contextItem.qualifier.blank'
        'bad val'              | 'aa'           | 1.0      | { Element.build() } | false | 'not.inList'

        'equals'               | '= '           | 1.0      | { Element.build() } | true  | null
        'less than'            | '< '           | 1.0      | { Element.build() } | true  | null
        'less than or equal'   | '<='           | 1.0      | { Element.build() } | true  | null
        'greter than'          | '> '           | 1.0      | { Element.build() } | true  | null
        'greter than or equal' | '>='           | 1.0      | { Element.build() } | true  | null
        '<<'                   | '<<'           | 1.0      | { Element.build() } | true  | null
        '>>'                   | '>>'           | 1.0      | { Element.build() } | true  | null
        '~ '                   | '~ '           | 1.0      | { Element.build() } | true  | null

        'null'                 | null           | 1.0      | { Element.build() } | false | 'contextItem.qualifier.blank'
        'null'                 | null           | null     | {}                  | true  | null

    }

    //TODO value num, value min, value max
    void "test valueDisplay constraints #desc valueDisplay: '#valueUnderTest'"() {

        final String field = 'valueDisplay'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                           | valid | errorCode
        'too long'         | createString(VALUE_DISPLAY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                       | false | 'blank'
        'blank valid'      | '  '                                     | false | 'blank'
        'null valid'       | null                                     | false | 'contextItem.valueDisplay.null'

        'exactly at limit' | createString(VALUE_DISPLAY_MAX_SIZE)     | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
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

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

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

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

}
