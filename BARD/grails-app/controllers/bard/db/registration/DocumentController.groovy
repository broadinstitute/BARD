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
        } else {
            flash.message = null
        }

        [document: document]
    }

    def create() {
        [assayId : params.assayId]
    }

    def save() {
        def assay = Assay.get(params.assayId)
        if (!assay) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        } else {
            flash.message = null
        }

        AssayDocument document = new AssayDocument()
        document.properties["documentName", "documentType", "documentContent"] = params
        document.assay = assay
        document.dateCreated = new Date()

        if(document.save()) {
            redirect(controller: "assayDefinition", action: "show", id: assay.id)
        }
    }

    def update() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        } else {
            flash.message = null
        }

        document.properties["documentName", "documentType", "documentContent"] = params

        redirect(controller: "assayDefinition", action: "show", id: document.assay.id)
    }

    def delete() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        } else {
            flash.message = null
        }

        document.delete()

        redirect(controller: "assayDefinition", action: "show", id: document.assay.id)
    }
}
