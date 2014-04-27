package bard.dm.assaycompare

import bard.db.registration.AssayContextItem
import bard.dm.Log
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/25/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextItemMatchTripleBuilder implements MatchTripleBuilder<AssayContextItem, ContextItemComparisonResultEnum> {
    private final AssayContextItemCompare assayContextItemCompare

    private final Logger logger

    public AssayContextItemMatchTripleBuilder(Logger logger = Log.logger) {
        assayContextItemCompare = new AssayContextItemCompare(logger)

        this.logger = logger
    }

    MatchTriple<AssayContextItem, ContextItemComparisonResultEnum> build(AssayContextItem item1, AssayContextItem item2) {
        logger.trace("\t\t\tAssayContextCompare MatchTripleBuilder build AssayContextItem s ${item1?.id} ${item2?.id}")

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
