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

package bard.db.registration

import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.guidance.GuidanceRule
import bard.db.guidance.context.BiologyShouldHaveOneSupportingReferencePerContextRule
import bard.db.guidance.context.OneBiologyAttributePerContextRule
import bard.db.guidance.owner.ContextItemShouldHaveValueRule
import bard.db.guidance.owner.OneItemPerNonFixedAttributeElementRule

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner
import bard.db.guidance.owner.ItemsWithAttributeTypeFixedInExperimentalVars

class AssayContext extends AbstractContext {


    private static final Map<String, String> KEY_LABEL_NAME_MAP = ['assay component role': 'label',
            'assay component type': 'label', 'detection': 'detection method',
            'assay readout': 'assay readout', 'wavelength': 'fluorescence/luminescence',
            'number': 'result detail']

    Assay assay

    List<AssayContextItem> assayContextItems = []

    Set<AssayContextExperimentMeasure> assayContextExperimentMeasures = [] as Set

    static belongsTo = [assay: Assay]

    static hasMany = [assayContextItems: AssayContextItem, assayContextExperimentMeasures: AssayContextExperimentMeasure]

    static mapping = {
        sort("ASSAY_CONTEXT_ID") // default sort order
        id(column: "ASSAY_CONTEXT_ID", generator: "sequence", params: [sequence: 'ASSAY_CONTEXT_ID_SEQ'])
        assayContextItems(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'true')
    }

    /**
     * duck typing for context
     * @return list of assayContextItems
     */
    List<AssayContextItem> getContextItems() {
        this.assayContextItems
    }

    @Override
    AbstractContextOwner getOwner() {
        return assay
    }

    public AssayContext clone(Assay newOwner) {
        AssayContext newContext = new AssayContext(contextName: contextName, contextType: contextType);

        for (item in assayContextItems) {
            item.clone(newContext)
        }

        newOwner.addToAssayContexts(newContext)

        return newContext;
    }

    @Override
    String getSimpleClassName() {
        return "AssayContext"
    }

    @Override
    void addContextItem(AbstractContextItem item) {
        this.addToAssayContextItems(item)
    }

    @Override
    Class<? extends AbstractContextItem> getItemSubClass() {
        return AssayContextItem
    }

    /**
     *
     * @return the rules for AssayContext
     */
    @Override
    List<GuidanceRule> getGuidanceRules() {
        final List<GuidanceRule> rules = super.getGuidanceRules()
        rules.add(new OneBiologyAttributePerContextRule(this))
        rules.add(new BiologyShouldHaveOneSupportingReferencePerContextRule(this))
        rules.add(new OneItemPerNonFixedAttributeElementRule(this))
        rules.add(new ItemsWithAttributeTypeFixedInExperimentalVars(this))
        rules.add(new ContextItemShouldHaveValueRule(this))
        rules
    }
}
