package registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ReadyForExtraction
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayDocument

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
        newAssay = newAssay.save(flush: true)
        Map<AssayContext, AssayContext> assayContextOldToNew = cloneContexts(assay, newAssay, false)
        cloneDocuments(assay, newAssay)
        // clone all measures
        //Map<Measure, Measure> measureOldToNew = cloneMeasures(assay, newAssay)
        //assignParentMeasures(assay, measureOldToNew)

        //cloneContextsMeasures(assay, assayContextOldToNew, measureOldToNew)

        return [assay: newAssay, measureOldToNew: [:]]
    }
    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments and documents)
     */
    Assay cloneAssayForEditing(Assay assay, String designedBy) {
        Assay newAssay = cloneAssayOnly(assay, new Date(), designedBy)
        if (newAssay.assayType == AssayType.TEMPLATE) { //convert templates to regular
            newAssay.assayType = AssayType.REGULAR
        }
        newAssay.save(flush: true, validate: false)

        Map<AssayContext, AssayContext> assayContextOldToNew = cloneContexts(assay, newAssay, false)
        // clone all measures
        //Map<Measure, Measure> measureOldToNew = cloneMeasures(assay, newAssay)
        //assignParentMeasures(assay, measureOldToNew)

        //  cloneContextsMeasures(assay, assayContextOldToNew, measureOldToNew)
        newAssay.save(flush: true, failOnError: true, validate: false)

        //now call the manage names stored procedure
        //then look up and return the assay
        return Assay.findById(newAssay.id)
    }

    /**
     *
     */
    Assay recomputeAssayShortName(Assay assay) {
//  DISABLING until rollback problem has been addressed
//        Assay.withSession { session ->
//            session.createSQLQuery("""BEGIN MANAGE_NAMES.UPDATE_ASSAY_SHORT_NAME('${assay.id}'); END;""").executeUpdate()
//        }
        //now call the manage names stored procedure
        //then look up and return the assay
//        return Assay.findById(assay.id)
        return assay
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
        //assay version should always be set to 1 after colining
        return new Assay(
                assayStatus: assayStatus,
                assayShortName: assay.assayShortName,
                assayName: assayName,
                assayVersion: "1",
                designedBy: designedBy,
                readyForExtraction: readyForExtraction,
                dateCreated: dateCreated
        )
    }

    Map<AssayContext, AssayContext> cloneContexts(Assay assay, Assay newAssay, boolean validate) {
        Map<AssayContext, AssayContext> assayContextOldToNew = [:]

        for (context in assay.assayContexts) {
            AssayContext newContext = context.clone(newAssay)
            assayContextOldToNew[context] = newContext

            newContext.save(failOnError: true, validate: validate)

            // this shouldn't be necessary, but it appears that if you save, bypassing validating, any invalid items
            // were not actually getting written to the db (even though they _would_ get an ID assigned)
            for (item in newContext.contextItems) {
                item.save(failOnError: true, validate: validate)
            }
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
    /**
     *
     * @param assay
     * @param clonedAssay
     * @return
     */
    @Deprecated
    Map cloneMeasures(Assay assay, Assay clonedAssay) {
//        Map<Measure, Measure> measureOldToNew = [:]
//        for (measure in assay.measures) {
//            Measure newMeasure = measure.clone()
//
//            measureOldToNew[measure] = newMeasure
//
//            clonedAssay.addToMeasures(newMeasure)
//        }
        // return measureOldToNew
        return [:]
    }

    @Deprecated
    void assignParentMeasures(Assay assay, Map measureOldToNew) {
        // assign parent measures now that all measures have been created
        /*for (measure in assay.measures) {
            measureOldToNew[measure].parentMeasure = measureOldToNew[measure.parentMeasure]
        }
        for (measure in measureOldToNew.values()) {
            measure.save(failOnError: true)
        }*/
    }

    @Deprecated
    void cloneContextsMeasures(Assay assay, Map<AssayContext, AssayContext> assayContextOldToNew, Map measureOldToNew) {
//        Set<AssayContextMeasure> assayContextMeasures = assay.measures.collectMany { it.assayContextMeasures }
//
//        for (assayContextMeasure in assayContextMeasures) {
//            cloneContextsMeasure(assayContextMeasure, assayContextOldToNew, measureOldToNew)
//        }
    }

    @Deprecated
    void cloneContextsMeasure(def assayContextMeasure, Map<AssayContext, AssayContext> assayContextOldToNew, Map measureOldToNew) {

        /* AssayContext newAssayContext = assayContextOldToNew[assayContextMeasure.assayContext]
         Measure newMeasure = measureOldToNew[assayContextMeasure.measure]

         AssayContextMeasure newAssayContextMeasure = new AssayContextMeasure()

         newAssayContext.addToAssayContextMeasures(newAssayContextMeasure)
         newMeasure.addToAssayContextMeasures(newAssayContextMeasure)

         newAssayContextMeasure.save(failOnError: true)  */

    }


}
