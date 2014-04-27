package bard.dm.assaycompare

import bard.db.registration.Assay

import bard.dm.assaycompare.assaycleanup.LimitedAssayContext

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/25/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayMatch {
    Assay assay

//    Set<Assay> exactMatches

//    Set<Assay> subsetOfThis

//    Map<Assay, Integer> partialMatchCountMap

    List<LimitedAssayContext> limitedAssayContextList

    Map<LimitedAssayContext, LimitedAssayContext> duplicateOriginalContextMap

    public AssayMatch(Assay assay) {
        this.assay = assay

//        exactMatches = new HashSet<Assay>()
//        subsetOfThis = new HashSet<Assay>()
//        partialMatchCountMap = new HashMap<Assay, Integer>()

        limitedAssayContextList = new LinkedList<LimitedAssayContext>()

        duplicateOriginalContextMap = new HashMap<LimitedAssayContext, LimitedAssayContext>()
    }
}