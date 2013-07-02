package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.project.Project
import bard.db.project.ProjectDocument
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class DocumentService {


    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteAssayDocument(Long id, AssayDocument assayDocument) {
       assayDocument.delete()
    }
    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteProjectDocument(Long id, ProjectDocument projectDocument) {
        projectDocument.delete()
    }
}
