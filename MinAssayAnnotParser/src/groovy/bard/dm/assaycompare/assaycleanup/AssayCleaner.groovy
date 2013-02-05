package bard.dm.assaycompare.assaycleanup

import bard.dm.assaycompare.AssayMatch
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/28/12
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayCleaner {
    public void clean(AssayMatch assayMatch) {
        for (LimitedAssayContext duplicateContext : assayMatch.duplicateOriginalContextMap.keySet()) {
            assayMatch.assay.assayContexts.remove(duplicateContext.assayContext)
            duplicateContext.assayContext.delete()
        }
        assayMatch.assay.save()

        for (LimitedAssayContext limitedContext : assayMatch.limitedAssayContextList) {
            for (AssayContextItem duplicateItem : limitedContext.duplicateOriginalItemMap.keySet()) {
                limitedContext.assayContext.assayContextItems.remove(duplicateItem)
                duplicateItem.delete()
            }

            limitedContext.assayContext.save()
        }
    }
}
