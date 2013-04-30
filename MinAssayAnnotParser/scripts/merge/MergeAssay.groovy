import merge.MergeAssayService
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import org.apache.commons.lang.StringUtils
import javax.sql.DataSource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import groovy.sql.Sql
import bard.db.experiment.ExperimentMeasure
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/20/13
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */

String fileName = "data/merge/assayIdNeedToMerge.txt"
String modifiedBy = "xiaorong-merge"
List<String> ids = []
new File(fileName).eachLine {String line ->
    line = StringUtils.trim(line)
    if (!StringUtils.startsWith(line, "//")) {
        ids << line
    }
}

Assay.withTransaction { status ->
for (String id : ids) {
    def assayIds = StringUtils.split(id, ",")
    def assays = []
    assayIds.each {
        Assay found = Assay.findById(Long.valueOf(it))
        if (found)
            assays << found
        else
            println("Assay id ${it} not found")
    }
    merge(assays, modifiedBy)
   // def aids = [2050,5644,5635,5636,5642,1947,5634,5638,5978,5645,5637,5221,5223,5414,1468,5163, 5509]   // dataset1, should update the status
   // updateStatus(aids, modifiedBy)
}
}

def merge(List<Assay> assays, String modifiedBy) {
    def mergeAssayService = new MergeAssayService()
    println("Merge Assay : ${assays.collect {it.id}}")


    Assay assayWillKeep = mergeAssayService.keepAssay(assays)
    List<Assay> removingAssays = mergeAssayService.assaysNeedToRemove(assays, assayWillKeep)
    println("start mergeAssayContextItem")
    mergeAssayService.mergeAssayContextItem(removingAssays, assayWillKeep, modifiedBy)  // merege assay contextitem, experiment contextitem
    println("end mergeAssayContextItem")
    println("start handleExperiments")
    mergeAssayService.handleExperiments(removingAssays, assayWillKeep, modifiedBy)     // associate experiments with kept
    println("end handleExperiments")
    println("start handleDocuments ")
    mergeAssayService.handleDocuments(removingAssays, assayWillKeep, modifiedBy)       // associate document
    println("end handleDocuments ")
    println("start handleMeasure")
    mergeAssayService.handleMeasure(removingAssays, assayWillKeep, modifiedBy)         // associate measure
    println("end handleMeasure")
    println("Update assays status to Retired")
    mergeAssayService.updateStatus(removingAssays, modifiedBy)         // associate measure
    println("End of marking assayStatus to retired")
}

def updateStatus(List<Long> aids, modifiedBy) {
    MergeAssayService mergeAssayService = new MergeAssayService()
    def assays = []
    aids.each{Long aid->
        Assay found = Assay.findById(Long.valueOf(aid))
        assays<<found
    }
    mergeAssayService.updateStatus(assays, modifiedBy)
}
//def delete(List<Assay> assays) {
//    def db = grailsApplication.config.dataSource
//    def sql = Sql.newInstance(db.url, db.username, db.password, db.driverClassName)
//}

def deleteAssay(Long assayId) {
    MergeAssayService mergeAssayService = new MergeAssayService()
   Assay assay = Assay.findById(assayId)
   if (assay)
   mergeAssayService.deleteAssay(assay)
}