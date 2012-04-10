package bard.db.model

import org.springframework.dao.DataIntegrityViolationException

class ProtocolController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [protocolInstanceList: Protocol.list(params), protocolInstanceTotal: Protocol.count()]
    }

    def create() {
        [protocolInstance: new Protocol(params)]
    }

    def save() {
        def protocolInstance = new Protocol(params)
        if (!protocolInstance.save(flush: true)) {
            render(view: "create", model: [protocolInstance: protocolInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'protocol.label', default: 'Protocol'), protocolInstance.id])
        redirect(action: "show", id: protocolInstance.id)
    }

    def show() {
        def protocolInstance = Protocol.get(params.id)
        if (!protocolInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'protocol.label', default: 'Protocol'), params.id])
            redirect(action: "list")
            return
        }

        [protocolInstance: protocolInstance]
    }

    def edit() {
        def protocolInstance = Protocol.get(params.id)
        if (!protocolInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'protocol.label', default: 'Protocol'), params.id])
            redirect(action: "list")
            return
        }

        [protocolInstance: protocolInstance]
    }

    def update() {
        def protocolInstance = Protocol.get(params.id)
        if (!protocolInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'protocol.label', default: 'Protocol'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (protocolInstance.version > version) {
                protocolInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'protocol.label', default: 'Protocol')] as Object[],
                          "Another user has updated this Protocol while you were editing")
                render(view: "edit", model: [protocolInstance: protocolInstance])
                return
            }
        }

        protocolInstance.properties = params

        if (!protocolInstance.save(flush: true)) {
            render(view: "edit", model: [protocolInstance: protocolInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'protocol.label', default: 'Protocol'), protocolInstance.id])
        redirect(action: "show", id: protocolInstance.id)
    }

    def delete() {
        def protocolInstance = Protocol.get(params.id)
        if (!protocolInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'protocol.label', default: 'Protocol'), params.id])
            redirect(action: "list")
            return
        }

        try {
            protocolInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'protocol.label', default: 'Protocol'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'protocol.label', default: 'Protocol'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
