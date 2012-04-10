package bard.db.model

import org.springframework.dao.DataIntegrityViolationException

class ElementStatusController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [elementStatusInstanceList: ElementStatus.list(params), elementStatusInstanceTotal: ElementStatus.count()]
    }

    def create() {
        [elementStatusInstance: new ElementStatus(params)]
    }

    def save() {
        def elementStatusInstance = new ElementStatus(params)
        if (!elementStatusInstance.save(flush: true)) {
            render(view: "create", model: [elementStatusInstance: elementStatusInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), elementStatusInstance.id])
        redirect(action: "show", id: elementStatusInstance.id)
    }

    def show() {
        def elementStatusInstance = ElementStatus.get(params.id)
        if (!elementStatusInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), params.id])
            redirect(action: "list")
            return
        }

        [elementStatusInstance: elementStatusInstance]
    }

    def edit() {
        def elementStatusInstance = ElementStatus.get(params.id)
        if (!elementStatusInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), params.id])
            redirect(action: "list")
            return
        }

        [elementStatusInstance: elementStatusInstance]
    }

    def update() {
        def elementStatusInstance = ElementStatus.get(params.id)
        if (!elementStatusInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (elementStatusInstance.version > version) {
                elementStatusInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'elementStatus.label', default: 'ElementStatus')] as Object[],
                          "Another user has updated this ElementStatus while you were editing")
                render(view: "edit", model: [elementStatusInstance: elementStatusInstance])
                return
            }
        }

        elementStatusInstance.properties = params

        if (!elementStatusInstance.save(flush: true)) {
            render(view: "edit", model: [elementStatusInstance: elementStatusInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), elementStatusInstance.id])
        redirect(action: "show", id: elementStatusInstance.id)
    }

    def delete() {
        def elementStatusInstance = ElementStatus.get(params.id)
        if (!elementStatusInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), params.id])
            redirect(action: "list")
            return
        }

        try {
            elementStatusInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'elementStatus.label', default: 'ElementStatus'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
