package bard.dm.assaycompare.assaycleanup

import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/27/12
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
class LimitedAssayContext {
    AssayContext assayContext

    Set<AssayContextItem> itemSet

    Map<AssayContextItem, AssayContextItem> duplicateOriginalItemMap

    public LimitedAssayContext() {
        itemSet = new HashSet<AssayContextItem>()

        duplicateOriginalItemMap = new HashMap<AssayContextItem, AssayContextItem>()
    }
}
