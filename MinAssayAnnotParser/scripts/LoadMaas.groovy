import maas.AssayHandlerService
import bard.db.registration.Assay
import maas.ExperimentHandlerService
import org.apache.commons.lang3.StringUtils
import bard.db.experiment.Experiment
import org.hibernate.Session
import org.hibernate.jdbc.Work
import java.sql.Connection
import bard.db.project.Project
import bard.dm.cars.domainspreadsheetmapping.CarsBardMapping
import bard.dm.cars.spreadsheet.CarsProject
import bard.dm.cars.spreadsheet.CarsSpreadsheetReader
import bard.dm.cars.spreadsheet.CarsSpreadsheetValidator
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.dm.cars.domainspreadsheetmapping.ProjectPair
import bard.dm.cars.domainspreadsheetmapping.ProjectMapperBuilder
import bard.dm.cars.domainspreadsheetmapping.ProjectExperimentMapperBuilder
import bard.dm.cars.domainspreadsheetmapping.ProjectAnnotater
import bard.dm.cars.domainspreadsheetmapping.ProjectExperimentAnnotater
import bard.dm.cars.spreadsheet.exceptions.CouldNotReadHeadersException
import bard.dm.cars.spreadsheet.exceptions.MultipleProjectsForProjectUidException
import bard.dm.cars.spreadsheet.exceptions.ExternalReferenceMissingProjectException
import bard.dm.Log
import maas.ProjectHandlerService
import maas.ProjectExperimentStageHandlerService
import maas.FixPersonNameService

/**
 * This script load annotations from spreadsheet.
 * grails -Dgrails.env=dataqa -Dinputdir=data/maas/maasDataset3 -Ddatasetid=3 -Dwillcommit=false run-script scripts/LoadMaas.groovy --stacktrace
 */
String dir = System.getProperty("inputdir")
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

println("aids to load" + mustLoadedAids)
println("Start loading AssayContext")
if (!mustLoadedAids) {
    println("Nothing to be loaded")
    return
}

def assayHandlerService = new AssayHandlerService()
if (!StringUtils.equals("true", willCommit)) {
    Assay.withTransaction {status ->
        assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    assayHandlerService.load(inputDirs, outputDir, runBy, mustLoadedAids)
}
println("End loading AssayContext")

println("Start loading ExperimentContext")
ExperimentHandlerService experimentHandlerService = new ExperimentHandlerService()
if (!StringUtils.equals("true", willCommit)) {
    Experiment.withTransaction {status ->
        experimentHandlerService.handle(runBy, inputDirs, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    experimentHandlerService.handle(runBy, inputDirs, mustLoadedAids)
}
println("End loading ExperimentContext")

println("Start loading Project from cars")
if (!StringUtils.equals("true", willCommit)) {
    Project.withTransaction {status ->
        loadCars(dataset_id)
        status.setRollbackOnly()
    }
}
else {
    loadCars(dataset_id)
}
println("Finished cars loading")

println("Start loading ProjectContext")
ProjectHandlerService projectHandlerService = new ProjectHandlerService()
if (!StringUtils.equals("true", willCommit)) {
    Project.withTransaction {status ->
        projectHandlerService.handle(runBy, inputDirs, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    projectHandlerService.handle(runBy, inputDirs, mustLoadedAids)
}
println("Finished  loading projectContext")
println("Start loading ProjectExperimentStage")
ProjectExperimentStageHandlerService handlerService = new ProjectExperimentStageHandlerService()

if (!StringUtils.equals("true", willCommit)) {
    Project.withTransaction {status ->
        handlerService.handle('xiaorong-override-stage', inputDirs, mustLoadedAids)
        status.setRollbackOnly()
    }
}
else {
    handlerService.handle('xx-stage', inputDirs, mustLoadedAids)
}
println("Finished loading ProjectExperimentStage")

println("Start fixing person name")
def fixPersonNameService = new FixPersonNameService()
if (!StringUtils.equals("true", willCommit)) {
    Project.withTransaction {status ->
        fixPersonNameService.fix("fixPersonName")
        status.setRollbackOnly()
    }
}
else {
    fixPersonNameService.fix("fixPersonName")
}
println("Finished fixing person name")

def loadCars(dataset_id) {
    Log.initializeLogger("data/maas/cars/output/parseCarsSpreadsheet.log")
//constants
    final String inputFileRelativePath = "data/maas/cars/project_data_from_CARS.csv"
    final String headerMappingRelativePath = "grails-app/conf/resources/HeaderMappings.config"
//TODO rename this file, it is more general now
    final String elementFieldMappingRelativePath = "grails-app/conf/resources/ElementFieldMapping.config"
    final String username = "dlahr_CARS"
    def projectUidsToLoad
    Project.withSession { session ->
        projectUidsToLoad = session.createSQLQuery("""
        select distinct project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=${dataset_id} order by project_uid
  """).setCacheable(false).list().collect {it.longValue()}
    }

    println("uids to load " + projectUidsToLoad)
    if (!projectUidsToLoad) {
        println("No project UID to load")
        return
    }

    final Date startDate = new Date()

    try {
        final CarsBardMapping carsBardMapping = new CarsBardMapping(elementFieldMappingRelativePath)

        List<CarsProject> carsProjectList = (new CarsSpreadsheetReader()).readProjectsFromFile(inputFileRelativePath, headerMappingRelativePath, projectUidsToLoad)

        (new CarsSpreadsheetValidator()).validateProjects(carsProjectList)

        List<ProjectPair> projectPairList = (new ProjectMapperBuilder(username)).buildProjectPairs(carsProjectList)

        (new ProjectExperimentMapperBuilder(carsBardMapping, username)).mapOrBuildProjectExperiments(projectPairList)

        (new ProjectAnnotater(carsBardMapping, username)).addAnnotations(projectPairList)

        (new ProjectExperimentAnnotater(carsBardMapping, username)).addAnnotations(projectPairList)

        //run some checks on projects
        projectPairList.each {ProjectPair projectPair ->
            if (null == projectPair.project && null == projectPair.carsProject) {
                println("both db project and cars project are null!!!")
            } else if (null == projectPair.project) {
                println("has null db project " + projectPair.carsProject.projectUid)
            } else if (null == projectPair.carsProject) {
                println("has null cars project " + projectPair.project.id)
            } else if (projectPair.project != null) {
            }
        }
    } catch (Exception e) {
        e.printStackTrace()
    } catch (FileNotFoundException e) {
        e.printStackTrace()
    } catch (CouldNotReadHeadersException e) {
        e.printStackTrace()
    } catch (MultipleProjectsForProjectUidException e) {
        e.printStackTrace()
    } catch (ExternalReferenceMissingProjectException e) {
        e.printStackTrace()
    }
    finally {
        final Date endDate = new Date()
        double runningTime = ((double) (endDate.time - startDate.time)) / 60000.0
        Log.logger.info("time to run(min): " + runningTime)
        Log.close()
    }
}


