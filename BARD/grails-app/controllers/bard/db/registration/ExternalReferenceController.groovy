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

package bard.db.registration

import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.project.Project
import grails.validation.Validateable
import groovy.transform.InheritConstructors

import javax.servlet.http.HttpServletResponse

class ExternalReferenceController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def create(String ownerClass, Long ownerId, Long xRefId) {
        boolean isEdit = xRefId ? true : false
        ExternalReference externalReferenceInstance
        if(xRefId){
            externalReferenceInstance = ExternalReference.findById(xRefId)
            if(!externalReferenceInstance)
                flash.message = "External-reference not found."
        }
        else {
            externalReferenceInstance = new ExternalReference()
            if (ownerClass == "Experiment" && ownerId) {
                Experiment experiment = Experiment.findById(ownerId)
                externalReferenceInstance.experiment = experiment
            } else if (ownerClass == "Project" && ownerId) {
                Project project = Project.findById(ownerId)
                externalReferenceInstance.project = project
            } else {
                render(status: HttpServletResponse.SC_BAD_REQUEST, text: "A project or an experiment is required")
            }
        }
        return [externalReferenceInstance: externalReferenceInstance, isEdit: isEdit]
    }

    def save(ExternalReferenceCommand externalReferenceCommand) {
        def externalReferenceInstance = new ExternalReference()

        //bind command-object data
        bindData(externalReferenceInstance, externalReferenceCommand.properties, [include: ['experiment', 'project', 'extAssayRef', 'externalSystem']])

        if (externalReferenceInstance.save(flush: true)) {
            flash.message = message(code: 'default.created.message', default: "Successfully created a new external-reference")
        } else {
            flash.message = message(code: 'default.created.error.message', default: "Failed to create a new external-reference")
        }

        render(view: "create", model: [externalReferenceInstance: externalReferenceInstance, isEdit: false])
    }

    def edit(ExternalReferenceCommand externalReferenceCommand){
        ExternalReference externalReferenceInstance = ExternalReference.findById(externalReferenceCommand.id)
        if(externalReferenceInstance) {
            bindData(externalReferenceInstance, externalReferenceCommand.properties, [include: ['experiment', 'project', 'extAssayRef', 'externalSystem']])
            if (externalReferenceInstance.save(flush: true)) {
                flash.message = message(code: 'default.update.message', default: "Successfully updated the external-reference")
            } else {
                flash.message = message(code: 'default.update.error.message', default: "Failed to update the external-reference")
            }
        }
        else {
            flash.message = message(code: 'default.update.error.message', default: "Unable to update the external-reference. External-reference not found.")
        }
        render(view: "create", model: [externalReferenceInstance: externalReferenceInstance, isEdit: true])

    }

    def delete(String ownerClass, Long ownerId, Long xRefId){
        ExternalReference xRef = ExternalReference.findById(xRefId)
        if(xRef) {
            if (ownerClass == "Experiment" && ownerId) {
                Experiment experiment = Experiment.findById(ownerId)
                experiment.removeFromExternalReferences(xRef)
                deleteExternalReference(xRef)
                redirect(controller: "experiment", action: "show", params: [id: experiment.id])
            }
            else if (ownerClass == "Project" && ownerId) {
                Project project = Project.findById(ownerId)
                project.removeFromExternalReferences(xRef)
                deleteExternalReference(xRef)
                redirect(controller: "project", action: "show", params: [id: project.id])
            } else {
                render(status: HttpServletResponse.SC_BAD_REQUEST, text: "A project or an experiment is required")
            }
        }
        else {
            flash.message = "Unable to delete the external-reference. External-reference not found."
            if (ownerClass == "Experiment" && ownerId) {
                redirect(controller: "experiment", action: "show", params: [id: ownerId])
            }
            else if (ownerClass == "Project" && ownerId) {
                redirect(controller: "project", action: "show", params: [id: ownerId])
            }
            else {
                render(status: HttpServletResponse.SC_BAD_REQUEST, text: "A project or an experiment is required")
            }
        }
    }

    private def deleteExternalReference(ExternalReference xRef){
        String xRefDisplayName = "${xRef.externalSystem.systemName} ${xRef.extAssayRef}"
        try{
            xRef.delete(flush: true)
            flash.success = "Successfully deleted external-reference: $xRefDisplayName"
        }
        catch (Exception e){
            e.printStackTrace()
            flash.error = "Failed to delete external-reference: $xRefDisplayName"
        }
    }
}


@InheritConstructors
@Validateable
class ExternalReferenceCommand extends BardCommand {
    Long id
    ExternalSystem externalSystem
    Experiment experiment
    Project project
    String extAssayRef
    Date dateCreated
    Date lastUpdated
    String modifiedBy

}
