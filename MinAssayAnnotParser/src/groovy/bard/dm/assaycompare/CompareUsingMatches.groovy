package bard.dm.assaycompare

/**
 * Determines the extent of matches between items (of class T) in 2 sets
 *
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/24/12
 * Time: 2:04 AM
 * To change this template use File | Settings | File Templates.

 * @param < T >  class of item to be matched
 * @param < V >  class of matching conditions
 */
class CompareUsingMatches<T, V> {
    private MatchTripleBuilder<T, V> matchTripleBuilder

    /**
     * @param matchTripleBuilder    used for determining whether to items of class T match each other, and if they match
     * building the match triple of the pair of matched items and the matching condition of class V
     */
    public CompareUsingMatches(MatchTripleBuilder<T, V> matchTripleBuilder) {
        this.matchTripleBuilder = matchTripleBuilder
    }

    /**
     * @param coll1    first collection of items to be matched
     * @param coll2    second collection of items to be matched
     * @return null if there were no items in one or both of the collections to compare, otherwise result of attempting to compare those
     */
    public ComparisonResult<V> compare(Collection<T> coll1, Collection<T> coll2) {
        //first check that both collections have items to be matched
        if (coll1.size() > 0 && coll2.size() > 0) {

            //generate temporary sets for each context, so that we can remove items from these as matches are found
            Set<T> set1 = new HashSet<T>(coll1)
            Set<T> set2 = new HashSet<T>(coll2)

            //collection of matches (includes condition of the match)
            Set<MatchTriple<T, V>> matchSet = new HashSet<MatchTriple<T,V>>()

            //iterate over the first collection, checking for matches against the second
            Iterator<T> iter1 = set1.iterator()
            while (iter1.hasNext()) {
                T item1 = iter1.next()

                Iterator<T> iter2 = set2.iterator()
                boolean foundMatch = false
                while ((!foundMatch) && iter2.hasNext()) {
                    T item2 = iter2.next()

                    MatchTriple<T, V> match = matchTripleBuilder.build(item1, item2)

                    //if an exact or EPS match was found between the items, remove them from the temporary sets
                    //and add the match to our matchSet
                    if (match) {
                        foundMatch = true

                        iter1.remove()
                        iter2.remove()

                        matchSet.add(match)
                    }
                }
            }

            //if no matches were found, indicate that
            if (matchSet.size() == 0) {
                return new ComparisonResult<V>(resultEnum: ComparisonResultEnum.DoesNotMatch)
            } else { //otherwise specify the type of match found
                ComparisonResult<V> result = new ComparisonResult<V>()

                if (set1.size() == 0 && set2.size() == 0) {
                    //since there are no items remaining in either temporary set, they were all matched
                    result.resultEnum = ComparisonResultEnum.ExactMatch
                } else if (set1.size() == 0 || set2.size() == 0) {
                    //since there are no items remaining in one of the temporary sets, that set was a subset of the other
                    result.resultEnum = ComparisonResultEnum.SubsetMatch
                } else {
                    //since matches were found but there are still items remaining in both of the temporary sets, they
                    //are a partial match
                    result.resultEnum = ComparisonResultEnum.PartialMatch
                }

                result.matchedItemCount = matchSet.size()

                //determine worst-case match condition from individual matches
                result.matchCondition = matchTripleBuilder.determineMatchCondition(matchSet)

                return result
            }

        } else { //no items in either or both collections so no comparison can be made
            return null
        }
    }
}
