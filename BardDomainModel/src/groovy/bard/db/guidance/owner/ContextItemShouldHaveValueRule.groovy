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

/**
 * Created by ddurkin on 1/28/14.
 */
class ContextItemShouldHaveValueRule implements GuidanceRule {

    private final Assay assay

    private final AssayContext assayContext

    ContextItemShouldHaveValueRule(Assay assay) {
        this.assay = assay
    }

    ContextItemShouldHaveValueRule(AssayContext assayContext) {
        this.assayContext = assayContext
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidanceList = []
        for (AssayContextItem item : getContextItems()) {
            if (isFixedAndAllValuesNull(item)) {
                guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(item.attributeElement.label)))
            }
        }
        return guidanceList
    }

    private List<AssayContextItem> getContextItems() {
        final List<AssayContextItem> contextItems = []
        if (assay) {
            contextItems.addAll(assay.contexts.assayContextItems.flatten())
        } else {
            contextItems.addAll(assayContext.assayContextItems)
        }
        contextItems
    }

    public static String getErrorMsg(String attributeLabel) {
        "Please specify a value for '${attributeLabel}'."
    }

    public static boolean isFixedAndAllValuesNull(AssayContextItem item) {
        item.attributeType==AttributeType.Fixed && item.allValueColumnsAreNull()
    }
}
