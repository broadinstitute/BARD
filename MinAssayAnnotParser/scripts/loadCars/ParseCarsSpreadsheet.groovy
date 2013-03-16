import bard.dm.cars.spreadsheet.CarsSpreadsheetReader
import bard.dm.cars.spreadsheet.CarsSpreadsheetValidator
import bard.dm.Log
import bard.dm.cars.domainspreadsheetmapping.ProjectPair
import bard.dm.cars.domainspreadsheetmapping.ProjectMapperBuilder
import bard.dm.cars.domainspreadsheetmapping.CarsBardMapping
import bard.dm.cars.domainspreadsheetmapping.ProjectAnnotater
import bard.dm.cars.spreadsheet.CarsProject
import bard.dm.cars.domainspreadsheetmapping.ProjectExperimentMapperBuilder
import bard.dm.cars.domainspreadsheetmapping.ProjectExperimentAnnotater
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.db.project.Project
import bard.dm.cars.spreadsheet.exceptions.CouldNotReadHeadersException
import bard.dm.cars.spreadsheet.exceptions.MultipleProjectsForProjectUidException
import bard.dm.cars.spreadsheet.exceptions.ExternalReferenceMissingProjectException
import bard.dm.cars.spreadsheet.ProjectIdsToLoad

Log.initializeLogger("test/exampleData/logsAndOutput/parseCarsSpreadsheet.log")
println("Start script")

//constants
final String inputFileRelativePath = "test/exampleData/project_data_from_CARS.csv"
final String headerMappingRelativePath = "grails-app/conf/resources/HeaderMappings.config"
//TODO rename this file, it is more general now
final String elementFieldMappingRelativePath = "grails-app/conf/resources/ElementFieldMapping.config"
final String username = "dlahr_CARS"
final String projectIdsToLoadFileRelativePath = "test/exampleData/project_UID_dataset_1.csv"


final Date startDate = new Date()

try {
    final CarsBardMapping carsBardMapping = new CarsBardMapping(elementFieldMappingRelativePath)

    ProjectIdsToLoad projectIdsToLoadSet = new ProjectIdsToLoad()
    projectIdsToLoadSet.loadProjectIds(projectIdsToLoadFileRelativePath, 1)

    List<CarsProject> carsProjectList = (new CarsSpreadsheetReader(projectIdsToLoadSet)).readProjectsFromFile(inputFileRelativePath, headerMappingRelativePath)

    (new CarsSpreadsheetValidator()).validateProjects(carsProjectList)

    Project.withTransaction { DefaultTransactionStatus status ->
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

        //comment out to commit
//        status.setRollbackOnly()
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
    double runningTime = ((double)(endDate.time - startDate.time)) / 60000.0
    Log.logger.info("time to run(min): " + runningTime)
    Log.close()
}

println("Finished with script")

return




