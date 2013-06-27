package bard.db

import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.registration.Assay
import org.springframework.security.access.prepost.PreAuthorize

class ContextService {

    @PreAuthorize("hasPermission(#assay,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    String updatePreferredAssayContextName(Assay assay, AbstractContext context, String preferredName) {
       return updatePreferredContextName(context,preferredName)
    }
    @PreAuthorize("hasPermission(#project,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    String updatePreferredProjectContextName(Project project, AbstractContext context, String preferredName) {
        return updatePreferredContextName(context,preferredName)
    }
    @PreAuthorize("hasPermission(#experiment,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    String updatePreferredExperimentContextName(Experiment experiment, AbstractContext context, String preferredName) {
        return updatePreferredContextName(context,preferredName)
    }

    @PreAuthorize("hasPermission(#assay,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    AbstractContext createAssayContext(Assay assay, String name, String section) {
        return createContext(assay, name, section)
    }

    @PreAuthorize("hasPermission(#project,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    AbstractContext createProjectContext(Project project, String name, String section) {
        return createContext(project, name, section)
    }

    private AbstractContext createContext(AbstractContextOwner owner, String name, String section) {
        AbstractContext context = owner.createContext(contextName: name, contextGroup: section)
        context.save(flush: true)
        return context
    }

    @PreAuthorize("hasPermission(#assay,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteAssayContext(Assay assay, AbstractContext context) {
        deleteContext(assay, context)
    }

    @PreAuthorize("hasPermission(#project,admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteProjectContext(Project project, AbstractContext context) {
        deleteContext(project, context)
    }

    private void deleteContext(AbstractContextOwner owner, AbstractContext context) {
        owner.removeContext(context)
        context.delete()
    }

    private String updatePreferredContextName(AbstractContext context, String preferredName) {
        context.preferredName = preferredName
        context.save(flush: true)
        return context.preferredName
    }


}
