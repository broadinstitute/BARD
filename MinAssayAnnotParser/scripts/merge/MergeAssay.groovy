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

List<String> ids = []
new File(fileName).eachLine {String line ->
    line = StringUtils.trim(line)
    if (!StringUtils.startsWith(line, "//")) {
        ids << line
    }
}

Assay.withTransaction { s ->
    if (!StringUtils.equals("true", willCommit)) {
        s.setRollbackOnly()
    }

Assay.withSession { status ->
    mergeAll(status, ids, modifiedBy)
}
}
println("Finished!")


def mergeAll(def status, List<String> ids, String modifiedBy) {
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
        merge(status, assays, modifiedBy)
    }
}

def merge(def session, List<Assay> assays, String modifiedBy) {
    def mergeAssayService = new MergeAssayService()
    println("Merge Assay : ${assays.collect {it.id}}")
    Assay assayWillKeep = mergeAssayService.keepAssay(assays)
    List<Assay> removingAssays = mergeAssayService.assaysNeedToRemove(assays, assayWillKeep)

    println("start mergeAssayContextItem")
    mergeAssayService.mergeAssayContextItem(removingAssays, assayWillKeep, modifiedBy)  // merege assay contextitem, experiment contextitem
    println("end mergeAssayContextItem")
    session.flush()

    println("start handleExperiments")
    mergeAssayService.handleExperiments(removingAssays, assayWillKeep, modifiedBy)     // associate experiments with kept
    println("end handleExperiments")
    session.flush()

    println("start handleDocuments ")
    mergeAssayService.handleDocuments(removingAssays, assayWillKeep, modifiedBy)       // associate document
    println("end handleDocuments ")
    session.flush()

    println("start handleMeasure")
    mergeAssayService.handleMeasure(session,removingAssays, assayWillKeep, modifiedBy)         // associate measure
    println("end handleMeasure")
    session.flush()

    println("Update assays status to Retired")
    mergeAssayService.updateStatus(removingAssays, modifiedBy)         // associate measure
    println("End of marking assayStatus to retired")
    session.flush()
}
