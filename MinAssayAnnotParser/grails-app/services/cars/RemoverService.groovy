package cars

import bard.db.project.ProjectExperiment
import bard.db.project.Project
import bard.db.project.ProjectDocument
import bard.db.registration.ExternalReference
import bard.db.project.ProjectStep
import bard.db.project.ProjectContext

class RemoverService {
    def deleteProject(String loadedBy) {
        def projects = Project.findAllByModifiedByIlike(loadedBy)
        projects.each{Project project ->
            ProjectDocument.deleteAll(project.documents)
            ExternalReference.deleteAll(project.externalReferences)
            ProjectStep.deleteAll(project.projectSteps)
            ProjectExperiment.deleteAll(project.projectExperiments)
            ProjectContext.deleteAll(project.contexts)
            project.delete()
        }
    }
}
