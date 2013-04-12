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

    void "test deriveDisplayValue  #desc"() {

        given:
        Element valueElement = optionallyCreateElement(valueElementMap)
        Element unitElement = optionallyCreateElement(unitElementMap)

        when:
        domainInstance.valueElement = valueElement
        domainInstance.qualifier = qualifier
        domainInstance.valueNum = valueNum
        domainInstance.attributeElement.unit = unitElement
        domainInstance.valueMin = valueMin
        domainInstance.valueMax = valueMax
        domainInstance.valueDisplay = 'someNonBlankValue'

        then:
        domainInstance.validate()
        domainInstance.deriveDisplayValue() == expectedDisplayValue

        where:
        desc                            | expectedDisplayValue | valueElementMap | qualifier | valueNum | unitElementMap         | valueMin | valueMax
        'all null'                      | null                 | null            | null      | null     | null                   | null     | null
        'only valueElement'             | 't'                  | [label: 't']    | null      | null     | null                   | null     | null
        'only valueNum'                 | '= 1.0'              | null            | '= '      | 1.0      | null                   | null     | null
        'qualifier and valueNum'        | '= 1.0'              | null            | '= '      | 1.0      | null                   | null     | null
        'qualifier, valueNum and units' | '= 1.0 abbr'         | null            | '= '      | 1.0      | [abbreviation: 'abbr'] | null     | null
        'range'                         | '5.0 - 6.0'          | null            | null      | null     | null                   | 5.0      | 6.0
    }

    /**
     * Business rules for validating a contextItem are pretty complicated
     * The value a contextItem holds can be held in 1 or more columns, but only certain combinations are valid and the
     * value of other fields particularly the attributeElement impact what state is valid
     * @see <a href="https://github.com/broadinstitute/BARD/wiki/Business-rules#general-business-rules-for-assay_context_item">general-business-rules-for-assay_context_item</a>
     *
     */
    void "test validation  #desc"() {

        given:
        Element valueElement = optionallyCreateElement(valueElementMap)
        Element unitElement = optionallyCreateElement(unitElementMap)
        domainInstance.valueDisplay = null // clearing any value from TestDataConfig via @Build for this test

        domainInstance.attributeElement.externalURL = attributeExternalUrl
        domainInstance.valueElement = valueElement
        domainInstance.extValueId = valueMap.extValueId
        domainInstance.qualifier = valueMap.qualifier
        domainInstance.valueNum = valueMap.valueNum
        domainInstance.attributeElement.unit = unitElement
        domainInstance.valueMin = valueMap.valueMin
        domainInstance.valueMax = valueMap.valueMax
        domainInstance.valueDisplay = valueMap.valueDisplay

        when:
        domainInstance.validate()

        then: 'verify field errors'
        for (entry in fieldErrorCodes) {
            assertFieldValidationExpectations(domainInstance, entry.key, expectedValid, entry.value)
        }
        and: 'verify global errors'
        domainInstance.errors?.getGlobalErrors()?.code == globalErrorsCodes


        where:
        desc                                                                      | attributeExternalUrl | valueElementMap | unitElementMap         | valueMap                                                                                                             | expectedValid | globalErrorsCodes                                     | fieldErrorCodes
        'valid text value with only valueDisplay'                                 | null                 | null            | null                   | [valueDisplay: 'someDisplay']                                                                                        | true          | []                                                    | [:]
        'invalid text value with null valueDisplay'                               | null                 | null            | null                   | [valueDisplay: null]                                                                                                 | false         | []                                                    | [valueDisplay: 'contextItem.valueDisplay.null']
        'invalid text value with blank valueDisplay'                              | null                 | null            | null                   | [valueDisplay: '']                                                                                                   | false         | []                                                    | [valueDisplay: 'blank']
        'invalid text value with blank valueDisplay'                              | null                 | null            | null                   | [valueDisplay: ' ']                                                                                                  | false         | []                                                    | [valueDisplay: 'blank']

        'valid text value but but valueElement not null '                         | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null]     | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null]
        'valid text value but but extValueId not null '                           | null                 | null            | null                   | [valueDisplay: 'someDisplay', extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null] | false         | []                                                    | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null]
        'valid text value but qualifier not null'                                 | null                 | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null]     | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null]
        'valid text value but but valueNum not null '                             | null                 | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null]      | false         | ['contextItem.valueNum.required.fields']              | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: null, valueMin: null, valueMax: null]
        'valid text value but but valueMin not null '                             | null                 | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null]      | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.null']
        'valid text value but but valueMax not null '                             | null                 | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0]      | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.null', valueMax: null]

        'valid externalOntology reference externalUrl'                            | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay']                                                                  | true          | []                                                    | [extValueId: null, valueDisplay: null]
        'invalid externalOntology reference both blank'                           | 'http://bard.org'    | null            | null                   | [:]                                                                                                                  | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank', valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid externalOntology reference blank extValueId'                     | 'http://bard.org'    | null            | null                   | [extValueId: null, valueDisplay: 'someDisplay']                                                                      | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank']
        'invalid externalOntology reference blank display'                        | 'http://bard.org'    | null            | null                   | [extValueId: 'someId']                                                                                               | false         | ['contextItem.attribute.externalURL.required.fields'] | [valueDisplay: 'contextItem.valueDisplay.blank']

        'valid externalOntology reference but valueElement not null'              | 'http://bard.org'    | [label: 'foo']  | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay']                                                                  | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueDisplay: null, valueElement: 'contextItem.valueElement.not.null']
        'valid externalOntology reference but qualifier not null'                 | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', qualifier: '= ']                                                 | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueDisplay: null, qualifier: 'contextItem.qualifier.not.null']
        'valid externalOntology reference but valueNum not null'                  | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueNum: 2]                                                     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueDisplay: null, valueNum: 'contextItem.valueNum.not.null']
        'valid externalOntology reference but valueMin not null'                  | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMin: 1]                                                     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueDisplay: null, valueMin: 'contextItem.valueMin.not.null']
        'valid externalOntology reference but valueMax not null'                  | 'http://bard.org'    | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMax: 2]                                                     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueDisplay: null, valueMax: 'contextItem.valueMax.not.null']

        'valid numeric valueNum with attributeWithUnit'                           | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0]                                                                                     | true          | []                                                    | [qualifier: null, valueNum: null, valueMin: null, valueMax: null]

        'valid numeric valueNum but attributeWithUnit qualifier null'             | null                 | null            | [abbreviation: 'abbr'] | [qualifier: null, valueNum: 2.0]                                                                                     | false         | ['contextItem.valueNum.required.fields']              | [qualifier: 'contextItem.qualifier.blank', valueNum: null, valueMin: null, valueMax: null]
        'valid numeric valueNum but attributeWithUnit but extValueId not null '   | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, extValueId: 'someId']                                                               | false         | ['contextItem.valueNum.required.fields']              | [extValueId: 'contextItem.extValueId.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null]
        'valid numeric valueNum but attributeWithUnit but valueElement not null ' | null                 | [label: 'foo']  | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0]                                                                                     | false         | ['contextItem.valueNum.required.fields']              | [valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null]
        'valid numeric valueNum but attributeWithUnit but valueMin not null '     | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, valueMin: 1.0]                                                                      | false         | ['contextItem.valueNum.required.fields']              | [qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null]
        'valid numeric valueNum but attributeWithUnit but valueMax not null '     | null                 | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, valueMax: 3.0]                                                                      | false         | ['contextItem.valueNum.required.fields']              | [qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null']

        'valid numeric range'                                                     | null                 | null            | [:]                    | [qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0]                                                      | true          | []                                                    | [qualifier: null, valueNum: null, valueMin: null, valueMax: null]
        'invalid numeric range null valueMin'                                     | null                 | null            | [:]                    | [qualifier: null, valueNum: null, valueMin: null, valueMax: 2.0]                                                     | false         | ['contextItem.range.required.fields']                 | [qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.null', valueMax: null]
        'invalid numeric range null valueMax'                                     | null                 | null            | [:]                    | [qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null]                                                     | false         | ['contextItem.range.required.fields']                 | [qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.null']
        'invalid numeric range valueMin > valueMax'                               | null                 | null            | [:]                    | [qualifier: null, valueNum: null, valueMin: 2.0, valueMax: 1.0]                                                      | false         | []                                                    | [qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.less.than.valueMax', valueMax: 'contextItem.valueMax.not.greater.than.valueMin']
        'invalid numeric range valueMin = valueMax'                               | null                 | null            | [:]                    | [qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 1.0]                                                      | false         | []                                                    | [qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.less.than.valueMax', valueMax: 'contextItem.valueMax.not.greater.than.valueMin']
        'valid numeric range but with qualifier'                                  | null                 | null            | [:]                    | [qualifier: '= ', valueNum: null, valueMin: 1.0, valueMax: 2.0]                                                      | false         | ['contextItem.range.required.fields']                 | [qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null]
        'valid numeric range but with extValueId'                                 | null                 | null            | [:]                    | [qualifier: null, extValueId: 'extValueId', valueMin: 1.0, valueMax: 2.0]                                            | false         | ['contextItem.range.required.fields']                 | [qualifier: null, extValueId: 'contextItem.extValueId.not.null', valueMin: null, valueMax: null]

        'valid dictionary reference'                                              | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay']                                                                                        | true          | []                                                    | [:]
        'invalid dictionary reference value blank display'                        | null                 | [label: 'foo']  | null                   | [:]                                                                                                                  | false         | []                                                    | [valueDisplay: 'contextItem.valueDisplay.blank']

        'valid dictionary reference value but extValueId not null'                | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', extValueId: 'someId']                                                                  | false         | []                                                    | [valueDisplay: null, extValueId: 'contextItem.extValueId.not.null']
        'valid dictionary reference value but qualifier not null'                 | null                 | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', qualifier: '= ']                                                                       | false         | []                                                    | [valueDisplay: null, qualifier: 'contextItem.qualifier.not.null']
    }

    private Element optionallyCreateElement(Map map) {
        if (map != null) {
            return Element.build(map)
        }
    }

}
