package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.project.Project
import bard.db.project.ProjectDocument
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService

class DocumentService {


    @PreAuthorize("hasPermission(#assay, admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteAssayDocument(Assay assay, AssayDocument assayDocument) {
       assayDocument.delete()
    }
    @PreAuthorize("hasPermission(#project, admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteProjectDocument(Project project, ProjectDocument projectDocument) {
        projectDocument.delete()
    }
}
