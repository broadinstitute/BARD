package bard.dm.assaycompare.assaycleanup

import bard.db.registration.AssayContextItem

import bard.dm.assaycompare.assaycleanup.LimitedAssayContext
import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.dm.assaycompare.CompareUsingMatches
import bard.dm.assaycompare.AssayContextItemMatchTripleBuilder
import bard.dm.assaycompare.ComparisonResult

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/27/12
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
class LimitedAssayContextCompare {
    private CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum> compareUsingMatches

    public LimitedAssayContextCompare() {
        compareUsingMatches = new CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum>(new AssayContextItemMatchTripleBuilder())
    }

    /**
     * compare assay contexts based on their assay context items to see if they match
     * @param ac1
     * @param ac2
     * @return null if one of the assay contexts has no items, otherwise comparison result
     */
    ComparisonResult<ContextItemComparisonResultEnum> compareContext(LimitedAssayContext ac1, LimitedAssayContext ac2) {
        return compareUsingMatches.compare(ac1.itemSet, ac2.itemSet)
    }
}
