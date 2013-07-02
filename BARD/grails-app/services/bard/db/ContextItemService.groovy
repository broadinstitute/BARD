package bard.db

import bard.db.context.item.BasicContextItemCommand
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.registration.Assay
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 5/29/13
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
class ContextItemService {

    /**
     * lifting method out of command object to demarcate transactional boundary
     * @param basicContextItemCommand
     */
    private boolean updateContextItem(BasicContextItemCommand basicContextItemCommand) {
        boolean updateSuccessful = false
        basicContextItemCommand.with {
            AbstractContextItem contextItem = attemptFindItem()
            if (contextItem) {
                if (version?.longValue() != contextItem.version.longValue()) {
                    getErrors().reject('default.optimistic.locking.failure', [BasicContextItemCommand.getContextItemClass(getContextClass())] as Object[], 'optimistic lock failure')
                    copyFromDomainToCmd(contextItem)
                } else {
                    copyFromCmdToDomain(contextItem)
                    updateSuccessful = attemptSave(contextItem)
                    copyFromDomainToCmd(contextItem)
                }
            }
        }
        return updateSuccessful
    }
    /**
     * lifting method out of command object to demarcate transactional boundary
     * @param basicContextItemCommand
     */
    private boolean createContextItem(BasicContextItemCommand basicContextItemCommand) {
        boolean createSuccessful = false
        basicContextItemCommand.with {
            AbstractContextItem contextItem = getContextItemClass(basicContextItemCommand.contextClass).newInstance()
            copyFromCmdToDomain(contextItem)
            context.addContextItem(contextItem)
            if (attemptSave(contextItem)) {
                copyFromDomainToCmd(contextItem)
                createSuccessful = true
            }
        }
        return createSuccessful
    }


    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean createExperimentContextItem(Long id, BasicContextItemCommand basicContextItemCommand) {
        return createContextItem(basicContextItemCommand)
    }


    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean createAssayContextItem(Long id, BasicContextItemCommand basicContextItemCommand) {
        return createContextItem(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.project.Project', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean createProjectContextItem(Long id, BasicContextItemCommand basicContextItemCommand) {
        return createContextItem(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.experiment.Experiment',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean updateExperimentContextItem(Long id, BasicContextItemCommand basicContextItemCommand) {
        return updateContextItem(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean updateAssayContextItem(Long id, BasicContextItemCommand basicContextItemCommand) {
        return updateContextItem(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean updateProjectContextItem(Long id, BasicContextItemCommand basicContextItemCommand) {
        return updateContextItem(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean deleteAssayContextItem(Long id, AbstractContextOwner owner, BasicContextItemCommand basicContextItemCommand) {
        return delete(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean deleteProjectContextItem(Long id, AbstractContextOwner owner, BasicContextItemCommand basicContextItemCommand) {
        return delete(basicContextItemCommand)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.experiment.Experiment',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    boolean deleteExperimentContextItem(Long id, AbstractContextOwner owner, BasicContextItemCommand basicContextItemCommand) {
        return delete(basicContextItemCommand)
    }

    private boolean delete(BasicContextItemCommand basicContextItemCommand) {
        boolean deleteSuccessful = false
        basicContextItemCommand.with {
            AbstractContext context = basicContextItemCommand.findContext()
            if (context) {
                AbstractContextOwner owner = context.owner
                AbstractContextItem contextItem = context.contextItems.find { it.id == contextItemId }
                if (contextItem) {
                    context.contextItems.remove(contextItem)
                    contextItem.delete()
                    deleteSuccessful = true
                }
            }
        }
        return deleteSuccessful
    }
}
