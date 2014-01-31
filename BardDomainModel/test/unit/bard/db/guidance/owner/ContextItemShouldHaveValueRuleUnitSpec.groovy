package bard.db.guidance.owner

import bard.db.dictionary.Element
import bard.db.enums.ValueType
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.AttributeType.*
import static bard.db.registration.AttributeType.Range as AttrRange
import static bard.db.registration.AttributeType.List as AttrList

import static bard.db.enums.ValueType.*

import static bard.db.guidance.owner.ContextItemShouldHaveValueRule.*

/**
 * Created by ddurkin on 1/28/14.
 */
@Build([Assay, AssayContext, AssayContextItem, Element])
@Mock([Assay, AssayContext, AssayContextItem, Element])
@Unroll
class ContextItemShouldHaveValueRuleUnitSpec extends Specification {


    void "test assay level rule, #desc"() {
        given:
        TestDataConfigurationHolder.reset()
        final Assay assay = Assay.build()
        final AssayContext assayContext = AssayContext.build(assay: assay)

        itemMaps.each { itemMap ->
            itemMap.put('assayContext', assayContext)
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(itemMap)
        }

        when:

        final ContextItemShouldHaveValueRule contextItemShouldHaveValueRule = new ContextItemShouldHaveValueRule(assay)

        then:
        contextItemShouldHaveValueRule.guidance.message == expectedMessages

        where:
        desc                                  | itemMaps                                                                                                     | expectedMessages
        'all null values should have message' | [[attributeType: Fixed, valueDisplay: null]]                                                                 | [getErrorMsg('label1')]
        'all null values 2 messages'          | [[attributeType: Fixed, valueDisplay: null], [attributeType: Fixed, valueType: ELEMENT, valueDisplay: null]] | [getErrorMsg('label1'), getErrorMsg('label2')]
        'no msg for attributeType: Free'      | [[attributeType: Free, valueDisplay: null]]                                                                  | []
        'no msg for attributeType: AttrRange' | [[attributeType: AttrRange, valueDisplay: null]]                                                             | []
        'no msg for attributeType: AttrList'  | [[attributeType: AttrList, valueDisplay: null]]                                                              | []

        'extValueId not null; no message'     | [[attributeType: Fixed, extValueId: '123']]                                                                  | []
        'qualifier not null; no message'      | [[attributeType: Fixed, qualifier: ' =']]                                                                    | []
        'valueNum not null; no message'       | [[attributeType: Fixed, valueNum: 1]]                                                                        | []
        'valueMin not null; no message'       | [[attributeType: Fixed, valueMin: 1]]                                                                        | []
        'valueMax not null; no message'       | [[attributeType: Fixed, valueMax: 1]]                                                                        | []
        'qualifier not null; no message'      | [[attributeType: Fixed, qualifier: ' =']]                                                                    | []
        'valueDisplay not null; no message'   | [[attributeType: Fixed, valueDisplay: 'foo']]                                                                | []
    }

    void "test assayContext level rule, #desc"() {
        given:
        TestDataConfigurationHolder.reset()
        final Assay assay = Assay.build()
        final AssayContext assayContext = AssayContext.build(assay: assay)

        itemMaps.each { itemMap ->
            itemMap.put('assayContext', assayContext)
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(itemMap)
        }

        when:

        final ContextItemShouldHaveValueRule contextItemShouldHaveValueRule = new ContextItemShouldHaveValueRule(assayContext)

        then:
        contextItemShouldHaveValueRule.guidance.message == expectedMessages

        where:
        desc                                  | itemMaps                                                                                                     | expectedMessages
        'all null values should have message' | [[attributeType: Fixed, valueDisplay: null]]                                                                 | [getErrorMsg('label1')]
        'all null values 2 messages'          | [[attributeType: Fixed, valueDisplay: null], [attributeType: Fixed, valueType: ELEMENT, valueDisplay: null]] | [getErrorMsg('label1'), getErrorMsg('label2')]
        'no msg for attributeType: Free'      | [[attributeType: Free, valueDisplay: null]]                                                                  | []
        'no msg for attributeType: AttrRange' | [[attributeType: AttrRange, valueDisplay: null]]                                                             | []
        'no msg for attributeType: AttrList'  | [[attributeType: AttrList, valueDisplay: null]]                                                              | []

        'extValueId not null; no message'     | [[attributeType: Fixed, extValueId: '123']]                                                                  | []
        'qualifier not null; no message'      | [[attributeType: Fixed, qualifier: ' =']]                                                                    | []
        'valueNum not null; no message'       | [[attributeType: Fixed, valueNum: 1]]                                                                        | []
        'valueMin not null; no message'       | [[attributeType: Fixed, valueMin: 1]]                                                                        | []
        'valueMax not null; no message'       | [[attributeType: Fixed, valueMax: 1]]                                                                        | []
        'qualifier not null; no message'      | [[attributeType: Fixed, qualifier: ' =']]                                                                    | []
        'valueDisplay not null; no message'   | [[attributeType: Fixed, valueDisplay: 'foo']]                                                                | []
    }
}
