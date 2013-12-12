package bard.db.registration

import bard.db.experiment.ExperimentDocument
import bard.db.project.ProjectDocument
import org.springframework.security.access.prepost.PreAuthorize

class DocumentService {


    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteAssayDocument(Long id, AssayDocument assayDocument) {
        assayDocument.delete()
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteProjectDocument(Long id, ProjectDocument projectDocument) {
        projectDocument.delete()
    }

    @PreAuthorize("hasPermission(#id,'bard.db.experiment.Experiment',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteExperimentDocument(Long id, ExperimentDocument experimentDocument) {
        experimentDocument.delete()
    }
}
