package bard.dm.postUploadProcessing

import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 10/4/12
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Represents a context-change as typed element from the data-model
 */
class ContextChange {
    Assay assay
    AssayContext sourceAssayContext
    AssayContextItem sourceItem
    List<AssayContextItem> modifiedItems = []
    Boolean newGroup
    private Boolean changed = false

    final private String DEFAULT_ASSAY_CONTEXT_NAME = 'Needs a name'

    Boolean doChange() {
        //Delete the source AssayContextItem and remove it from its AssayContext; leave the AssayContextGroup even if empty (Simon's requirement)
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        assert  sourceAssayContext.save(), 'Could not remove the source AssayContextItem'
        sourceItem.delete()

        AssayContext assayContext
        //if newGroup: create a new AssayContext object
        if (this.newGroup) {
            assayContext = new AssayContext(assay: this.assay,
                    contextName: DEFAULT_ASSAY_CONTEXT_NAME)
        }
        //else, use the sourceAssayContext
        else {
            assayContext = sourceAssayContext
        }

        //Add all the new AssayContextItems to AssayContext
        assayContext.assayContextItems.addAll(modifiedItems)
        assert  assayContext.save(flush: true), 'Could not save the new AssayContext'

        this.changed = true
        return this.changed
    }
}

//Represents a group of related change-items (i.e., a context-change paragraph) as built from parsing of the spreadsheet's rows
class ContextChangeDTO {
    Double adid
    Double sourceAssayContextId
    ContextItem sourceContextItem
    List<ContextItem> modifiedContextItems = []
    Boolean newGroup
}

class ContextItem {
    String attributeLabel
    String valueLabel
}