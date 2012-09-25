package bard.dm.assaycompare

import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextCompare {
    private CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum> compareUsingMatches

    public AssayContextCompare() {
        MatchTripleBuilder<AssayContextItem, ContextItemComparisonResultEnum> matchTripleBuilder = new MatchTripleBuilder<AssayContextItem, ContextItemComparisonResultEnum>() {
            private final AssayContextItemCompare assayContextItemCompare = new AssayContextItemCompare()

            MatchTriple build(AssayContextItem item1, AssayContextItem item2) {
                ContextItemComparisonResultEnum resultEnum = assayContextItemCompare.compareContextItems(item1, item2)

                if (resultEnum.equals(ContextItemComparisonResultEnum.ExactMatch) || resultEnum.equals(ContextItemComparisonResultEnum.EpsMatch)) {
                    return new MatchTriple<AssayContextItem, ContextItemComparisonResultEnum>(item1: item1, item2: item2, matchCondition: resultEnum)
                } else {
                    return null
                }
            }

            ContextItemComparisonResultEnum determineMatchCondition(Collection<MatchTriple<AssayContextItem, ContextItemComparisonResultEnum>> matchTripleColl) {
                boolean epsMatch = false
                Iterator<MatchTriple<AssayContextItem, ContextItemComparisonResultEnum>> iter = matchTripleColl.iterator()
                while ((!epsMatch) && iter.hasNext()) {
                    //check to see if the current matchTriple was matched by an EPS match
                    epsMatch = iter.next().matchCondition.equals(ContextItemComparisonResultEnum.EpsMatch)
                }

                if (epsMatch) {
                    return ContextItemComparisonResultEnum.EpsMatch
                } else {
                    return ContextItemComparisonResultEnum.ExactMatch
                }
            }
        }

        compareUsingMatches = new CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum>(matchTripleBuilder)
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
