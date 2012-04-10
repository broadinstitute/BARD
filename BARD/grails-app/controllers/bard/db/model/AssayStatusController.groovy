package bard.db.model

import org.springframework.dao.DataIntegrityViolationException

class AssayStatusController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [assayStatusInstanceList: AssayStatus.list(params), assayStatusInstanceTotal: AssayStatus.count()]
    }

    def create() {
        [assayStatusInstance: new AssayStatus(params)]
    }

    def save() {
        def assayStatusInstance = new AssayStatus(params)
        if (!assayStatusInstance.save(flush: true)) {
            render(view: "create", model: [assayStatusInstance: assayStatusInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), assayStatusInstance.id])
        redirect(action: "show", id: assayStatusInstance.id)
    }

    def show() {
        def assayStatusInstance = AssayStatus.get(params.id)
        if (!assayStatusInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), params.id])
            redirect(action: "list")
            return
        }

        [assayStatusInstance: assayStatusInstance]
    }

    def edit() {
        def assayStatusInstance = AssayStatus.get(params.id)
        if (!assayStatusInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), params.id])
            redirect(action: "list")
            return
        }

        [assayStatusInstance: assayStatusInstance]
    }

    def update() {
        def assayStatusInstance = AssayStatus.get(params.id)
        if (!assayStatusInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (assayStatusInstance.version > version) {
                assayStatusInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'assayStatus.label', default: 'AssayStatus')] as Object[],
                          "Another user has updated this AssayStatus while you were editing")
                render(view: "edit", model: [assayStatusInstance: assayStatusInstance])
                return
            }
        }

        assayStatusInstance.properties = params

        if (!assayStatusInstance.save(flush: true)) {
            render(view: "edit", model: [assayStatusInstance: assayStatusInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), assayStatusInstance.id])
        redirect(action: "show", id: assayStatusInstance.id)
    }

    def delete() {
        def assayStatusInstance = AssayStatus.get(params.id)
        if (!assayStatusInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), params.id])
            redirect(action: "list")
            return
        }

        try {
            assayStatusInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'assayStatus.label', default: 'AssayStatus'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
