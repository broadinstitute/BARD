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

import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 2:42 PM
 *
 * Assay should have at least 1 context that defines biology, UNLESS they have a context-item that with attribute=='assay format' and valueElement=='small-molecule format' (https://www.pivotaltracker.com/story/show/60368176).
 * They can have more than 1 but need at least 1
 */
class MinimumOfOneBiologyGuidanceRule implements GuidanceRule {

    private static final String BIOLOGY_LABEL = 'biology'
    private static final String ASSAY_ONE_BIOLOGY_ATTRIBUTE_REQUIRED = "There should be at least 1 context with an item where either the attribute is 'biology' or where the attribute is 'assay format' and the value 'small molecule'."
    private static final String PROJECT_ONE_BIOLOGY_ATTRIBUTE_REQUIRED = "There should be at least 1 context with an item where the attribute is 'biology'."
    AbstractContextOwner owner

    MinimumOfOneBiologyGuidanceRule(AbstractContextOwner owner) {
        this.owner = owner
    }

    @Override
    List<Guidance> getGuidance() {

        final List<Guidance> guidance = []

        List<AbstractContextItem> itemsWithAttributeOfBiology = []
        for (AbstractContext context in owner.contexts) {
            for (AbstractContextItem item in context.contextItems) {
                if (item.attributeElement.label == BIOLOGY_LABEL) {
                    itemsWithAttributeOfBiology.add(item)
                }
            }
        }

        if(owner.getClass().simpleName.equals("Assay")){
            //Find all context-items where attribute=='assay format' and valueElement=='small-molecule format'
            List<AbstractContextItem> itemsWithAssayFormatEqualsSmallMoleculeFormat = this.owner.contexts?.collect { AbstractContext context ->
                return context.contextItems?.findAll { AbstractContextItem contextItem ->
                    return (contextItem.attributeElement?.label == 'assay format' && contextItem.valueElement?.label == 'small-molecule format')
                }
            }.flatten()

            if (itemsWithAttributeOfBiology.isEmpty() && itemsWithAssayFormatEqualsSmallMoleculeFormat.isEmpty()) {
                guidance.add(new DefaultGuidanceImpl(ASSAY_ONE_BIOLOGY_ATTRIBUTE_REQUIRED))
            }
        }
        else if(owner.getClass().simpleName.equals("Project")){
            if (itemsWithAttributeOfBiology.isEmpty()) {
                guidance.add(new DefaultGuidanceImpl(PROJECT_ONE_BIOLOGY_ATTRIBUTE_REQUIRED))
            }
        }

        return guidance
    }
}
