package bard.dm.assaycompare

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
class ComparisonResult<T> {
    /**
     * Indicates the relative number of context items that matched
     */
    ComparisonResultEnum resultEnum

    int matchedItemCount = 0

    /**
     * Indicates how the context items were found to match.
     * If they were all exact, this is exact
     * If at least one matched by EPS, this is eps
     * if there was no match, this is null
     */
    T matchCondition
}

/**
 * ExactMatch - all context items in each context matched
 * SubsetMatch - all of the context items in one context had a matching context item in the other context.  The second context had unmatched items
 * PartialMatch - some but not all of the context items in either context matched
 * DoesnotMatch - not match between any context items were found to match
 */
enum ComparisonResultEnum {
    ExactMatch, SubsetMatch, PartialMatch, DoesNotMatch
}
