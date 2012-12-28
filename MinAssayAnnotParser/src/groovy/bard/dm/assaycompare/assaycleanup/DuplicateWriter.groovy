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

    public DuplicateWriter() {
        itemWriter = new BufferedWriter(new FileWriter("test/exampleData/duplicate_assay_context_items.csv"))
        dupContextItemWriter = new BufferedWriter(new FileWriter("test/exampleData/duplicate_assay_context_items_in_duplicate_contexts.csv"))
        contextWriter = new BufferedWriter(new FileWriter("test/exampleData/duplicate_assay_contexts.csv"))
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

    public closeAll() {
        contextWriter.close()
        itemWriter.close()
        dupContextItemWriter.close()
    }
}


