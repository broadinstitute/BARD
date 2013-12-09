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
import static OneItemPerNonFixedAttributeElementRule.*

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
class OneItemPerNonFixedAttributeElementRuleUnitSpec extends Specification {


    void 'test guidance for Assay level OneItemPerNonFixedAttributeElement #desc #assayContextItemMaps'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        final String attributeLabel = 'number of points'
        final Element attribute = Element.findByLabel(attributeLabel) ?: Element.build([label: attributeLabel])

        // putting each item in it's own context
        assayContextItemMaps.each { Map assayContextItemMap ->
            final AssayContext assayContext = AssayContext.build(assay: assay)
            assayContextItemMap << ['attributeElement': attribute, assayContext: assayContext]
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(assayContextItemMap)
        }

        when: 'we have the rule evaluate the assay'
        final OneItemPerNonFixedAttributeElementRule oneItemRule = new OneItemPerNonFixedAttributeElementRule(assay)
        final ArrayList<String> actualGuidanceMessages = oneItemRule.guidance.message

        then: 'we should see only the expected messages'
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                      | assayContextItemMaps                                                                               | expectedGuidanceMessages
        "no items ok"             | [[:]]                                                                                              | []
        "1 Fixed ok"              | [[attributeType: Fixed]]                                                                           | []
        "2 Fixed ok"              | [[attributeType: Fixed], [attributeType: Fixed]]                                                   | []
        "1 Fixed 1 List items ok" | [[attributeType: Fixed], [attributeType: AttributeType.List]]                                      | []
        "1 Fixed 2 List items ok" | [[attributeType: Fixed], [attributeType: AttributeType.List], [attributeType: AttributeType.List]] | []
        "1 Fixed 1 Free ok"       | [[attributeType: Fixed], [attributeType: Free]]                                                    | []
        "1 Fixed 1 Range ok"      | [[attributeType: Fixed], [attributeType: AttributeType.Range]]                                     | []

        "1 Free 1 List not ok"    | [[attributeType: Free], [attributeType: AttributeType.List]]                                       | [getErrorMsg('number of points')]
        "2 Free not ok"           | [[attributeType: Free], [attributeType: Free]]                                                     | [getErrorMsg('number of points')]
        "1 Free 1 Range not ok"   | [[attributeType: Free], [attributeType: AttributeType.Range]]                                      | [getErrorMsg('number of points')]

        "2 List ok"               | [[attributeType: AttributeType.List], [attributeType: AttributeType.List]]                         | []
        "1 List 1 Range not ok"   | [[attributeType: AttributeType.List], [attributeType: AttributeType.Range]]                        | [getErrorMsg('number of points')]
        "1 List 1 Free not ok"    | [[attributeType: AttributeType.List], [attributeType: Free]]                                       | [getErrorMsg('number of points')]

        "2 Range not ok"          | [[attributeType: AttributeType.Range], [attributeType: AttributeType.Range]]                       | [getErrorMsg('number of points')]
    }

    void 'test guidance AssayContext OneItemPerNonFixedAttributeElement #desc #assayContextItemMaps'() {
        given: 'an assay with some context items'
        final Assay assay = Assay.build()
        final String attributeLabel = 'number of points'
        final Element attribute = Element.findByLabel(attributeLabel) ?: Element.build([label: attributeLabel])
        final AssayContext assayContext = AssayContext.build(assay: assay)

        assayContextItemMaps.each { Map assayContextItemMap ->
            assayContextItemMap << ['attributeElement': attribute, assayContext: assayContext]
            final AssayContextItem aci = AssayContextItem.buildWithoutSave(assayContextItemMap)
        }

        when: 'we have the rule evaluate the assay'
        final OneItemPerNonFixedAttributeElementRule oneItemRule = new OneItemPerNonFixedAttributeElementRule(assayContext)
        final ArrayList<String> actualGuidanceMessages = oneItemRule.guidance.message

        then: 'we should see only the expected messages'
        actualGuidanceMessages == expectedGuidanceMessages

        where:
        desc                      | assayContextItemMaps                                                                               | expectedGuidanceMessages
        "no items ok"             | [[:]]                                                                                              | []
        "1 Fixed ok"              | [[attributeType: Fixed]]                                                                           | []
        "2 Fixed ok"              | [[attributeType: Fixed], [attributeType: Fixed]]                                                   | []
        "1 Fixed 1 List items ok" | [[attributeType: Fixed], [attributeType: AttributeType.List]]                                      | []
        "1 Fixed 2 List items ok" | [[attributeType: Fixed], [attributeType: AttributeType.List], [attributeType: AttributeType.List]] | []
        "1 Fixed 1 Free ok"       | [[attributeType: Fixed], [attributeType: Free]]                                                    | []
        "1 Fixed 1 Range ok"      | [[attributeType: Fixed], [attributeType: AttributeType.Range]]                                     | []

        "1 Free 1 List not ok"    | [[attributeType: Free], [attributeType: AttributeType.List]]                                       | [getErrorMsg('number of points')]
        "2 Free not ok"           | [[attributeType: Free], [attributeType: Free]]                                                     | [getErrorMsg('number of points')]
        "1 Free 1 Range not ok"   | [[attributeType: Free], [attributeType: AttributeType.Range]]                                      | [getErrorMsg('number of points')]

        "2 List ok"               | [[attributeType: AttributeType.List], [attributeType: AttributeType.List]]                         | []
        "1 List 1 Range not ok"   | [[attributeType: AttributeType.List], [attributeType: AttributeType.Range]]                        | [getErrorMsg('number of points')]
        "1 List 1 Free not ok"    | [[attributeType: AttributeType.List], [attributeType: Free]]                                       | [getErrorMsg('number of points')]

        "2 Range not ok"          | [[attributeType: AttributeType.Range], [attributeType: AttributeType.Range]]                       | [getErrorMsg('number of points')]
    }
}
