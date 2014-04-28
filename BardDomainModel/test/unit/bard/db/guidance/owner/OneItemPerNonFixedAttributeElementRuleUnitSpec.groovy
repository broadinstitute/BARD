/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
