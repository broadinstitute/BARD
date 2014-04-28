/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import org.apache.commons.lang.StringUtils

Log.initializeLogger("data/maas/cars/output/parseCarsSpreadsheet.log")
println("Start script")

//constants
final String inputFileRelativePath = "data/maas/cars/project_data_from_CARS.csv"
final String headerMappingRelativePath = "grails-app/conf/resources/HeaderMappings.config"
//TODO rename this file, it is more general now
final String elementFieldMappingRelativePath = "grails-app/conf/resources/ElementFieldMapping.config"
final String username = "dlahr_CARS"
//final String projectIdsToLoadFileRelativePath = "data/maas/cars/project_UID_dataset.csv"

String dataset_id = System.getProperty("datasetid")
String willCommit = System.getProperty("willcommit")
if (!dataset_id && !StringUtils.isNumeric(dataset_id)) {
    println("Please provide a valid dataset id: -Ddatasetid=validid")
    return
}
if (!willCommit) {
    println("This will be a dry run to check if there is any errors need to be fixed in spreadsheet.")
    println("If you are sure you need to commit: -Dwillcommit=true|false")
}
def projectUidsToLoad
Project.withSession { session ->
    projectUidsToLoad = session.createSQLQuery("""
        select distinct project_uid from bard_data_qa_dashboard.dataset_project_uid where dataset_id=${dataset_id} order by project_uid
  """).setCacheable(false).list().collect {it.longValue()}
}

println (projectUidsToLoad)
if (!projectUidsToLoad) {
    println("No project UID to load")
    return
}

final Date startDate = new Date()

try {
    final CarsBardMapping carsBardMapping = new CarsBardMapping(elementFieldMappingRelativePath)

//    ProjectIdsToLoad projectIdsToLoadSet = new ProjectIdsToLoad()
//    projectIdsToLoadSet.loadProjectIds(projectIdsToLoadFileRelativePath, 1)


    List<CarsProject> carsProjectList = (new CarsSpreadsheetReader()).readProjectsFromFile(inputFileRelativePath, headerMappingRelativePath, projectUidsToLoad)

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

println("Finished with cars loading")




