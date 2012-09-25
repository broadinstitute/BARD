package bard.dm.assaycompare

/**
  * Created with IntelliJ IDEA.
  * User: dlahr
  * Date: 9/24/12
  * Time: 1:58 AM
  * To change this template use File | Settings | File Templates.

 * @param < T >  class of item that is being matched
 * @param < V >  class of item that represents condition of observed match
 */
class MatchTriple<T, V> {
    T item1
    T item2

    V matchCondition
}
