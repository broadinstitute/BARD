package bard.dm.assaycompare

import bard.db.registration.Assay

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/25/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayMatch {
    Assay assay

    Set<Assay> exactMatches

    Set<Assay> subsetOfThis


    public AssayMatch(Assay assay) {
        this.assay = assay

        exactMatches = new HashSet<Assay>()
        subsetOfThis = new HashSet<Assay>()
    }
}