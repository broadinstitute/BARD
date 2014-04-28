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
import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceRule
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import groovy.transform.TypeChecked

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/4/13
 * Time: 10:55 AM
 * An assay definition should only be allowed to have a single context item per attributeElement that has an attributeType != Fixed
 */
class OneItemPerNonFixedAttributeElementRule implements GuidanceRule {

    final Assay assay
    final AssayContext assayContext

    OneItemPerNonFixedAttributeElementRule(Assay assay) {
        this.assay = assay
    }

    OneItemPerNonFixedAttributeElementRule(AssayContext assayContext) {
        this.assayContext = assayContext
        this.assay = assayContext.assay
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidanceList = []
        final List<AssayContextItem> items = this.assay.contexts.assayContextItems.flatten()
        final Map attributeToItemsMap = items.groupBy { it.attributeElement }

        attributeToItemsMap.each { Element attribute, List<AssayContextItem> itemsForAttribute ->
            final ArrayList<AssayContextItem> nonFixedItems = itemsForAttribute.findAll { it.attributeType != AttributeType.Fixed }
            final String attributeLabel = attribute.label
            final Map<AttributeType,List<AssayContextItem>> attributeTypeToItemsMap = nonFixedItems.groupBy {AssayContextItem aci -> aci.attributeType}
            if (nonFixedItems.size() > 1) {
                if(attributeTypeToItemsMap.containsKey(AttributeType.List) && attributeTypeToItemsMap.size() == 1 ){
                   // multiple List items are ok as long as there are only List items no Range or Free items
                }
                else if (this.assayContext == null) { // report assay wide issue
                    guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(attributeLabel)))
                } else { // only report if an item within this assayContext contains one an offending item
                    if (this.assayContext.contextItems.any { nonFixedItems.contains(it) }) {
                        guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(attributeLabel)))
                    }
                }
            }
        }
        return guidanceList
    }


    public static String getErrorMsg(String attributeLabel) {
        "The attribute '${attributeLabel}' should only appear in one context item for any value that will be provided with an experiment."
    }
}
