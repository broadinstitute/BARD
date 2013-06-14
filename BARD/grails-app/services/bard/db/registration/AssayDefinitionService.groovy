package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import registration.AssayService

class AssayDefinitionService {
    AssayService assayService

    Assay saveNewAssay(Assay assayInstance) {
        return assayInstance.save(flush: true)
    }

    Assay recomputeAssayShortName(Assay assay) {
        return assayService.recomputeAssayShortName(assay)
    }

    Assay updateAssayType(long assayId, AssayType assayType) {
        Assay assay = Assay.findById(assayId)
        assay.assayType = assayType
        assay.save(flush: true)
        return Assay.findById(assayId)
    }

    Assay updateAssayStatus(long assayId, AssayStatus assayStatus) {
        Assay assay = Assay.findById(assayId)
        assay.assayStatus = assayStatus
        assay.save(flush: true)
        return Assay.findById(assayId)
    }

    Assay updateAssayName(Long assayId, String newAssayName) {
        Assay assay = Assay.findById(assayId)
        assay.assayName = newAssayName
        //validate version here
        assay.save(flush: true)
        return Assay.findById(assayId)
    }

    Assay updateDesignedBy(long assayId, String newDesigner) {
        Assay assay = Assay.findById(assayId)
        assay.designedBy = newDesigner
        assay.save(flush: true)
        return Assay.findById(assayId)
    }
    /**
     * Copy an assay new a new object, including all objects owned by this assay (but excluding any experiments and documents)
     */
    Assay cloneAssayForEditing(Assay assay, String designedBy) {
        return assayService.cloneAssayForEditing(assay, designedBy)
    }
}
