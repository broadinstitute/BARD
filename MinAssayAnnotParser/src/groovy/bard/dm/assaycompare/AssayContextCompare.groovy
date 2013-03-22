package bard.dm.assaycompare

import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.dm.Log
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextCompare {
    private CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum> compareUsingMatches

    public AssayContextCompare(Logger logger = Log.logger) {
        compareUsingMatches =
            new CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum>(new AssayContextItemMatchTripleBuilder(logger))
    }

    /**
     * compare assay contexts based on their assay context items to see if they match
     * @param ac1
     * @param ac2
     * @return null if one of the assay contexts has no items, otherwise comparison result
     */
    ComparisonResult<ContextItemComparisonResultEnum> compareContext(AssayContext ac1, AssayContext ac2) {
        return compareUsingMatches.compare(ac1.assayContextItems, ac2.assayContextItems)
    }

}
