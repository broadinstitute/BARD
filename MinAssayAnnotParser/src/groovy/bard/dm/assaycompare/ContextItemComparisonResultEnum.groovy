package bard.dm.assaycompare

/**
 * ExactMatch - the context items have the same element for their values
 * EpsMatch - the context items have numerical values that match (within EPS precision)
 * DoesNotMatch - the context items do not match (either different attributes or different values)
 *
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
enum ContextItemComparisonResultEnum {
    ExactMatch, EpsMatch, DoesNotMatch
}
