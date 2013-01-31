package bard.db.registration

class DocumentController {

    def edit() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        } else {
            flash.message = null
        }

        [document: document]

        redirect(action: "show", id: document.assay.id)
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
        document.assay = assay
        document.save()

        redirect(action: "show", id: assay.id)
    }

    def update() {
        def document = AssayDocument.get(params.id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        } else {
            flash.message = null
        }

        redirect(action: "show", id: document.assay.id)
    }
}
