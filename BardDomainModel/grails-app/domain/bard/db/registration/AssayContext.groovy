package bard.db.registration

import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceAware
import bard.db.guidance.context.BiologyShouldHaveOneSupportingReferencePerContextRule
import bard.db.guidance.context.OneBiologyAttributePerContextRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner

class AssayContext extends AbstractContext implements GuidanceAware {

    private static final Map<String, String> KEY_LABEL_NAME_MAP = ['assay component role': 'label',
            'assay component type': 'label', 'detection': 'detection method',
            'assay readout': 'assay readout', 'wavelength': 'fluorescence/luminescence',
            'number': 'result detail']

    Assay assay




    List<AssayContextItem> assayContextItems = []
    Set<AssayContextMeasure> assayContextMeasures = [] as Set

    static belongsTo = [assay: Assay]

    static hasMany = [assayContextItems: AssayContextItem, assayContextMeasures: AssayContextMeasure]

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

        for(item in assayContextItems) {
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

    @Override
    List<Guidance> getGuidance() {
        List<Guidance> guidanceList = []
        guidanceList.add(new OneBiologyAttributePerContextRule(this).getGuidance())
        guidanceList.add(new BiologyShouldHaveOneSupportingReferencePerContextRule(this).getGuidance())
        guidanceList.flatten()
    }
}