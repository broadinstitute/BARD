package bard.db.registration

import bard.db.dictionary.Descriptor
import org.apache.commons.lang.StringUtils
import bard.db.model.AbstractContext

class AssayContext extends AbstractContext{

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
    List<AssayContextItem> getContextItems(){
        this.assayContextItems
    }
}