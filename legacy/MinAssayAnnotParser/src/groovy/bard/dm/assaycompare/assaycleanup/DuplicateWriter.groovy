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

package bard.dm.assaycompare.assaycleanup

import bard.dm.assaycompare.AssayMatch
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/28/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
class DuplicateWriter {
    BufferedWriter contextWriter
    BufferedWriter itemWriter
    BufferedWriter dupContextItemWriter

    public DuplicateWriter(String outputDirectory) {
        itemWriter = new BufferedWriter(new FileWriter(new File(outputDirectory, "duplicate_assay_context_items.csv")))
        itemWriter.writeLine("assay_id,assay_context_id,assay_context_item_id1,assay_context_item_id2")

        dupContextItemWriter = new BufferedWriter(new FileWriter(new File(outputDirectory,
                "duplicate_assay_context_items_in_duplicate_contexts.csv")))
        dupContextItemWriter.writeLine("assay_id,assay_context_id,assay_context_item_id1, assay_context_item_id2")

        contextWriter = new BufferedWriter(new FileWriter(new File(outputDirectory, "duplicate_assay_contexts.csv")))
        contextWriter.writeLine("assay_id,asssay_context_id1,assay_context_id2")
    }

    public void write(AssayMatch assayMatch) {
        for (LimitedAssayContext duplicate : assayMatch.duplicateOriginalContextMap.keySet()) {
            LimitedAssayContext original = assayMatch.duplicateOriginalContextMap.get(duplicate)

            contextWriter.writeLine(assayMatch.assay.getId() + "," + duplicate.assayContext.getId() + "," + original.assayContext.getId())

            for (AssayContextItem duplicateItem : duplicate.duplicateOriginalItemMap.keySet()) {
                AssayContextItem originalItem = duplicate.duplicateOriginalItemMap.get(duplicateItem)

                dupContextItemWriter.writeLine(assayMatch.assay.getId() + "," + duplicate.assayContext.getId() + ","
                        + duplicateItem.getId() + "," + originalItem.getId())
            }
        }

        for (LimitedAssayContext limitedAssayContext : assayMatch.limitedAssayContextList) {
            for (AssayContextItem duplicateItem : limitedAssayContext.duplicateOriginalItemMap.keySet()) {
                AssayContextItem originalItem = limitedAssayContext.duplicateOriginalItemMap.get(duplicateItem)

                itemWriter.writeLine(assayMatch.assay.getId() + "," + limitedAssayContext.assayContext.getId() + ","
                        + duplicateItem.getId() + "," + originalItem.getId())
            }
        }
    }

    public void flushAll() {
        contextWriter.flush()
        itemWriter.flush()
        dupContextItemWriter.flush()
    }

    public void closeAll() {
        contextWriter.close()
        itemWriter.close()
        dupContextItemWriter.close()
    }
}


