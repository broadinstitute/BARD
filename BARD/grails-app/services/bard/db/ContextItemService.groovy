/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
                    updateSuccessful = copyFromCmdToDomain(contextItem)
                    updateSuccessful &= attemptSave(contextItem)
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
            createSuccessful = copyFromCmdToDomain(contextItem)
            if (createSuccessful) {
                context.addContextItem(contextItem)
                if (attemptSave(contextItem)) {
                    copyFromDomainToCmd(contextItem)
                    createSuccessful = true
                }
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
            context = basicContextItemCommand.findContext()
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
