package bard.db.model

import bard.db.dictionary.Element
import static bard.db.enums.ExpectedValueType.*
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/12/13
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class StandardContextItemValueValidationUnitSpec<T extends AbstractContextItem> extends Specification {

    T domainInstance

    @Before
    abstract void doSetup()

    void "test deriveDisplayValue  #desc"() {

        given:
        Element attributeElement = optionallyCreateElement(attributeElementMap)
        Element valueElement = optionallyCreateElement(valueElementMap)
        Element unitElement = optionallyCreateElement(unitElementMap)

        when:
        domainInstance.attributeElement = attributeElement
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
        desc                            | expectedDisplayValue | attributeElementMap          | valueElementMap | qualifier | valueNum | unitElementMap         | valueMin | valueMax
        'all null'                      | null                 | [:]                          | null            | null      | null     | null                   | null     | null
//        'only valueElement'             | 't'                  | [:]                          | [label: 't']    | null      | null     | null                   | null     | null
//        'only valueNum'                 | '= 1.0'              | [expectedValueType: NUMERIC] | null            | '= '      | 1.0      | null                   | null     | null
//        'qualifier and valueNum'        | '= 1.0'              | [expectedValueType: NUMERIC] | null            | '= '      | 1.0      | null                   | null     | null
//        'qualifier, valueNum and units' | '= 1.0 abbr'         | [expectedValueType: NUMERIC] | null            | '= '      | 1.0      | [abbreviation: 'abbr'] | null     | null
//        'range'                         | '5.0 - 6.0'          | [expectedValueType: NUMERIC] | null            | null      | null     | null                   | 5.0      | 6.0
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
        Element attributeElement = optionallyCreateElement(attributeElementMap)
        Element valueElement = optionallyCreateElement(valueElementMap)
        Element unitElement = optionallyCreateElement(unitElementMap)
        domainInstance.valueDisplay = null // clearing any value from TestDataConfig via @Build for this test

        domainInstance.attributeElement = attributeElement
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
        desc                                                                      | attributeElementMap  | valueElementMap | unitElementMap         | valueMap                                                                                                             | expectedValid | globalErrorsCodes                                     | fieldErrorCodes
        'valid text value with only valueDisplay'                                 | [:]                  | null            | null                   | [valueDisplay: 'someDisplay']                                                                                        | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid text value with null valueDisplay'                               | [:]                  | null            | null                   | [valueDisplay: null]                                                                                                 | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.null']
        'invalid text value with blank valueDisplay'                              | [:]                  | null            | null                   | [valueDisplay: '']                                                                                                   | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'blank']
        'invalid text value with blank valueDisplay'                              | [:]                  | null            | null                   | [valueDisplay: ' ']                                                                                                  | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'blank']

        'valid text value but valueElement not null '                             | [:]                  | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null]     | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but extValueId not null'                                | [:]                  | null            | null                   | [valueDisplay: 'someDisplay', extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null] | false         | []                                                    | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but qualifier not null'                                 | [:]                  | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null]     | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueNum not null'                                  | [:]                  | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null]      | false         | ['contextItem.valueNum.required.fields']              | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueMin not null'                                  | [:]                  | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null]      | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.null', valueDisplay: null]
        'valid text value but valueMax not null'                                  | [:]                  | null            | null                   | [valueDisplay: 'someDisplay', extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0]      | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.null', valueMax: null, valueDisplay: null]

        'valid externalOntology reference externalUrl'                            | [externalURL: 'url'] | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay']                                                                  | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference both blank'                           | [externalURL: 'url'] | null            | null                   | [:]                                                                                                                  | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid externalOntology reference blank extValueId'                     | [externalURL: 'url'] | null            | null                   | [extValueId: null, valueDisplay: 'someDisplay']                                                                      | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: 'contextItem.extValueId.blank', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference blank display'                        | [externalURL: 'url'] | null            | null                   | [extValueId: 'someId']                                                                                               | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid externalOntology reference but valueElement not null'              | [externalURL: 'url'] | [label: 'foo']  | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay']                                                                  | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but qualifier not null'                 | [externalURL: 'url'] | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', qualifier: '= ']                                                 | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueNum not null'                  | [externalURL: 'url'] | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueNum: 2]                                                     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueMin not null'                  | [externalURL: 'url'] | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMin: 1]                                                     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueMax not null'                  | [externalURL: 'url'] | null            | null                   | [extValueId: 'someId', valueDisplay: 'someDisplay', valueMax: 2]                                                     | false         | ['contextItem.attribute.externalURL.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid numeric valueNum with attributeWithUnit'                           | [:]                  | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0]                                                                                     | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum with valueNum 0'                                  | [:]                  | null            | null                   | [qualifier: '= ', valueNum: 0]                                                                                       | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum with valueNum 0.0'                                | [:]                  | null            | null                   | [qualifier: '= ', valueNum: 0.0]                                                                                     | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid numeric valueNum but attributeWithUnit qualifier null'             | [:]                  | null            | [abbreviation: 'abbr'] | [qualifier: null, valueNum: 2.0]                                                                                     | false         | ['contextItem.valueNum.required.fields']              | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but attributeWithUnit but extValueId not null '   | [:]                  | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, extValueId: 'someId']                                                               | false         | ['contextItem.valueNum.required.fields']              | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but attributeWithUnit but valueElement not null ' | [:]                  | [label: 'foo']  | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0]                                                                                     | false         | ['contextItem.valueNum.required.fields']              | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but attributeWithUnit but valueMin not null '     | [:]                  | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, valueMin: 1.0]                                                                      | false         | ['contextItem.valueNum.required.fields']              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid numeric valueNum but attributeWithUnit but valueMax not null '     | [:]                  | null            | [abbreviation: 'abbr'] | [qualifier: '= ', valueNum: 2.0, valueMax: 3.0]                                                                      | false         | ['contextItem.valueNum.required.fields']              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid numeric range'                                                     | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0]                                                      | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric range with valueMin 0 and valueMax null'                   | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: 0, valueMax: null]                                                       | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.null', valueDisplay: null]
        'valid numeric range with valueMax 0 and valueMin null'                   | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: null, valueMax: 0]                                                       | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.null', valueMax: null, valueDisplay: null]
        'invalid numeric range null valueMin'                                     | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: null, valueMax: 2.0]                                                     | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.null', valueMax: null, valueDisplay: null]
        'invalid numeric range null valueMax'                                     | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null]                                                     | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.null', valueDisplay: null]
        'invalid numeric range valueMin > valueMax'                               | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: 2.0, valueMax: 1.0]                                                      | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.less.than.valueMax', valueMax: 'contextItem.valueMax.not.greater.than.valueMin', valueDisplay: null]
        'invalid numeric range valueMin = valueMax'                               | [:]                  | null            | null                   | [qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 1.0]                                                      | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.less.than.valueMax', valueMax: 'contextItem.valueMax.not.greater.than.valueMin', valueDisplay: null]
        'valid numeric range but with qualifier'                                  | [:]                  | null            | null                   | [qualifier: '= ', valueNum: null, valueMin: 1.0, valueMax: 2.0]                                                      | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric range but with extValueId'                                 | [:]                  | null            | null                   | [qualifier: null, extValueId: 'extValueId', valueMin: 1.0, valueMax: 2.0]                                            | false         | ['contextItem.range.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, extValueId: 'contextItem.extValueId.not.null', valueMin: null, valueMax: null, valueDisplay: null]

        'valid dictionary reference'                                              | [:]                  | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay']                                                                                        | true          | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid dictionary reference value blank display'                        | [:]                  | [label: 'foo']  | null                   | [:]                                                                                                                  | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid dictionary reference value but extValueId not null'                | [:]                  | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', extValueId: 'someId']                                                                  | false         | []                                                    | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid dictionary reference value but qualifier not null'                 | [:]                  | [label: 'foo']  | null                   | [valueDisplay: 'someDisplay', qualifier: '= ']                                                                       | false         | []                                                    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
    }

    private Element optionallyCreateElement(Map map) {
        if (map != null) {
            return Element.build(map)
        }
    }
}
