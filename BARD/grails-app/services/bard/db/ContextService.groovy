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

import bard.db.enums.ContextType
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
    AbstractContext createAssayContext(Long id, AbstractContextOwner owningContext, String name, ContextType section) {
        return createContext(owningContext, name, section)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.project.Project',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    AbstractContext createProjectContext(Long id, AbstractContextOwner owningContext, String name, ContextType section) {
        return createContext(owningContext, name, section)
    }

    @PreAuthorize("hasPermission(#id,'bard.db.experiment.Experiment',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    AbstractContext createExperimentContext(Long id, AbstractContextOwner owningContext, String name, ContextType section) {
        return createContext(owningContext, name, section)
    }

    private AbstractContext createContext(AbstractContextOwner owner, String name, ContextType section) {
        AbstractContext context = owner.createContext(contextName: name, contextType: section)
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

    @PreAuthorize("hasPermission(#id,'bard.db.experiment.Experiment',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deleteExperimentContext(Long id, AbstractContextOwner owner, AbstractContext context) {
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
