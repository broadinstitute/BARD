package merge

import bard.db.enums.AssayStatus
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.Assay
import bard.db.registration.AssayContext

class MergeAssayService {

    /**
     * This is the assay we want to keep
     * @param assays
     * @return the assay we want to keep
     */
    private Assay keepAssay(List<Assay> assays) {
        Assay assay = assays.max {  // keep assay that having the largest number of contextItems
            it.assayContextItems.size()
        }
        println("Assay ${assay.id} has max # of contextItem ${assay.assayContextItems.size()}, keep it")
        return assay
    }

    private List<Assay> assaysNeedToRemove(List<Assay> assays, Assay assayWillKeep) {
        List<Assay> removingAssays = []
        assays.each {
            if (it.id != assayWillKeep.id)
                removingAssays.add(it)
        }
        return removingAssays
    }

    //experiments:  change the experiments associated with all the removingAssays in the duplicate set so that they point to the single kept  assay
    def handleExperiments(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {

        List<Experiment> experiments = []
        removingAssays.each { Assay assay ->
            experiments.addAll(assay.experiments)
        }
        moveExperiments(experiments, assayWillKeep, modifiedBy)
    }

    def moveExperiments(List<Experiment> experiments, Assay assayWillKeep, String modifiedBy) {
        int addExperimentToKept = 0 // count number of experiments added to kept assays
        experiments.each { Experiment experiment ->
            Experiment found = assayWillKeep.experiments.find { it.id == experiment.id }
            if (!found) {
                addExperimentToKept++
                experiment.modifiedBy = modifiedBy + "-movedFromA-${experiment?.assay?.id}"
                experiment.assay.removeFromExperiments(experiment)

                experiment.assay = assayWillKeep
                assayWillKeep.addToExperiments(experiment)
            }
        }
        println("Total candidate experiments: ${experiments.size()}, added ${addExperimentToKept}")
        assayWillKeep.save(failOnError: true)
    }

    def constructMeasureKey(ExperimentMeasure measure) {
        String key = "r=${measure.resultType.id} s=${measure.statsModifier?.id}"
        if (measure.parent != null) {
            key = "${constructMeasureKey(measure.parent)}, ${key}";
        }
        return key
    }

    /**
     *
     */
    void handleMeasures(Assay target, Collection<Assay> originalAssays) {
        for(assay in originalAssays) {
            for(experiment in assay.experiments) {
                for(measure in experiment.experimentMeasures) {
                    for(link in measure.assayContextExperimentMeasures) {
                        AssayContext sourceContext = link.assayContext
                        sourceContext.removeFromAssayContextExperimentMeasures(link)
                        AssayContext targetContext = findOrCreateEquivalentContext(target, sourceContext)
                        targetContext.addToAssayContextExperimentMeasures(link)
                    }
                }
            }
        }
    }

    AssayContext findOrCreateEquivalentContext(Assay target, AssayContext context) {
        return context.clone(target)
    }


    def updateStatus(List<Assay> assays, String modifiedBy) {
        assays.each { Assay assay ->
            assay.assayStatus = AssayStatus.RETIRED
            assay.modifiedBy = modifiedBy
            if (!assay.save(failOnError: true)) {
                println("Error happened when update assay status ${assay.errors}")
            }
        }
    }
}
