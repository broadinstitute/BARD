package bard.dm.cars.domainspreadsheetmapping

import bard.db.project.Project

import bard.dm.cars.spreadsheet.CarsProject
import bard.db.registration.ExternalReference
import bard.dm.cars.spreadsheet.CarsExperiment

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 8:26 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectPair {
    Project project
    CarsProject carsProject

    List<ExperimentPair> experimentPairList = new LinkedList<ExperimentPair>()

    Set<CarsExperiment> unmatchedCarsExperiments = new HashSet<CarsExperiment>()
    Set<ExternalReference> unmatchedExternalReferences = new HashSet<ExternalReference>()

    Set<ProjectExperimentPair> projectExperimentPairSet = new HashSet<ProjectExperimentPair>()
}
