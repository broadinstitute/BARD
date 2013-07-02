package bard.db

import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.registration.Assay
import org.springframework.security.access.prepost.PreAuthorize

class ContextService {

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    String updatePreferredAssayContextName(Long id, AbstractContext context, String preferredName) {
        return updatePreferredContextName(context, preferredName)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    String updatePreferredProjectContextName(Long id, AbstractContext context, String preferredName) {
        return updatePreferredContextName(context, preferredName)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.experiment.Experiment',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    String updatePreferredExperimentContextName(Long id, AbstractContext context, String preferredName) {
        return updatePreferredContextName(context, preferredName)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    AbstractContext createAssayContext(Long id, AbstractContextOwner owningContext, String name, String section) {
        return createContext(owningContext, name, section)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    AbstractContext createProjectContext(Long id, AbstractContextOwner owningContext, String name, String section) {
        return createContext(owningContext, name, section)
    }

    private AbstractContext createContext(AbstractContextOwner owner, String name, String section) {
        AbstractContext context = owner.createContext(contextName: name, contextGroup: section)
        context.save(flush: true)
        return context
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteAssayContext(Long id, AbstractContextOwner owner, AbstractContext context) {
        deleteContext(owner, context)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteProjectContext(Long id, AbstractContextOwner owner, AbstractContext context) {
        deleteContext(owner, context)
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
