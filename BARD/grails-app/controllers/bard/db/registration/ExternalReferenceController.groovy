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
