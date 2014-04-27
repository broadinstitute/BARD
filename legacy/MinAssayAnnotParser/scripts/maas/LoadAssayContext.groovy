import maas.AssayHandlerService
import maas.MustLoadAid
import org.apache.commons.lang.StringUtils
import bard.db.registration.Assay


String dir =  System.getProperty("inputdir")
String dataset_id = System.getProperty("datasetid")
String willCommit = System.getProperty("willcommit")
if (!dir) {
    println("Please provide inputdir: -Dinputdir=yourdir")
    return
}
if (!dataset_id && !StringUtils.isNumeric(dataset_id)) {
    println("Please provide a valid dataset id: -Ddatasetid=validid")
    return
}
if (!willCommit) {
    println("This will be a dry run to check if there is any errors need to be fixed in spreadsheet.")
    println("If you are sure you need to commit: -Dwillcommit=true|false")
}


final String runBy = "xx"
final List<String> inputDirs = [dir]
final String outputDir = "${dir}/output/"

def mustLoadedAids
Assay.withSession { session ->
    mustLoadedAids = session.createSQLQuery("""
      select distinct aid from bard_data_qa_dashboard.vw_dataset_aid where dataset_id=${dataset_id} order by aid
  """).setCacheable(false).list().collect {it.longValue()}
}


//String aidsFile = "aids.csv"
//def mustLoadedAids = MustLoadAid.mustLoadedAids("${dir}/${aidsFile}")
println(mustLoadedAids)
if (!mustLoadedAids) {
    println("Nothing to be loaded")
    return
}


def assayHandlerService = new AssayHandlerService()

if (!StringUtils.equals("true", willCommit)) {
    println "willCommit is not true"
    Assay.withTransaction{status ->
    assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)
    status.setRollbackOnly()
    }
}
else {
    println("willCommit is true")
    assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)
}
//Assay.withTransaction{status ->
//    assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)
//    if (!StringUtils.equals("true", willCommit)) {
//        println("rollback")
//        status.setRollbackOnly()
//    }
//}