import bard.db.registration.Assay
import org.apache.commons.lang3.StringUtils
import merge.MergeAssayService
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/20/13
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */

String fileName = System.getProperty("filename")
String willCommit = System.getProperty("willcommit")

if (!fileName ) {
    println("Please provide a file name containing assays need to be merged: -Dinputdir=filename")
    return
}
if (!willCommit) {
    println("This will be a dry run and data won't be persisted in database.")
    println("If you are sure you need to commit: -Dwillcommit=true|false")
}


final String modifiedBy = "merge"

//String fileName = "data/merge/assayIdNeedToMerge.txt"

List<String> ids = []
new File(fileName).eachLine {String line ->
    line = StringUtils.trim(line)
    if (!StringUtils.startsWith(line, "//")) {
        ids << line
    }
}
if (!StringUtils.equals("true", willCommit)) {
    Assay.withSession { status ->
        mergeAll(ids, modifiedBy)
        status.setRollbackOnly()
    }
}
else {
    Assay.withSession { status ->
        mergeAll(ids, modifiedBy)
    }
}

def mergeAll(List<String> ids, String modifiedBy) {
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
