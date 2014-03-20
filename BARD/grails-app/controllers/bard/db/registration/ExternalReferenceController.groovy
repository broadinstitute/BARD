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

//    def index() {
//        redirect(action: "list", params: params)
//    }
//
//    def list(Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//        [externalReferenceInstanceList: ExternalReference.list(params), externalReferenceInstanceTotal: ExternalReference.count()]
//    }
//
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

//    def show(Long id) {
//        def externalReferenceInstance = ExternalReference.get(id)
//        if (!externalReferenceInstance) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), id])
//            redirect(action: "list")
//            return
//        }
//
//        [externalReferenceInstance: externalReferenceInstance]
//    }
//
//    def edit(Long id) {
//        def externalReferenceInstance = ExternalReference.get(id)
//        if (!externalReferenceInstance) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), id])
//            redirect(action: "list")
//            return
//        }
//
//        [externalReferenceInstance: externalReferenceInstance]
//    }
//
//    def update(Long id, Long version) {
//        def externalReferenceInstance = ExternalReference.get(id)
//        if (!externalReferenceInstance) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), id])
//            redirect(action: "list")
//            return
//        }
//
//        if (version != null) {
//            if (externalReferenceInstance.version > version) {
//                externalReferenceInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
//                          [message(code: 'externalReference.label', default: 'ExternalReference')] as Object[],
//                          "Another user has updated this ExternalReference while you were editing")
//                render(view: "edit", model: [externalReferenceInstance: externalReferenceInstance])
//                return
//            }
//        }
//
//        externalReferenceInstance.properties = params
//
//        if (!externalReferenceInstance.save(flush: true)) {
//            render(view: "edit", model: [externalReferenceInstance: externalReferenceInstance])
//            return
//        }
//
//        flash.message = message(code: 'default.updated.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), externalReferenceInstance.id])
//        redirect(action: "show", id: externalReferenceInstance.id)
//    }
//
//    def delete(Long id) {
//        def externalReferenceInstance = ExternalReference.get(id)
//        if (!externalReferenceInstance) {
//            flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), id])
//            redirect(action: "list")
//            return
//        }
//
//        try {
//            externalReferenceInstance.delete(flush: true)
//            flash.message = message(code: 'default.deleted.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), id])
//            redirect(action: "list")
//        }
//        catch (DataIntegrityViolationException e) {
//            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'externalReference.label', default: 'ExternalReference'), id])
//            redirect(action: "show", id: id)
//        }
//    }
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
