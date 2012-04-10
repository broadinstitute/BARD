package bard.db.model

import org.springframework.dao.DataIntegrityViolationException

class UnitController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [unitInstanceList: Unit.list(params), unitInstanceTotal: Unit.count()]
    }

    def create() {
        [unitInstance: new Unit(params)]
    }

    def save() {
        def unitInstance = new Unit(params)
        if (!unitInstance.save(flush: true)) {
            render(view: "create", model: [unitInstance: unitInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'unit.label', default: 'Unit'), unitInstance.id])
        redirect(action: "show", id: unitInstance.id)
    }

    def show() {
        def unitInstance = Unit.get(params.id)
        if (!unitInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'unit.label', default: 'Unit'), params.id])
            redirect(action: "list")
            return
        }

        [unitInstance: unitInstance]
    }

    def edit() {
        def unitInstance = Unit.get(params.id)
        if (!unitInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'unit.label', default: 'Unit'), params.id])
            redirect(action: "list")
            return
        }

        [unitInstance: unitInstance]
    }

    def update() {
        def unitInstance = Unit.get(params.id)
        if (!unitInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'unit.label', default: 'Unit'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (unitInstance.version > version) {
                unitInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'unit.label', default: 'Unit')] as Object[],
                          "Another user has updated this Unit while you were editing")
                render(view: "edit", model: [unitInstance: unitInstance])
                return
            }
        }

        unitInstance.properties = params

        if (!unitInstance.save(flush: true)) {
            render(view: "edit", model: [unitInstance: unitInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'unit.label', default: 'Unit'), unitInstance.id])
        redirect(action: "show", id: unitInstance.id)
    }

    def delete() {
        def unitInstance = Unit.get(params.id)
        if (!unitInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'unit.label', default: 'Unit'), params.id])
            redirect(action: "list")
            return
        }

        try {
            unitInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'unit.label', default: 'Unit'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'unit.label', default: 'Unit'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
