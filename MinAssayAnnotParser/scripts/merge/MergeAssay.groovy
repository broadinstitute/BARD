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
   // delete(assays)
}

def merge(List<Assay> assays, String modifiedBy) {
    def mergeAssayService = new MergeAssayService()
    println("Merge Assay : ${assays.collect {it.id}}")


    Assay assayWillKeep = mergeAssayService.keepAssay(assays)
    List<Assay> removingAssays = mergeAssayService.assaysNeedToRemove(assays, assayWillKeep)
    println("start mergeAssayContextItem")
    Assay.withTransaction { status ->
        mergeAssayService.mergeAssayContextItem(removingAssays, assayWillKeep, modifiedBy)  // merege assay contextitem, experiment contextitem
    }
    println("end mergeAssayContextItem")
    println("start handleExperiments")
    Assay.withTransaction { status ->
        mergeAssayService.handleExperiments(removingAssays, assayWillKeep, modifiedBy)     // associate experiments with kept
    }
    println("end handleExperiments")
    println("start handleDocuments ")
    Assay.withTransaction { status ->
        mergeAssayService.handleDocuments(removingAssays, assayWillKeep, modifiedBy)       // associate document
    }
    println("end handleDocuments ")
    println("start handleMeasure")
    Assay.withTransaction { status ->
        mergeAssayService.handleMeasure(removingAssays, assayWillKeep, modifiedBy)         // associate measure
    }
    println("end handleMeasure")

}

def delete(List<Assay> assays) {
    def db = grailsApplication.config.dataSource
    def sql = Sql.newInstance(db.url, db.username, db.password, db.driverClassName)
}