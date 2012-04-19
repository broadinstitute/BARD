package bard.db.registration

import org.springframework.dao.DataIntegrityViolationException
import bard.db.registration.Assay

class AssayController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [assayInstanceList: Assay.list(params), assayInstanceTotal: Assay.count()]
    }

    def create() {
        [assayInstance: new Assay(params)]
    }

    def save() {
        def assayInstance = new Assay(params)
        if (!assayInstance.save(flush: true)) {
            render(view: "create", model: [assayInstance: assayInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'assay.label', default: 'Assay'), assayInstance.id])
        redirect(action: "show", id: assayInstance.id)
    }

    def show() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        [assayInstance: assayInstance]
    }

    def edit() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        [assayInstance: assayInstance]
    }

    def update() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (assayInstance.version > version) {
                assayInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'assay.label', default: 'Assay')] as Object[],
                          "Another user has updated this Assay while you were editing")
                render(view: "edit", model: [assayInstance: assayInstance])
                return
            }
        }

        assayInstance.properties = params

        if (!assayInstance.save(flush: true)) {
            render(view: "edit", model: [assayInstance: assayInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'assay.label', default: 'Assay'), assayInstance.id])
        redirect(action: "show", id: assayInstance.id)
    }

    def delete() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        try {
            assayInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
