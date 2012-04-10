package bard.db.model

import org.springframework.dao.DataIntegrityViolationException

class UnitConversionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [unitConversionInstanceList: UnitConversion.list(params), unitConversionInstanceTotal: UnitConversion.count()]
    }

    def create() {
        [unitConversionInstance: new UnitConversion(params)]
    }

    def save() {
        def unitConversionInstance = new UnitConversion(params)
        if (!unitConversionInstance.save(flush: true)) {
            render(view: "create", model: [unitConversionInstance: unitConversionInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), unitConversionInstance.id])
        redirect(action: "show", id: unitConversionInstance.id)
    }

    def show() {
        def unitConversionInstance = UnitConversion.get(params.id)
        if (!unitConversionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), params.id])
            redirect(action: "list")
            return
        }

        [unitConversionInstance: unitConversionInstance]
    }

    def edit() {
        def unitConversionInstance = UnitConversion.get(params.id)
        if (!unitConversionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), params.id])
            redirect(action: "list")
            return
        }

        [unitConversionInstance: unitConversionInstance]
    }

    def update() {
        def unitConversionInstance = UnitConversion.get(params.id)
        if (!unitConversionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (unitConversionInstance.version > version) {
                unitConversionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'unitConversion.label', default: 'UnitConversion')] as Object[],
                          "Another user has updated this UnitConversion while you were editing")
                render(view: "edit", model: [unitConversionInstance: unitConversionInstance])
                return
            }
        }

        unitConversionInstance.properties = params

        if (!unitConversionInstance.save(flush: true)) {
            render(view: "edit", model: [unitConversionInstance: unitConversionInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), unitConversionInstance.id])
        redirect(action: "show", id: unitConversionInstance.id)
    }

    def delete() {
        def unitConversionInstance = UnitConversion.get(params.id)
        if (!unitConversionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), params.id])
            redirect(action: "list")
            return
        }

        try {
            unitConversionInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'unitConversion.label', default: 'UnitConversion'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
