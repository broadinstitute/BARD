package bard.db.registration

import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import grails.validation.ValidationErrors
import groovy.transform.InheritConstructors

@Secured(['isFullyAuthenticated()'])
class DocumentController {
    static allowedMethods = [save: "POST", update: "POST"]

    def create(DocumentCommand documentCommand) {
        documentCommand.clearErrors()
        g.message()
        [document: documentCommand]
    }

    def save(DocumentCommand documentCommand) {
        AssayDocument document = documentCommand.createNewAssayDocument()
        if (document) {
            redirect(controller: "assayDefinition", action: "show", id: document.assay.id, fragment: "document-${document.id}")
        } else {
            render(view: "create", model: [document: documentCommand])
        }
    }

    def edit(Long id) {
        DocumentCommand dc = new DocumentCommand()
        dc.populateWithExistingAssayDocument(id)
        [document: dc]
    }

    def update(DocumentCommand documentCommand) {
        AssayDocument assayDocument = documentCommand.updateExistingAssayDocument()
        if (assayDocument) {
            redirect(controller: "assayDefinition", action: "show", id: assayDocument.assay.id, fragment: "document-${documentCommand.documentId}")
        } else {
            render(view: "edit", model: [document: documentCommand])
        }
    }

    def delete(Long id) {
        def document = AssayDocument.get(id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        }
        document.delete()
        redirect(controller: "assayDefinition", action: "show", id: document.assay.id, fragment: 'documents-header')
    }
}
@InheritConstructors
@Validateable
class DocumentCommand {
    Long assayId
    Long documentId
    Long version
    String documentName
    String documentType
    String documentContent
    Date dateCreated
    Date lastUpdated
    String modifiedBy


    public static final List<String> PROPS_FROM_CMD_TO_DOMAIN = ['version', 'documentName', 'documentType', 'documentContent'].asImmutable()
    public static final List<String> PROPS_FROM_DOMAIN_TO_CMD = [PROPS_FROM_CMD_TO_DOMAIN, 'dateCreated', 'lastUpdated', 'modifiedBy'].flatten().asImmutable()

    static constraints = {
        importFrom(AssayDocument, exclude: ['dateCreated', 'lastUpdated', 'modifiedBy'])
        assayId(nullable: false)
        documentId(nullable: true, validator: { val, self, errors ->
            if (self.assayId == null && self.documentId == null) {
                errors.rejectValue('documentId', 'nullable')
                return false
            }
        })
        version(nullable: true)
    }

    DocumentCommand(AssayDocument assayDocument) {
        copyFromDomainToCmd(assayDocument)
    }

    AssayDocument createNewAssayDocument() {
        AssayDocument assayDocument
        if (validate()) {
            assayDocument = new AssayDocument()
            copyFromCmdToDomain(assayDocument)
            assayDocument.assay = attemptFindById(Assay, assayId)
            if (!attemptSave(assayDocument)) {
                assayDocument = null
            }
        }
        assayDocument
    }

    void populateWithExistingAssayDocument(final Long id) {
        AssayDocument assayDocument = attemptFindById(AssayDocument, id)
        if (assayDocument) {
            copyFromDomainToCmd(assayDocument)
        }
    }

    AssayDocument updateExistingAssayDocument() {
        AssayDocument assayDocument = null
        if (validate()) {
            assayDocument = attemptFindById(AssayDocument, documentId)
            if (assayDocument) {
                if (this.version?.longValue() != assayDocument.version.longValue()) {
                    getErrors().reject('default.optimistic.locking.failure', [AssayDocument] as Object[], 'optimistic lock failure')
                    copyFromDomainToCmd(assayDocument)
                    assayDocument = null
                } else {
                    copyFromCmdToDomain(assayDocument)
                    if (!attemptSave(assayDocument)) {
                        assayDocument = null
                    }
                }
            }
        }
        return assayDocument
    }



    def attemptFindById(Class domain, Long id) {
        def instance
        if (id) {
            instance = domain.findById(id)
        }
        if (!instance) {
            getErrors().reject('default.not.found.message', [domain, id] as Object[], 'not found')
        }
        instance
    }

    boolean attemptSave(Object domain) {
        if (!domain?.save()) {
            domain?.errors?.allErrors?.each { error ->
                getErrors().reject(error.code, g.message(error: error))
            }
            return false
        }
        return true
    }

    void copyFromCmdToDomain(AssayDocument assayDocument) {
        for (String field in PROPS_FROM_CMD_TO_DOMAIN) {
            assayDocument[(field)] = this[(field)]
        }
    }

    DocumentCommand copyFromDomainToCmd(AssayDocument assayDocument) {
        assert assayDocument
        this.assayId = assayDocument.assay.id
        this.documentId = assayDocument.id
        for (String field in PROPS_FROM_DOMAIN_TO_CMD) {
            this[(field)] = assayDocument[(field)]
        }
        return this
    }

    /**
     * if an errors object hasn't yet been created
     * create one
     * @return
     */
    def getErrors() {
        if (errors == null) {
            setErrors(new ValidationErrors(this))
        }
        errors
    }


}