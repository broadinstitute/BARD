package bard.dm.assaycompare

import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.dm.Log

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/24/12
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayCompare {
    CompareUsingMatches<AssayContext, ComparisonResultEnum> compareUsingMatches

    public AssayCompare() {
        compareUsingMatches = new CompareUsingMatches<AssayContext, ComparisonResultEnum>(new AssayContextMatchTripleBuilder())
    }

    ComparisonResult<ComparisonResultEnum> compareAssays(Assay a1, Assay a2) {
        return compareUsingMatches.compare(a1.assayContexts, a2.assayContexts)
    }
}
