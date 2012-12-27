package bard.dm.assaycompare

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/24/12
 * Time: 2:00 AM
 * To change this template use File | Settings | File Templates.

 * @param < T >  class of item that is being compared / matched
 * @param < V >  class of item that represents condition of match
 */
interface MatchTripleBuilder<T, V> {
    /**
     *
     * @param item1
     * @param item2
     * @return null if item1 does not match item2, otherwise match containing the two matching items and an object
     * describing the condition of the match
     */
    MatchTriple<T, V> build(T item1, T item2)

    /**
     *
     * @param matchTripleColl
     * @return
     */
    V determineMatchCondition(Collection<MatchTriple<T, V>> matchTripleColl)
}
