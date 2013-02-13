package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure

class ExperimentService {

    Experiment createNewExperiment(Assay assay, String experimentName, String description) {
        Experiment experiment = new Experiment(assay: assay)
        experiment.experimentName = experimentName
        experiment.description = description
        experiment.dateCreated = new Date()
        if (experiment.save(flush: true)) {
            populateMeasures(experiment)
        }

        return experiment
    }

    void populateMeasures(Experiment experiment) {
        Map measureToExpMeasure = [:]

        experiment.assay.measures.each { Measure measure ->
            ExperimentMeasure expMeasure = new ExperimentMeasure(experiment: experiment, measure: measure, dateCreated: new Date())
            measureToExpMeasure[measure] = expMeasure
        }

        measureToExpMeasure.values().each { ExperimentMeasure child ->
            if (child.measure.parentMeasure != null) {
                ExperimentMeasure parent = measureToExpMeasure[child.measure.parentMeasure]
                child.parentChildRelationship = "Derived from"
                child.parent = parent
            }
        }

        experiment.experimentMeasures = new HashSet(measureToExpMeasure.values())
    }
}
