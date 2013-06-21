package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class AssayDefinitionService {
    AssayService assayService

    Assay saveNewAssay(Assay assayInstance) {
        return assayInstance.save(flush: true)
    }

    Assay recomputeAssayShortName(Assay assay) {
        return assayService.recomputeAssayShortName(assay)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateAssayType(long id, AssayType assayType) {
        Assay assay = Assay.findById(id)
        assay.assayType = assayType
        assay.save(flush: true)
        return Assay.findById(id)
    }

    @PreAuthorize("hasPermission(#assayId, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateAssayStatus(long assayId, AssayStatus assayStatus) {
        Assay assay = Assay.findById(assayId)
        assay.assayStatus = assayStatus
        assay.save(flush: true)
        return Assay.findById(assayId)
    }

    @PreAuthorize("hasPermission(#assayId, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay updateAssayName(Long assayId, String newAssayName) {
        Assay assay = Assay.findById(assayId)
        assay.assayName = newAssayName
        //validate version here
        assay.save(flush: true)
        return Assay.findById(assayId)
    }

    @PreAuthorize("hasPermission(#assayId, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
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
