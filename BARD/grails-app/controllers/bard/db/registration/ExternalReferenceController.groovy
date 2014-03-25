package bard.db.registration

import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.project.Project
import org.springframework.dao.DataIntegrityViolationException
import grails.validation.Validateable
import groovy.transform.InheritConstructors

import javax.servlet.http.HttpServletResponse

class ExternalReferenceController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def create(String ownerClass, Long ownerId) {
        ExternalReference externalReferenceInstance = new ExternalReference()
        if (ownerClass == "Experiment" && ownerId) {
            Experiment experiment = Experiment.findById(ownerId)
            externalReferenceInstance.experiment = experiment
        } else if (ownerClass == "Project" && ownerId) {
            Project project = Project.findById(ownerId)
            externalReferenceInstance.project = project
        } else {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "A project or an experiment is required")
        }

        return [externalReferenceInstance: externalReferenceInstance]
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

        render(view: "create", model: [externalReferenceInstance: externalReferenceInstance])
    }
}


@InheritConstructors
@Validateable
class ExternalReferenceCommand extends BardCommand {

    ExternalSystem externalSystem
    Experiment experiment
    Project project
    String extAssayRef
    Date dateCreated
    Date lastUpdated
    String modifiedBy

}
