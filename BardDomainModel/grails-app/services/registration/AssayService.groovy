package registration

import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextMeasure
import bard.db.registration.AssayDocument
import bard.db.registration.Measure

class AssayService {

    List<Assay> findByPubChemAid(Long aid) {
        def criteria = Assay.createCriteria()
        return criteria.listDistinct {
            experiments {
                externalReferences {
                    eq('extAssayRef', "aid=${aid.toString()}")
                }
            }
        }
    }

    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments)
     */
    Map cloneAssay(Assay assay) {
        Map<AssayContext, AssayContext> assayContextOldToNew = [:]

        Assay newAssay = new Assay(assayStatus: assay.assayStatus,
                assayShortName: assay.assayShortName,
                assayName: assay.assayName,
                assayVersion: assay.assayVersion,
                designedBy: assay.designedBy,
                readyForExtraction: assay.readyForExtraction);

        for(context in assay.assayContexts) {
            AssayContext newContext = context.clone(newAssay)
            assayContextOldToNew[context] = newContext

            newContext.save(failOnError: true)
        }

        for(document in assay.assayDocuments) {
            AssayDocument newDocument = document.clone()
            newAssay.addToAssayDocuments(newDocument)

            newDocument.save(failOnError: true)
        }

        // clone all measures
        Map<Measure, Measure> measureOldToNew = [:]
        for(measure in assay.measures) {
            Measure newMeasure = measure.clone()

            measureOldToNew[measure] = newMeasure

            newAssay.addToMeasures(newMeasure)
        }

        // assign parent measures now that all measures have been created
        for(measure in assay.measures) {
            measureOldToNew[measure].parentMeasure = measureOldToNew[measure.parentMeasure]
        }

        for(measure in measureOldToNew.values()) {
            measure.save(failOnError: true)
        }

        // clone assay context measures
        Set<AssayContextMeasure> assayContextMeasures = assay.measures.collectMany { it.assayContextMeasures }
        for(assayContextMeasure in assayContextMeasures) {
            AssayContext newAssayContext = assayContextOldToNew[assayContextMeasure.assayContext]
            Measure newMeasure =  measureOldToNew[assayContextMeasure.measure]

            AssayContextMeasure newAssayContextMeasure = new AssayContextMeasure()

            newAssayContext.addToAssayContextMeasures(newAssayContextMeasure)
            newMeasure.addToAssayContextMeasures(newAssayContextMeasure)

            newAssayContextMeasure.save(failOnError: true)
        }

        return [assay: newAssay, measureOldToNew: measureOldToNew]
    }

}
