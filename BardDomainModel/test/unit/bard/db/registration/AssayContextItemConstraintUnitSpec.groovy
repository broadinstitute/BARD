package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.ValueType
import bard.db.experiment.Experiment
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextItemConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.IgnoreRest
import spock.lang.Unroll

import static bard.db.enums.ExpectedValueType.*
import static bard.db.registration.AttributeType.Fixed
import static bard.db.registration.AttributeType.Free
import static bard.db.registration.AttributeType.List as ListAttrType
import static bard.db.registration.AttributeType.Range as RangeAttrType
import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([AssayContextItem, Element, Assay, AssayContext])
@Mock([AssayContextItem, Element, Assay, AssayContext])
@Unroll
class AssayContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec<AssayContextItem> {

    @Before
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    AssayContextItem constructInstance(Map props) {
        def instance = AssayContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError: true, flush: true)

        return instance
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
    void "test validation - validation turned off #desc"() {

        given:
        Element attributeElement = optionallyCreateElement(attributeElementMap)
        Element valueElement = optionallyCreateElement(valueElementMap)
        domainInstance.assayContext.assay.fullyValidateContextItems = false
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
        desc                                                         | attributeElementMap                                        | valueElementMap | valueMap                                                                                                             | expectedValid | globalErrorsCodes | fieldErrorCodes
        'valid text value with only valueDisplay'                    | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid text value with null valueDisplay'                  | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid text value with blank valueDisplay'                 | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: '']                | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid text value with blank valueDisplay'                 | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: ' ']               | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid text value but valueElement not null '                | [expectedValueType: FREE_TEXT]                             | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but extValueId not null'                   | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but qualifier not null'                    | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueNum not null'                     | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueMin not null'                     | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid text value but valueMax not null'                     | [expectedValueType: FREE_TEXT]                             | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid externalOntology reference externalUrl'               | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference both blank'              | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference blank extValueId'        | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference null display'            | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference blank display'           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: '']            | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid externalOntology reference blank display'           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: ' ']           | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid externalOntology reference but valueElement not null' | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | [label: 'foo']  | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but qualifier not null'    | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueNum not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: 2, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']    | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueMin not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: 1, valueMax: null, valueDisplay: 'someDisplay']    | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid externalOntology reference but valueMax not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: 2, valueDisplay: 'someDisplay']    | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid numeric valueNum with attributeWithUnit'              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum with valueNum 0'                     | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']        | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum with valueNum 0.0'                   | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 0.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid numeric valueNum but qualifier null'                  | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: null, valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueDisplay null'               | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: null]               | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueDisplay blank'              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: '']                 | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueDisplay blank'              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: ' ']                | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but extValueId not null '            | [expectedValueType: NUMERIC]                               | null            | [extValueId: 'someId', qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']  | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueElement not null '          | [expectedValueType: NUMERIC]                               | [label: 'foo']  | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueMin not null '              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']       | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid numeric valueNum but valueMax not null '              | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: 3.0, valueDisplay: 'someDisplay']       | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'range not allowed for numeric for standard contextItem'     | [expectedValueType: NUMERIC]                               | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']       | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid dictionary reference'                                 | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid dictionary reference value blank display'           | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid dictionary reference value but extValueId not null'   | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid dictionary reference value but qualifier not null'    | [expectedValueType: ELEMENT]                               | [label: 'foo']  | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid NONE state no values (default testing config) '       | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state extValueId not null '                    | [expectedValueType: NONE]                                  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueElement not null '                  | [expectedValueType: NONE]                                  | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state qualifier not null '                     | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueNum not null '                      | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: null]               | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueMin not null '                      | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: null]               | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueMax not null '                      | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 2.0, valueDisplay: null]               | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE state valueDisplay not null '                  | [expectedValueType: NONE]                                  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
    }

    void "test canDelete has No experiments #desc"() {
        given:
        domainInstance.attributeType = attributeType
        when:
        boolean canDelete = AssayContextItem.canDeleteContextItem(domainInstance)
        then:
        assert canDelete == valid

        where:
        desc                   | attributeType       | valid
        "Fixed Attribute Type" | AttributeType.Fixed | true
        "Free Attribute Type"  | AttributeType.Free  | true
        "List Attribute Type"  | AttributeType.List  | true
        "Range Attribute Type" | AttributeType.Range | true
    }

    void "test safeToDeleteContextItem #desc"() {
        given:
        final AssayContext ac = AssayContext.build()
        count.times {
            AssayContextItem.buildWithoutSave(assayContext: ac, attributeType: attributeType)
        }

        when:
        boolean safeToDelete = AssayContextItem.safeToDeleteContextItem(ac.assayContextItems.first())

        then:
        assert safeToDelete == valid

        where:
        desc                     | attributeType       | count | valid
        "Fixed Attribute Type"   | AttributeType.Fixed | 1     | true
        "1 Free Attribute Type"  | AttributeType.Free  | 1     | false
        "1 List Attribute Type"  | AttributeType.List  | 1     | false
        "1 Range Attribute Type" | AttributeType.Range | 1     | false


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
    void "test validation with #desc"() {

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
        desc                                                            | attributeElementMap                                        | attributeType | valueElementMap | valueMap                                                                                                             | expectedValid | globalErrorsCodes                                                        | fieldErrorCodes
        'invalid List Type & NONE not valid state'                      | [expectedValueType: NONE]                                  | Fixed         | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid dictionary reference'                                    | [expectedValueType: ELEMENT]                               | ListAttrType  | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid dictionary reference value blank display'              | [expectedValueType: ELEMENT]                               | ListAttrType  | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.ELEMENT.required.fields']      | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid dictionary reference value but qualifier not null'       | [expectedValueType: ELEMENT]                               | ListAttrType  | [label: 'foo']  | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay',]    | false         | ['contextItem.attribute.expectedValueType.ELEMENT.required.fields']      | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid dictionary reference value but extValueId not null'      | [expectedValueType: ELEMENT]                               | ListAttrType  | [label: 'foo']  | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.expectedValueType.ELEMENT.required.fields']      | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid List Type & EXTERNAL_ONTOLOGY'                           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid List Type & EXTERNAL_ONTOLOGY both blank'              | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: 'contextItem.extValueId.blank', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid List Type & EXTERNAL_ONTOLOGY blank extValueId'        | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: 'contextItem.extValueId.blank', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid List Type & EXTERNAL_ONTOLOGY null display'            | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid List Type & EXTERNAL_ONTOLOGY blank display'           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: '']            | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid List Type & EXTERNAL_ONTOLOGY blank display'           | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: ' ']           | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid List Type & EXTERNAL_ONTOLOGY but valueElement not null' | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | [label: 'foo']  | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & EXTERNAL_ONTOLOGY but qualifier not null'    | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & EXTERNAL_ONTOLOGY but valueNum not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: 2, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']    | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & EXTERNAL_ONTOLOGY but valueMin not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: 1, valueMax: null, valueDisplay: 'someDisplay']    | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid List Type & EXTERNAL_ONTOLOGY but valueMax not null'     | [externalURL: 'url', expectedValueType: EXTERNAL_ONTOLOGY] | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: 2, valueDisplay: 'someDisplay']    | false         | ['contextItem.attribute.externalURL.required.fields']                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid List Type & NUMERIC'                                     | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & NUMERIC with valueNum 0'                     | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']        | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & NUMERIC with valueNum 0.0'                   | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 0.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid List Type & NUMERIC but qualifier null'                  | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & NUMERIC but valueDisplay null'               | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: null]               | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'valid List Type & NUMERIC but valueDisplay blank'              | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: '']                 | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'valid List Type & NUMERIC but valueDisplay blank'              | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: ' ']                | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'valid List Type & NUMERIC but extValueId not null '            | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: 'someId', qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']  | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & NUMERIC but valueElement not null '          | [expectedValueType: NUMERIC]                               | ListAttrType  | [label: 'foo']  | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & NUMERIC but valueMin not null '              | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']       | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid List Type & NUMERIC but valueMax not null '              | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: 2.0, valueMin: null, valueMax: 3.0, valueDisplay: 'someDisplay']       | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'range not allowed for List Type & NUMERIC'                     | [expectedValueType: NUMERIC]                               | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']       | false         | ['contextItem.valueNum.required.fields']                                 | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.blank', valueNum: 'contextItem.valueNum.null', valueMin: 'contextItem.valueMin.not.null', valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid List Type & FREE_TEXT'                                   | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid List Type & FREE_TEXT with null valueDisplay'          | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid List Type & FREE_TEXT with blank valueDisplay'         | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: '']                | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid List Type & FREE_TEXT with blank valueDisplay'         | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: ' ']               | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']

        'valid List Type & FREE_TEXT but extValueId not null'           | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay'] | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & FREE_TEXT but valueElement not null '        | [expectedValueType: FREE_TEXT]                             | ListAttrType  | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & FREE_TEXT but qualifier not null'            | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & FREE_TEXT but valueNum not null'             | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid List Type & FREE_TEXT but valueMin not null'             | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid List Type & FREE_TEXT but valueMax not null'             | [expectedValueType: FREE_TEXT]                             | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0, valueDisplay: 'someDisplay']      | false         | ['contextItem.attribute.expectedValueType.FREE_TEXT.required.fields']    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]

        'valid NONE & LIST'                                             | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE & LIST extValueId not null '                      | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE & LIST valueElement not null '                    | [expectedValueType: NONE]                                  | ListAttrType  | [label: 'foo']  | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE & LIST qualifier not null '                       | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE & LIST valueNum not null '                        | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: null]               | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'invalid NONE & LIST valueMin not null '                        | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: null]               | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'invalid NONE & LIST valueMax not null '                        | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 2.0, valueDisplay: null]               | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]
        'invalid NONE & LIST valueDisplay not null '                    | [expectedValueType: NONE]                                  | ListAttrType  | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['contextItem.attribute.expectedValueType.NONE.required.fields']         | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.not.null']

        'invalid Range & ELEMENT           combo'                       | [expectedValueType: ELEMENT]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.invalid.attributeTypeAndAttributeExpectedValueCombo'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid Range & EXTERNAL_ONTOLOGY combo'                       | [expectedValueType: EXTERNAL_ONTOLOGY]                     | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.invalid.attributeTypeAndAttributeExpectedValueCombo'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid Range & FREE_TEXT combo'                               | [expectedValueType: FREE_TEXT]                             | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.invalid.attributeTypeAndAttributeExpectedValueCombo'] | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid Range Type & NUMERIC'                                    | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']       | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Range Type & NUMERIC but extValueId'                     | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']   | false         | ['contextItem.range.required.fields']                                    | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Range Type & NUMERIC but valueElement'                   | [expectedValueType: NUMERIC]                               | RangeAttrType | [label: 't']    | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']       | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Range Type & NUMERIC but scalar numeric'                 | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: '= ', valueNum: 1.0, valueMin: 1.0, valueMax: 2.0, valueDisplay: 'someDisplay']        | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]

        'invalid Range Type & NUMERIC null valueDisplay'                | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: null]                | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid Range Type & NUMERIC blank valueDisplay'               | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: '']                  | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid Range Type & NUMERIC blank valueDisplay'               | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: 2.0, valueDisplay: ' ']                 | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.blank']
        'invalid Range Type & NUMERIC null valueMin'                    | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 2.0, valueDisplay: 'someDisplay']      | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.null', valueMax: null, valueDisplay: null]
        'invalid Range Type & NUMERIC null valueMax'                    | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.null', valueDisplay: null]
        'invalid Range Type & NUMERIC scalar numeric'                   | [expectedValueType: NUMERIC]                               | RangeAttrType | null            | [extValueId: null, qualifier: '= ', valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']      | false         | ['contextItem.range.required.fields']                                    | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: 'contextItem.valueNum.not.null', valueMin: 'contextItem.valueMin.null', valueMax: 'contextItem.valueMax.null', valueDisplay: null]

        'invalid Free & ELEMENT combo'                                  | [expectedValueType: ELEMENT]                               | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'invalid Free & EXTERNAL_ONTOLOGY combo'                        | [expectedValueType: EXTERNAL_ONTOLOGY]                     | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]

        'valid Free Type & NUMERIC'                                     | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & NUMERIC but extValueId'                      | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & NUMERIC but valueElement'                    | [expectedValueType: NUMERIC]                               | Free          | [label: 't']    | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & NUMERIC but qualifier'                       | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & NUMERIC but valueNum'                        | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: null]               | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & NUMERIC but valueMin'                        | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: null]               | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid Free Type & NUMERIC but valueMax'                        | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0, valueDisplay: null]               | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]
        'valid Free Type & NUMERIC but valueDisplay'                    | [expectedValueType: NUMERIC]                               | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.not.null']

        'valid Free Type & FREE_TEXT'                                   | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | true          | []                                                                       | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & FREE_TEXT but extValueId'                    | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: 'someId', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]          | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: 'contextItem.extValueId.not.null', valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & FREE_TEXT but valueElement'                  | [expectedValueType: FREE_TEXT]                             | Free          | [label: 't']    | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: 'contextItem.valueElement.not.null', qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & FREE_TEXT but qualifier'                     | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: null, qualifier: '= ', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]              | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: 'contextItem.qualifier.not.null', valueNum: null, valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & FREE_TEXT but valueNum'                      | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: null, qualifier: null, valueNum: 1.0, valueMin: null, valueMax: null, valueDisplay: null]               | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: 'contextItem.valueNum.not.null', valueMin: null, valueMax: null, valueDisplay: null]
        'valid Free Type & FREE_TEXT but valueMin'                      | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: 1.0, valueMax: null, valueDisplay: null]               | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: 'contextItem.valueMin.not.null', valueMax: null, valueDisplay: null]
        'valid Free Type & FREE_TEXT but valueMax'                      | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 1.0, valueDisplay: null]               | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: 'contextItem.valueMax.not.null', valueDisplay: null]
        'valid Free Type & FREE_TEXT but valueDisplay'                  | [expectedValueType: FREE_TEXT]                             | Free          | null            | [extValueId: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'someDisplay']     | false         | ['assayContextItem.attributeType.free.required.fields']                  | [extValueId: null, valueElement: null, qualifier: null, valueNum: null, valueMin: null, valueMax: null, valueDisplay: 'contextItem.valueDisplay.not.null']
    }

    void "test attributeType constraints #desc attributeType: '#valueUnderTest'"() {

        final String field = 'attributeType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        RuntimeException e = thrown()
        e.message == 'Unknown attributeType: null'

        where:
        desc   | valueUnderTest
        'null' | null

    }

    void "test range display value"() {
        given:
        Element attributeElement = Element.build(expectedValueType: NUMERIC)
        Element unitElement = Element.build(abbreviation: 'abbr')
        AssayContextItem instance = AssayContextItem.build()
        domainInstance = instance

        when:
        instance.attributeElement = attributeElement
        instance.attributeElement.unit = unitElement
        instance.attributeType = AttributeType.Range
        instance.setRange(0, 100)

        then:
        instance.validate()
        instance.deriveDisplayValue() == "0.0 - 100.0 abbr"
    }
}
