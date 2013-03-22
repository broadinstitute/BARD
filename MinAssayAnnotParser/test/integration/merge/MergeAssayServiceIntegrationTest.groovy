package merge

import bard.db.registration.Assay

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/19/13
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
class MergeAssayServiceIntegrationTest extends GroovyTestCase {
    def mergeAssayService
    def testMerge() {
        def id1 = [2050,5168]
        def id2 = [5633,5644,5635,5636,5642,1947]
        def id3 = [5634,5638,5978,5645,5637,5221,5223,5414,5499,1468,5163]
        def id4 = [5509,5516]
        def assays = []
        id1.each{
            assays << Assay.findById(it)
        }
        String modifiedBy = 'xx'

            Assay assayWillKeep = mergeAssayService.keepAssay(assays)
            List<Assay> removingAssays = mergeAssayService.assaysNeedToRemove(assays, assayWillKeep)
             mergeAssayService.mergeAssayContextItem(removingAssays, assayWillKeep, modifiedBy)  // merege assay contextitem, experiment contextitem
        mergeAssayService.handleExperiments(removingAssays, assayWillKeep, modifiedBy)     // associate experiments with kept
        mergeAssayService.handleDocuments(removingAssays, assayWillKeep, modifiedBy)       // associate document
        mergeAssayService.handleMeasure(removingAssays, assayWillKeep, modifiedBy)         // associate measure
//        String modifiedBy = "xx"
//        removingAssays.each{
//            modifiedBy = modifiedBy + "-" + it.id
//        }
//        assayWillKeep.modifiedBy = modifiedBy.length() <= 40? modifiedBy : modifiedBy.substring(0, 40)
//        assayWillKeep.save()
      //  mergeAssayService.delete(removingAssays)
    }

}
