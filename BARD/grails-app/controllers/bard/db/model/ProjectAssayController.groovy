package bard.db.model

import org.springframework.dao.DataIntegrityViolationException

class ProjectAssayController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [projectAssayInstanceList: ProjectAssay.list(params), projectAssayInstanceTotal: ProjectAssay.count()]
    }

    def create() {
        [projectAssayInstance: new ProjectAssay(params)]
    }

    def save() {
        def projectAssayInstance = new ProjectAssay(params)
        if (!projectAssayInstance.save(flush: true)) {
            render(view: "create", model: [projectAssayInstance: projectAssayInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), projectAssayInstance.id])
        redirect(action: "show", id: projectAssayInstance.id)
    }

    def show() {
        def projectAssayInstance = ProjectAssay.get(params.id)
        if (!projectAssayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), params.id])
            redirect(action: "list")
            return
        }

        [projectAssayInstance: projectAssayInstance]
    }

    def edit() {
        def projectAssayInstance = ProjectAssay.get(params.id)
        if (!projectAssayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), params.id])
            redirect(action: "list")
            return
        }

        [projectAssayInstance: projectAssayInstance]
    }

    def update() {
        def projectAssayInstance = ProjectAssay.get(params.id)
        if (!projectAssayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (projectAssayInstance.version > version) {
                projectAssayInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'projectAssay.label', default: 'ProjectAssay')] as Object[],
                          "Another user has updated this ProjectAssay while you were editing")
                render(view: "edit", model: [projectAssayInstance: projectAssayInstance])
                return
            }
        }

        projectAssayInstance.properties = params

        if (!projectAssayInstance.save(flush: true)) {
            render(view: "edit", model: [projectAssayInstance: projectAssayInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), projectAssayInstance.id])
        redirect(action: "show", id: projectAssayInstance.id)
    }

    def delete() {
        def projectAssayInstance = ProjectAssay.get(params.id)
        if (!projectAssayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), params.id])
            redirect(action: "list")
            return
        }

        try {
            projectAssayInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'projectAssay.label', default: 'ProjectAssay'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
