package bard.dm.assaycompare

import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
class ContextItemMatchTriple {
    AssayContextItem aci1
    AssayContextItem aci2
    ContextItemComparisonResultEnum resultEnum

    public ContextItemMatchTriple(AssayContextItem aci1, AssayContextItem aci2, ContextItemComparisonResultEnum resultEnum) {
        this.aci1 = aci1
        this.aci2 = aci2
        this.resultEnum = resultEnum
    }
}
