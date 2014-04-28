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

package merge

import bard.db.enums.Status
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

    List<Long> retireAssaysWithNoExperiments(List<Assay> assays, String modifiedBy) {
        List<Long> assaysWithError = []
        assays.each { Assay assay ->
            if(!assay.experiments){
                assay.assayStatus = Status.RETIRED
                assay.modifiedBy = modifiedBy
                if (!assay.save(flush: true)) {
                    assaysWithError.add(assay.id)
                    println("Error happened when update assay status ${assay.errors}")
                }
            }
        }
        return assaysWithError
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
            assay.assayStatus = Status.RETIRED
            assay.modifiedBy = modifiedBy
            if (!assay.save(failOnError: true)) {
                println("Error happened when update assay status ${assay.errors}")
            }
        }
    }
}
