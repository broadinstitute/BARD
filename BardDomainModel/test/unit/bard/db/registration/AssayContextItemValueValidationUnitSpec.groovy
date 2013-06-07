package bard.db.registration

import bard.db.dictionary.Element
import bard.db.model.StandardContextItemValueValidationUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.enums.ExpectedValueType.*
import static bard.db.registration.AttributeType.List as ListAttrType
import static bard.db.registration.AttributeType.Free
import static bard.db.registration.AttributeType.Range as RangeAttrType
import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/12/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayContextItem, Element])
@Unroll
class AssayContextItemValueValidationUnitSpec extends StandardContextItemValueValidationUnitSpec<AssayContextItem> {


    @Before
    void doSetup() {
        domainInstance = AssayContextItem.buildWithoutSave()
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
    void "test validation with #attributeElementMap attributeType:#attributeType: #desc"() {

        given:
        Element attributeElement = optionallyCreateElement(attributeElementMap)
        Element valueElement = optionallyCreateElement(valueElementMap)

        domainInstance.attributeType = attributeType
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

        // attributeType Fixed covered in generic test case
        where:
        desc                                       | attributeElementMap            | attributeType | valueElementMap | valueMap                                                                                                             | expectedValid | globalErrorsCodes                                                     | fieldErrorCodes
        'valid with only valueDisplay'             | [expectedValueType: FREE_TEXT] | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid with valueDisplay but valueElement' | [expectedValueType: FREE_TEXT] | ListAttrType  | [label: 't']    | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid with valueDisplay but extValueId'   | [expectedValueType: FREE_TEXT] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid with valueDisplay but qualifier'    | [expectedValueType: FREE_TEXT] | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid with valueDisplay but valueNum'     | [expectedValueType: FREE_TEXT] | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid with valueDisplay but valueMin'     | [expectedValueType: FREE_TEXT] | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid with valueDisplay but valueMax'     | [expectedValueType: FREE_TEXT] | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid with only Range'                    | [expectedValueType: NUMERIC]   | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']     | true          | []                                                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]


    }

    private Element optionallyCreateElement(Map map) {
        if (map != null) {
            return Element.build(map)
        }
    }

}
