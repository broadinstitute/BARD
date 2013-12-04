package bard.db.registration

import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.guidance.GuidanceRule
import bard.db.guidance.context.BiologyShouldHaveOneSupportingReferencePerContextRule
import bard.db.guidance.context.OneBiologyAttributePerContextRule
import bard.db.guidance.owner.ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner

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
        rules.add(new ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule(this))
        rules
    }
}