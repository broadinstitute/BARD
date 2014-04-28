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

package bard.dm.postUploadProcessing

import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.dm.Log

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
        Log.logger.info("ADID: ${assay.id}")
        Log.logger.info("SourceAssayContext: ${sourceAssayContext.contextName} (${sourceAssayContext.id})")
        Log.logger.info("""SourceAssayContextItem: attribute='${sourceItem.attributeElement.label}' (${sourceItem.attributeElement.id}); valueElement='${sourceItem.valueElement?.label}' (${sourceItem.valueElement?.id});valueNum=${sourceItem.valueNum}; valueDisplay='${sourceItem.valueDisplay}'""")
        Integer i = 1
        modifiedItems.each {AssayContextItem modifiedItem ->
            Log.logger.info("""ModifiedAssayContextItem${i++}: attribute='${modifiedItem.attributeElement.label}' (${modifiedItem.attributeElement.id}); valueElement='${modifiedItem.valueElement?.label}' (${modifiedItem.valueElement?.id});valueNum=${modifiedItem.valueNum}; valueDisplay='${modifiedItem.valueDisplay}'""")
        }
        Log.logger.info("New group: ${newGroup}")

        //Delete the source AssayContextItem and remove it from its AssayContext; leave the AssayContextGroup even if empty (Simon's requirement)
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        assert sourceAssayContext.save(), 'Could not remove the source AssayContextItem'
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
        assert assayContext.save(flush: true), 'Could not save the new AssayContext'

        this.changed = true
        return this.changed
    }
}

//Represents a group of related change-items (i.e., a context-change paragraph) as built from parsing of the spreadsheet's rows
class ContextChangeDTO {
    Double aid
    Double sourceAssayContextId
    ContextItem sourceContextItem
    List<ContextItem> modifiedContextItems = []
    Boolean newGroup
}

class ContextItem {
    String attributeLabel
    String valueLabel
}
