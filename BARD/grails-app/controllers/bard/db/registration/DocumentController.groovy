package bard.db.registration

import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class DocumentController {
    static allowedMethods = [save: "POST", update: "POST"]

    def edit() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        }
        [document: document]
    }

    def create() {
        [assayId: params.assayId]
    }

    def save() {
        def assay = Assay.get(params.assayId)
        if (!assay) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }

        AssayDocument document = new AssayDocument()
        document.properties["documentName", "documentType", "documentContent"] = params
        document.assay = assay

        if (document.save()) {
            redirect(controller: "assayDefinition", action: "edit", id: document.assay.id, fragment:"document-${document.id}")
        } else {
            render(view: "create", model: [assayId: params.assayId, document: document])
        }
    }

    def update() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            render(view: "edit", assayId: params?.assayId)
            return
        }
        Long version = params?.version as Long
        if (version && version != document?.version) {
            document.errors.reject("default.optimistic.locking.failure", [document.getDomainClass().getNaturalName()] as Object[], "Optomistic lock failure")
            render(view: "edit", model: [document: document])
            return
        }
        document.properties["documentName", "documentType", "documentContent"] = params
        if (document?.save()) {
            redirect(controller: "assayDefinition", action: "edit", id: document.assay.id, fragment:"document-${document.id}")
        } else {
            render(view: "edit", model: [document: document])
        }
    }

    def delete() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        }
        document.delete()
        redirect(controller: "assayDefinition", action: "edit", id: document.assay.id, fragment: 'documents-header')
    }
}