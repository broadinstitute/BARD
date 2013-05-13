package registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ReadyForExtraction
import bard.db.registration.*

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
        String assayNamePrefix = ""
        Assay newAssay = cloneAssayOnly(assay, assay.dateCreated, assay.designedBy, assayNamePrefix, assay.assayStatus, assay.readyForExtraction)
        Map<AssayContext, AssayContext> assayContextOldToNew = cloneContexts(assay, newAssay)
        cloneDocuments(assay, newAssay)
        // clone all measures
        Map<Measure, Measure> measureOldToNew = cloneMeasures(assay, newAssay)
        assignParentMeasures(assay, measureOldToNew)

        cloneContextsMeasures(assay, assayContextOldToNew, measureOldToNew)

        return [assay: newAssay, measureOldToNew: measureOldToNew]
    }
    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments and documents)
     */
    Assay cloneAssayForEditing(Assay assay, String designedBy) {
        Assay newAssay = cloneAssayOnly(assay, new Date(), designedBy)
        if (newAssay.assayType == AssayType.TEMPLATE) { //convert templates to regular
            newAssay.assayType = AssayType.REGULAR
        }
        newAssay.save(flush: true)

        Map<AssayContext, AssayContext> assayContextOldToNew = cloneContexts(assay, newAssay)
        // clone all measures
        Map<Measure, Measure> measureOldToNew = cloneMeasures(assay, newAssay)
        assignParentMeasures(assay, measureOldToNew)

        cloneContextsMeasures(assay, assayContextOldToNew, measureOldToNew)
        return newAssay

    }

    Assay cloneAssayOnly(Assay assay,
                         Date dateCreated,
                         String designedBy,
                         String assayNamePrefix = "Clone of ",
                         AssayStatus assayStatus = AssayStatus.DRAFT,
                         ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY) {

        String assayName = assayNamePrefix + assay.assayName
        //we do not want to go over the max number of characters
        if (assayName.length() >= Assay.ASSAY_NAME_MAX_SIZE) {
            assayName = assayName?.trim().substring(0, Assay.ASSAY_NAME_MAX_SIZE)
        }

        return new Assay(
                assayStatus: assayStatus,
                assayShortName: assay.assayShortName,
                assayName: assayName,
                assayVersion: assay.assayVersion,
                designedBy: designedBy,
                readyForExtraction: readyForExtraction,
                dateCreated: dateCreated
        )

    }

    Map<AssayContext, AssayContext> cloneContexts(Assay assay, Assay newAssay) {
        Map<AssayContext, AssayContext> assayContextOldToNew = [:]

        for (context in assay.assayContexts) {
            AssayContext newContext = context.clone(newAssay)
            assayContextOldToNew[context] = newContext

            newContext.save(validate: false)
        }
        return assayContextOldToNew
    }

    void cloneDocuments(Assay assay, Assay clonedAssay) {
        for (document in assay.assayDocuments) {
            AssayDocument newDocument = document.clone()
            clonedAssay.addToAssayDocuments(newDocument)

            newDocument.save(failOnError: true)
        }
    }

    Map<Measure, Measure> cloneMeasures(Assay assay, Assay clonedAssay) {
        Map<Measure, Measure> measureOldToNew = [:]
        for (measure in assay.measures) {
            Measure newMeasure = measure.clone()

            measureOldToNew[measure] = newMeasure

            clonedAssay.addToMeasures(newMeasure)
        }
        return measureOldToNew
    }

    void assignParentMeasures(Assay assay, Map<Measure, Measure> measureOldToNew) {
        // assign parent measures now that all measures have been created
        for (measure in assay.measures) {
            measureOldToNew[measure].parentMeasure = measureOldToNew[measure.parentMeasure]
        }
        for (measure in measureOldToNew.values()) {
            measure.save(failOnError: true)
        }
    }

    void cloneContextsMeasures(Assay assay, Map<AssayContext, AssayContext> assayContextOldToNew, Map<Measure, Measure> measureOldToNew) {
        Set<AssayContextMeasure> assayContextMeasures = assay.measures.collectMany { it.assayContextMeasures }

        for (assayContextMeasure in assayContextMeasures) {
            cloneContextsMeasure(assayContextMeasure, assayContextOldToNew, measureOldToNew)
        }
    }

    void cloneContextsMeasure(AssayContextMeasure assayContextMeasure, Map<AssayContext, AssayContext> assayContextOldToNew, Map<Measure, Measure> measureOldToNew) {


        AssayContext newAssayContext = assayContextOldToNew[assayContextMeasure.assayContext]
        Measure newMeasure = measureOldToNew[assayContextMeasure.measure]

        AssayContextMeasure newAssayContextMeasure = new AssayContextMeasure()

        newAssayContext.addToAssayContextMeasures(newAssayContextMeasure)
        newMeasure.addToAssayContextMeasures(newAssayContextMeasure)

        newAssayContextMeasure.save(failOnError: true)

    }


}
