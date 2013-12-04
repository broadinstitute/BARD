package bard.db.guidance.owner

import bard.db.dictionary.Element
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.AttributeType.Fixed
import static bard.db.registration.AttributeType.Free
import static bard.db.guidance.owner.ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule.*

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/4/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, Element])
@Mock([Assay, AssayContext, AssayContextItem, Element])
@Unroll
class ShouldOnlyHoveOneItemPerNonFixedAttributeElementRuleUnitSpec extends Specification {


    void 'test guidance for Assay level OneItemPerNonFixedAttributeElement #desc #assayContextItemMaps'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        final String attributeLabel = 'biology'
        final Element attribute = Element.findByLabel(attributeLabel) ?: Element.build([label: attributeLabel])

        // putting each item in it's own context
        assayContextItemMaps.each { Map assayContextItemMap ->
            final AssayContext assayContext = AssayContext.build(assay: assay)
            assayContextItemMap << ['attributeElement': attribute, assayContext: assayContext]
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(assayContextItemMap)
        }

        when: 'we have the rule evaluate the assay'
        final ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule oneItemRule = new ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule(assay)
        final ArrayList<String> actualGuidanceMessages = oneItemRule.guidance.message

        then: 'we should see only the expected messages'
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                                         | assayContextItemMaps                                                         | expectedGuidanceMessages
        'no items ok'                                | [[:]]                                                                        | []
        '1 item  with attribute ok'                  | [[attributeType: Fixed]]                                                     | []
        '2 items with attributeType != Fixed ok'     | [[attributeType: Fixed], [attributeType: Fixed]]                             | []

        '2 items with attributeType != Fixed ok'     | [[attributeType: Free], [attributeType: Fixed]]                              | []
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: Free]]                               | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: AttributeType.List]]                 | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: AttributeType.Range]]                | [getErrorMsg('biology')]

        '2 items with attributeType != Fixed ok'     | [[attributeType: AttributeType.List], [attributeType: Fixed]]                | []
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.List], [attributeType: AttributeType.List]]   | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.List], [attributeType: AttributeType.Range]]  | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.List], [attributeType: Free]]                 | [getErrorMsg('biology')]

        '2 items with attributeType != Fixed ok'     | [[attributeType: AttributeType.Range], [attributeType: Fixed]]               | []
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.Range], [attributeType: AttributeType.List]]  | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.Range], [attributeType: AttributeType.Range]] | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.Range], [attributeType: Free]]                | [getErrorMsg('biology')]
    }

    void 'test guidance AssayContext OneItemPerNonFixedAttributeElement #desc #assayContextItemMaps'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        final String attributeLabel = 'biology'
        final Element attribute = Element.findByLabel(attributeLabel) ?: Element.build([label: attributeLabel])
        final AssayContext assayContext = AssayContext.build(assay: assay)

        assayContextItemMaps.each { Map assayContextItemMap ->
            assayContextItemMap << ['attributeElement': attribute, assayContext: assayContext]
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(assayContextItemMap)
        }

        when: 'we have the rule evaluate the assay'
        final ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule oneItemRule = new ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule(assayContext)
        final ArrayList<String> actualGuidanceMessages = oneItemRule.guidance.message

        then: 'we should see only the expected messages'
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                                         | assayContextItemMaps                                                         | expectedGuidanceMessages
        'no items ok'                                | [[:]]                                                                        | []
        '1 item  with attribute ok'                  | [[attributeType: Fixed]]                                                     | []
        '2 items with attributeType != Fixed ok'     | [[attributeType: Fixed], [attributeType: Fixed]]                             | []

        '2 items with attributeType != Fixed ok'     | [[attributeType: Free], [attributeType: Fixed]]                              | []
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: Free]]                               | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: AttributeType.List]]                 | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: Free], [attributeType: AttributeType.Range]]                | [getErrorMsg('biology')]

        '2 items with attributeType != Fixed ok'     | [[attributeType: AttributeType.List], [attributeType: Fixed]]                | []
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.List], [attributeType: AttributeType.List]]   | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.List], [attributeType: AttributeType.Range]]  | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.List], [attributeType: Free]]                 | [getErrorMsg('biology')]

        '2 items with attributeType != Fixed ok'     | [[attributeType: AttributeType.Range], [attributeType: Fixed]]               | []
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.Range], [attributeType: AttributeType.List]]  | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.Range], [attributeType: AttributeType.Range]] | [getErrorMsg('biology')]
        '2 items with attributeType != Fixed not ok' | [[attributeType: AttributeType.Range], [attributeType: Free]]                | [getErrorMsg('biology')]
    }
}
