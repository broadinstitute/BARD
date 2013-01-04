package bard.db.registration

import bard.db.dictionary.Descriptor
import org.apache.commons.lang.StringUtils
import bard.db.model.AbstractContext

class AssayContext extends AbstractContext{

    /**
     * these labels or portions of labels are pulled out of the ontology and are an order of preference for sorting and naming of cards
     */
    private static final List<String> KEY_LABELS = ['assay component role', 'assay component type', 'detection', 'assay readout', 'wavelength', 'number']

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
        assayContextItems(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    /**
     *
     * @return
     */
    Descriptor getPreferredDescriptor() {
        Descriptor preferredDescriptor
        List<Descriptor> preferredDescriptors = assayContextItems.collect {it.attributeElement.ontologyBreadcrumb.preferedDescriptor}
        preferredDescriptors = preferredDescriptors.findAll() // hack to eliminate any nulls (Elements (971 and 1329)
        for (String keyLabel in KEY_LABELS) {
            if (preferredDescriptors.any {it?.label?.contains(keyLabel)}) {
                preferredDescriptor = preferredDescriptors.find { it.label.contains(keyLabel)}
                break
            }
        }

        if (preferredDescriptor == null && preferredDescriptors) {
            preferredDescriptor = preferredDescriptors.first()
        }
        return preferredDescriptor
    }

    String getPreferredName() {
        String preferredName = 'undefined'
        if (StringUtils.isNotBlank(this.contextName)) {
            preferredName = this.contextName
        }
        else {
            preferredName = getPreferredDescriptor()?.label
            for (Map.Entry entry in KEY_LABEL_NAME_MAP) {
                if (preferredName && preferredName.contains(entry.key)) {
                    if ('label' != entry.value) {
                        preferredName = entry.value
                    }
                    break
                }
            }
        }
        return preferredName
    }

    /**
     * duck typing for context
     * @return list of assayContextItems
     */
    List<AssayContextItem> getContextItems(){
        this.assayContextItems
    }
}