package bard.dm.assaycompare

import bard.db.registration.AssayContext
import bard.dm.Log

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/25/12
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextMatchTripleBuilder implements MatchTripleBuilder<AssayContext, ComparisonResultEnum> {
    private final AssayContextCompare assayContextCompare

    public AssayContextMatchTripleBuilder() {
        assayContextCompare = new AssayContextCompare()
    }

    MatchTriple<AssayContext, ComparisonResultEnum> build(AssayContext item1, AssayContext item2) {
        Log.logger.trace("\t\tAssayCompare MatchTripleBuilder build AssayContext s item1 item2 ${item1.id} ${item2.id}")

        ComparisonResult<ContextItemComparisonResultEnum> result = assayContextCompare.compareContext(item1, item2)

        if (result) {
            if (result.resultEnum.equals(ComparisonResultEnum.ExactMatch) || result.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
                return new MatchTriple<AssayContext, ComparisonResultEnum>(item1: item1, item2: item2, matchCondition: result)
            }
        }

        return null
    }

    ComparisonResultEnum determineMatchCondition(Collection<MatchTriple<AssayContext, ComparisonResultEnum>> matchTripleColl) {
        boolean subsetMatch = false
        Iterator<MatchTriple<AssayContext, ComparisonResultEnum>> iter = matchTripleColl.iterator()
        while ((!subsetMatch) && iter.hasNext()) {
            subsetMatch = iter.next().matchCondition.equals(ComparisonResultEnum.SubsetMatch)
        }

        if (subsetMatch) {
            return ComparisonResultEnum.SubsetMatch
        } else {
            return ComparisonResultEnum.ExactMatch
        }
    }
}
