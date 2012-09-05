import bard.db.dictionary.Element
import bard.dm.cars.spreadsheet.CarsSpreadsheetReader
import bard.dm.cars.spreadsheet.CarsSpreadsheetValidator
import bard.dm.Log
import bard.dm.cars.domainspreadsheetmapping.ProjectPair
import bard.dm.cars.domainspreadsheetmapping.ProjectMapperBuilder
import bard.dm.cars.domainspreadsheetmapping.CarsBardMapping
import bard.dm.cars.domainspreadsheetmapping.ProjectAnnotater
import bard.db.experiment.StepContextItem
import bard.dm.cars.spreadsheet.CarsExperiment
import bard.dm.cars.spreadsheet.CarsProject
import bard.dm.cars.domainspreadsheetmapping.ProjectStepPair
import bard.dm.cars.domainspreadsheetmapping.ProjectStepMapperBuilder
import bard.dm.cars.domainspreadsheetmapping.ProjectStepAnnotater
import bard.db.experiment.Project
import bard.db.experiment.ProjectStep
import bard.db.experiment.ProjectContextItem
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import org.springframework.transaction.support.DefaultTransactionStatus

println("Start script")

//constants
final String inputFileRelativePath = "test/exampleData/Project data from CARS.csv"
final String headerMappingRelativePath = "grails-app/conf/resources/HeaderMappings.config"
//TODO rename this file, it is more general now
final String elementFieldMappingRelativePath = "grails-app/conf/resources/ElementFieldMapping.config"
final String username = "dlahr"



final Date startDate = new Date()

try {
    final CarsBardMapping carsBardMapping = new CarsBardMapping(elementFieldMappingRelativePath)

    List<CarsProject> carsProjectList = (new CarsSpreadsheetReader()).readProjectsFromFile(inputFileRelativePath, headerMappingRelativePath)

//    carsProjectList = carsProjectList.subList(0, 10)

    (new CarsSpreadsheetValidator()).validateProjects(carsProjectList)

    Project.withTransaction { DefaultTransactionStatus status ->
        List<ProjectPair> projectPairList = (new ProjectMapperBuilder()).buildProjectPairs(carsProjectList, username)

        (new ProjectStepMapperBuilder(carsBardMapping, username)).mapOrBuildProjectSteps(projectPairList)

        (new ProjectAnnotater(carsBardMapping, username)).addAnnotations(projectPairList)

        (new ProjectStepAnnotater(carsBardMapping, username)).addAnnotations(projectPairList)

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
} finally {
    Log.fileAppender.close()
    final Date endDate = new Date()
    double runningTime = ((double)(endDate.time - startDate.time)) / 60000.0
    Log.logger.info("time to run(min): " + runningTime)
}

println("Finished with script")

return




