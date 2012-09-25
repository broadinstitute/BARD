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
        MatchTripleBuilder<AssayContext, ComparisonResultEnum> matchTripleBuilder = new MatchTripleBuilder<AssayContext, ComparisonResultEnum>() {
            private final AssayContextCompare assayContextCompare = new AssayContextCompare()

            MatchTriple<AssayContext, ComparisonResultEnum> build(AssayContext item1, AssayContext item2) {
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
                    subsetMatch = iter.next().matchCondition.resultEnum.equals(ComparisonResultEnum.SubsetMatch)
                }

                if (subsetMatch) {
                    return ComparisonResultEnum.SubsetMatch
                } else {
                    return ComparisonResultEnum.ExactMatch
                }
            }
        }

        compareUsingMatches = new CompareUsingMatches<AssayContext, ComparisonResultEnum>(matchTripleBuilder)
    }

    ComparisonResult<ComparisonResultEnum> compareAssays(Assay a1, Assay a2) {
        return compareUsingMatches.compare(a1.assayContexts, a2.assayContexts)
    }
}
