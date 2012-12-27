package bard.dm.cars.domainspreadsheetmapping

import bard.db.experiment.Experiment
import bard.dm.cars.spreadsheet.CarsExperiment
import bard.db.registration.ExternalReference

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 8:19 AM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentPair {
    Experiment experiment
    CarsExperiment carsExperiment

    ExternalReference externalReference

    boolean isPossibleProjectSummaryAid
}
