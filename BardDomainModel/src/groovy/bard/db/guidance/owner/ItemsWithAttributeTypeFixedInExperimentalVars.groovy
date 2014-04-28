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
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.model.AbstractContextOwner.ContextGroup
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 1/8/14
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
class ItemsWithAttributeTypeFixedInExperimentalVars implements GuidanceRule  {

    final Assay assay
    final AssayContext assayContext

    ItemsWithAttributeTypeFixedInExperimentalVars(Assay assay) {
        this.assay = assay
    }

    ItemsWithAttributeTypeFixedInExperimentalVars(AssayContext assayContext) {
        this.assayContext = assayContext
        this.assay = assayContext.assay
    }

    List<Guidance> getGuidance() {

        final List<Guidance> guidanceList = []
        final List<AssayContextItem> items = this.assay.groupExperimentalVariables().value.assayContextItems.flatten()
        final Map attributeToItemsMap = items.groupBy { it.attributeElement }

        attributeToItemsMap.each { Element attribute, List<AssayContextItem> itemsForAttribute ->
            final ArrayList<AssayContextItem> fixedItems = itemsForAttribute.findAll { it.attributeType == AttributeType.Fixed }
            final String attributeLabel = attribute.label
            final Map<AttributeType,List<AssayContextItem>> attributeTypeToItemsMap = fixedItems.groupBy {AssayContextItem aci -> aci.attributeType}
            if (fixedItems.size() > 0) {
                if (this.assayContext.contextItems.any { fixedItems.contains(it) }) {
                    guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(attributeLabel)))
                }
            }
        }
        return guidanceList
    }

    public static String getErrorMsg(String attributeLabel) {
        "For attribute '${attributeLabel}', value in this section should be provided as part of result upload, not statically defined."
    }
}
