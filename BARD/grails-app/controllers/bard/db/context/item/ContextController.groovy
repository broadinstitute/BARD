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

package bard.db.context.item

import bard.db.ContextService
import bard.db.enums.ContextType
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.ExperimentController
import bard.db.project.Project
import bard.db.project.ProjectController
import bard.db.registration.Assay
import bard.db.registration.AssayDefinitionController
import grails.plugins.springsecurity.Secured
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse

@Secured(['isAuthenticated()'])
class ContextController {
    ContextService contextService

    def addCard(String contextClass, Long ownerId, String cardSection){
        render(view: 'addCard', model: [contextClass:contextClass, ownerId: ownerId, cardSection: cardSection, ownerController: BasicContextItemCommand.getOwnerController(contextClass)])
    }

    def createCard(String contextClass, Long ownerId, String cardName, String cardSection) {
        ContextType contextType = ContextType.byId(cardSection)
        if (ownerId == null) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "OwnerId is required", contentType: 'text/plain', template: null)
            return

        }
        AbstractContextOwner owningContext = BasicContextItemCommand.getContextOwnerClass(contextClass).findById(ownerId)
        try {
            if (owningContext instanceof Assay) {
                contextService.createAssayContext(owningContext.id, owningContext, cardName, contextType)
            }
            if (owningContext instanceof Project) {
                contextService.createProjectContext(owningContext.id, owningContext, cardName, contextType)
            }
            if (owningContext instanceof Experiment) {
                contextService.createExperimentContext(owningContext.id, owningContext, cardName, contextType)
            }
        } catch (AccessDeniedException ae) {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)
            return
        }
        redirect(controller: BasicContextItemCommand.getOwnerController(contextClass), action:'show', id:ownerId )
    }

    def deleteEmptyCard(String contextClass, Long contextId, String section) {
        AbstractContext context = BasicContextItemCommand.getContextClass(contextClass).findById(contextId)
        AbstractContextOwner owningContext = context.owner
        try {
            if (owningContext instanceof Assay) {
                contextService.deleteAssayContext(owningContext.id, owningContext, context)
            }
            if (owningContext instanceof Project) {
                contextService.deleteProjectContext(owningContext.id, owningContext, context)
            }
            if (owningContext instanceof Experiment) {
                contextService.deleteExperimentContext(owningContext.id, owningContext, context)
            }
        } catch (AccessDeniedException ae) {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)
            return
        }
        String controller
        switch (contextClass) {
            case 'AssayContext':
                controller = AssayDefinitionController.simpleName
                break
            case 'ProjectContext':
                controller = ProjectController.simpleName
                break
            case 'ExperimentContext':
                controller = ExperimentController.simpleName
                break
            default:
                controller = null
                break
        }
        controller = controller.replaceAll('Controller', '')
        redirect(controller: controller, action: 'show', params: ['id': owningContext.id])
    }

}
