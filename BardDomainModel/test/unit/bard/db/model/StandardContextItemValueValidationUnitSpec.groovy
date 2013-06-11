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
        domainInstance.valueDisplay = valueDisplay

        then:
        domainInstance.validate()
        domainInstance.deriveDisplayValue() == expectedDisplayValue

        where:
        desc                            | expectedDisplayValue | attributeElementMap          | valueElementMap | qualifier | valueNum | unitElementMap         | valueMin | valueMax | valueDisplay
        'all null'                      | null                 | [expectedValueType: NONE]    | null            | null      | null     | null                   | null     | null     | null
        'only valueElement'             | 't'                  | [expectedValueType: ELEMENT] | [label: 't']    | null      | null     | null                   | null     | null     | 'some value'
        'qualifier and valueNum'        | '= 1.0'              | [expectedValueType: NUMERIC] | null            | '= '      | 1.0      | null                   | null     | null     | 'some value'
        'qualifier, valueNum and units' | '= 1.0 abbr'         | [expectedValueType: NUMERIC] | null            | '= '      | 1.0      | [abbreviation: 'abbr'] | null     | null     | 'some value'
        'range'                         | '5.0 - 6.0'          | [expectedValueType: NUMERIC] | null            | null      | null     | null                   | 5.0      | 6.0      | 'some value'
    }

    /**
     * Business rules for validating a contextItem are pretty complicated
     *
     * This is a beast of a test but an attempt at getting all the moving parts in one place
     *
     * Apologies for the wideness of the where block, I did find it useful to view that block over two monitors
     *
     * The value a contextItem holds can be held in 1 or more columns, but only certain combinations are valid and the
     * value of other fields particularly the attributeElement impact what state is valid
     * @see <a href="https://github.com/broadinstitute/BARD/wiki/Business-rules#general-business-rules-for-assay_context_item">general-business-rules-for-assay_context_item</a>
     *
     */
    void "test validation  #desc"() {

        given:
        Element attributeElement = optionallyCreateElement(attributeElementMap)
        Element valueElement = optionallyCreateElement(valueElementMap)

        domainInstance.attributeElement = attributeElement
        domainInstance.valueElement = valueElement
        domainInstance.extValueId = valueMap.extValueId
        domainInstance.qualifier = valueMap.qualifier
        domainInstance.valueNum = valueMap.valueNum
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
        desc                                                         | attributeElementMap                                        | valueElementMap | valueMap                                                                                                             | expectedValid | globalErrorsCodes                                                     | fieldErrorCodes
        'valid text value with only valueDisplay'                    | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid text value with null valueDisplay'                  | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid text value with blank valueDisplay'                 | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: '']                | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid text value with blank valueDisplay'                 | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: ' ']               | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid text value but valueElement not null '                | [expectedValueType: FREE_TEXT]                             | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but extValueId not null'                   | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but qualifier not null'                    | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueNum not null'                     | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueMin not null'                     | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid text value but valueMax not null'                     | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid externalOntology reference externalUrl'               | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference both blank'              | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: 'contextItem.extValueId.blank', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid externalOntology reference blank extValueId'        | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: 'contextItem.extValueId.blank', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference null display'            | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid externalOntology reference blank display'           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: '']            | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid externalOntology reference blank display'           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: ' ']           | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid externalOntology reference but valueElement not null' | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | [label: 'foo']  | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but qualifier not null'    | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueNum not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: 2, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']    | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueMin not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: 1, valueMax: null, valueDisplay: 'someDisplay']    | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueMax not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: 2, valueDisplay: 'someDisplay']    | false         | ['contextItem.attribute.externalURL.required.fields']                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid numeric valueNum with attributeWithUnit'              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum with valueNum 0'                     | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']        | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum with valueNum 0.0'                   | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 0.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid numeric valueNum but qualifier null'                  | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: null, valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueDisplay null'               | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: null]               | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'valid numeric valueNum but valueDisplay blank'              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: '']                 | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'valid numeric valueNum but valueDisplay blank'              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: ' ']                | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'valid numeric valueNum but extValueId not null '            | [expectedValueType: NUMERIC]                               | null            | [extValueId: 'someId', qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']  | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueElement not null '          | [expectedValueType: NUMERIC]                               | [label: 'foo']  | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueMin not null '              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']       | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueMax not null '              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: 3.0, valueDisplay: 'someDisplay']       | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'range not allowed for numeric for standard contextItem'     | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']       | false         | ['contextItem.valueNum.required.fields']                              | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: 'contextItem.valueNum.null', valueMin: 'contextItem.valueMin.not.null', valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid dictionary reference'                                 | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid dictionary reference value blank display'           | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.ELEMENT.required.fields']   | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid dictionary reference value but extValueId not null'   | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.expectedValueType.ELEMENT.required.fields']   | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid dictionary reference value but qualifier not null'    | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay',]    | false         | ['contextItem.attribute.expectedValueType.ELEMENT.required.fields']   | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid NONE state no values (default testing config) '       | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state extValueId not null '                    | [expectedValueType: NONE]                                  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueElement not null '                  | [expectedValueType: NONE]                                  | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state qualifier not null '                     | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueNum not null '                      | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: null]               | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueMin not null '                      | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: null]               | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'invalid NONE state valueMax not null '                      | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 2.0, valueDisplay: null]               | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]
        'invalid NONE state valueDisplay not null '                  | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']      | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.not.null']
    }

    Element optionallyCreateElement(Map map) {
        if (map != null) {
            return Element.build(map)
        }
    }
}
