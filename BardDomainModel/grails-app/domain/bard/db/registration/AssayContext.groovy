package bard.db.registration

import bard.db.dictionary.Descriptor

class AssayContext {

    public static final String CONTEXT_NAME_WITH_NO_ITEMS = 'Empty Card, consider deleting!'

    private static final int CONTEXT_NAME_MAX_SIZE = 128
    private static final int MODIFIED_BY_MAX_SIZE = 40

    /**
     * these labels or portions of labels are pulled out of the ontology and are an order of preference for sorting and naming of cards
     */
    private static final List<String> KEY_LABELS = ['assay component role', 'assay component type', 'detection', 'assay readout', 'wavelength', 'number']

    String contextName
    Assay assay

    Set<Measure> measures = [] as Set<Measure>
    List<AssayContextItem> assayContextItems = []

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static belongsTo = [assay: Assay]

    static hasMany = [assayContextItems: AssayContextItem,
            measures: Measure]

    static mapping = {
        sort("ASSAY_CONTEXT_ID") // default sort order
        id(column: "ASSAY_CONTEXT_ID", generator: "sequence", params: [sequence: 'ASSAY_CONTEXT_ID_SEQ'])
        assayContextItems(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        contextName(maxSize: CONTEXT_NAME_MAX_SIZE, blank: false)
        assay()
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    /**
     *
     * @return
     */
    Descriptor getPreferredDescriptor() {
        Descriptor preferredDescriptor
        List<Descriptor> preferredDescriptors = assayContextItems.collect {it.attributeElement.ontologyBreadcrumb.preferedDescriptor}

        for (String keyLabel in KEY_LABELS) {
            if (preferredDescriptors.any {it.label.contains(keyLabel)}) {
                preferredDescriptor = preferredDescriptors.find { it.label.contains(keyLabel)}
                break
            }
        }

        if(preferredDescriptor == null && preferredDescriptors){
            preferredDescriptor = preferredDescriptors.first()
        }
        return preferredDescriptor
    }
}