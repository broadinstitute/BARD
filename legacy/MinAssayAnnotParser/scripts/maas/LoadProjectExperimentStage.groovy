import maas.ProjectExperimentStageHandlerService
import maas.MustLoadAid
import org.apache.commons.lang3.StringUtils
import bard.db.project.Project
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/15/13
 * Time: 12:27 PM
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

//final List<Long> mustLoadedAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids.csv')
//def inputDirs = ['data/maas/maasDataset2']

ProjectExperimentStageHandlerService handlerService = new ProjectExperimentStageHandlerService()

if (!StringUtils.equals("true", willCommit)) {
    println "willCommit is not true"
    Project.withTransaction{status ->
        handlerService.handle('xiaorong-override-stage', inputDirs, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    println("willCommit is true")
    handlerService.handle('xx-stage', inputDirs, mustLoadedAids)
}

