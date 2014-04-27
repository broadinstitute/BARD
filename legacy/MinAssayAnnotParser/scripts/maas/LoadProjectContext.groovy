import maas.ProjectHandlerService
import maas.MustLoadAid
import bard.db.project.Project
import org.springframework.transaction.support.DefaultTransactionStatus
import org.apache.commons.lang3.StringUtils
import bard.db.experiment.Experiment
/**
 *
 * Run this script to load project context
 *
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/7/13
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
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
Project.withSession { session ->
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
//String aidsFile = "aids.csv"
//final String runBy = "xx"
//final List<String> inputDirs = [dir]
//def mustLoadedAids = MustLoadAid.mustLoadedAids("${dir}${aidsFile}")

ProjectHandlerService projectHandlerService = new ProjectHandlerService()
if (!StringUtils.equals("true", willCommit)) {
    println "willCommit is not true"
    Project.withTransaction{status ->
        projectHandlerService.handle(runBy, inputDirs, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    println("willCommit is true")
    projectHandlerService.handle(runBy, inputDirs, mustLoadedAids)
}


