package bard.db.registration

import bard.db.command.BardCommand
import bard.db.model.AbstractDocument
import bard.db.project.Project
import bard.db.project.ProjectDocument
import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import grails.validation.ValidationErrors
import groovy.transform.InheritConstructors

@Secured(['isAuthenticated()'])
class DocumentController {
    static allowedMethods = [save: "POST", update: "POST"]

    Map<String, Class> nameToDomain = ["Assay": AssayDocument, "Project": ProjectDocument]

    def create(DocumentCommand documentCommand) {
        documentCommand.clearErrors()
        [document: documentCommand]
    }

    def redirectToOwner(document) {
        if (document.getOwner() instanceof Assay) {
            Assay assay = document.getOwner()
            redirect(controller: "assayDefinition", action: "show", id: assay.id, fragment: "document-${document.id}")
        } else if (document.getOwner() instanceof Project){
            Project project = document.getOwner()
            redirect(controller: "project", action: "show", id: project.id, fragment: "document-${document.id}")
        } else {
            throw new RuntimeException("document owner ${document.getOwner} is neither an assay nor project")
        }
    }

    def save(DocumentCommand documentCommand) {
        Object document = documentCommand.createNewDocument()
        if (document) {
            redirectToOwner(document)
        } else {
            render(view: "create", model: [document: documentCommand])
        }
    }

    def edit(String type, Long id) {
        DocumentCommand dc = new DocumentCommand()
        Class domainClass = nameToDomain[type]
        if (domainClass == null) {
            throw new RuntimeException("Not a valid value ${domainClass}")
        }
        dc.populateWithExistingDocument(domainClass, id)
        [document: dc]
    }

    def update(DocumentCommand documentCommand) {
        Object document = documentCommand.updateExistingDocument()
        if (document) {
            redirectToOwner(document)
        } else {
            render(view: "edit", model: [document: documentCommand])
        }
    }

    def delete(String type, Long id) {
        Class domainClass = nameToDomain[type]
        def document = domainClass.get(id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        }
        document.delete()
        redirectToOwner(document)
    }
}
@InheritConstructors
@Validateable
class DocumentCommand extends BardCommand{
    Long assayId
    Long projectId

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
        assayId(nullable: true)
        projectId(nullable: true)
        ownerController(nullable: false, blank: false)
        documentId(nullable: true, validator: { val, self, errors ->
            if (self.assayId == null && self.documentId == null && self.projectId == null) {
                errors.rejectValue('documentId', 'nullable')
                return false
            }
        })
        version(nullable: true)
    }

    DocumentCommand(){}

    DocumentCommand(AssayDocument assayDocument) {
        copyFromDomainToCmd(assayDocument)
    }

    AbstractDocument createNewDocument() {
        AbstractDocument documentToReturn = null
        if (validate()) {
            AbstractDocument doc
            if (assayId) {
                doc = new AssayDocument()
                doc.assay = attemptFindById(Assay, assayId)

            }
            if (projectId) {
                doc = new ProjectDocument()
                doc.project = attemptFindById(Project, projectId)
            }
            copyFromCmdToDomain(doc)
            if (attemptSave(doc)) {
                documentToReturn = doc
            }
        }
        documentToReturn
    }

    void populateWithExistingDocument(final Class domainClass, final Long id) {
        AbstractDocument document = attemptFindById(domainClass, id)
        if (document) {
            copyFromDomainToCmd(document)
        }
    }

    AbstractDocument updateExistingDocument() {
        AbstractDocument document = null
        if (validate()) {
            if (assayId != null) {
                document = attemptFindById(AssayDocument, documentId)
            } else if (projectId != null) {
                document = attemptFindById(ProjectDocument, documentId)
            } else {
                throw new RuntimeException("Neither assayId nor projectId was provided")
            }

            if (document) {
                if (this.version?.longValue() != document.version.longValue()) {
                    getErrors().reject('default.optimistic.locking.failure', [AbstractDocument] as Object[], 'optimistic lock failure')
                    copyFromDomainToCmd(document)
                    document = null
                } else {
                    copyFromCmdToDomain(document)
                    if (!attemptSave(document)) {
                        document = null
                    }
                }
            }
        }
        return document
    }







    void copyFromCmdToDomain(AbstractDocument assayDocument) {
        for (String field in PROPS_FROM_CMD_TO_DOMAIN) {
            assayDocument[(field)] = this[(field)]
        }
    }

    DocumentCommand copyFromDomainToCmd(AbstractDocument document) {
        assert document
        if (document instanceof AssayDocument) {
            this.assayId = document.assay.id
        }
        if (document instanceof ProjectDocument) {
            this.projectId = document.project.id
        }
        this.documentId = document.id
        for (String field in PROPS_FROM_DOMAIN_TO_CMD) {
            this[(field)] = document[(field)]
        }
        return this
    }


/**
 * Hack to determine the id of owning to forward to given presence of assay or project ids
 * @return
 */
    Long getOwnerId() {
        if (projectId) {
            return projectId
        } else if (assayId) {
            return projectId
        } else {
            throw new RuntimeException('need either a projectId or assayId to determine ownerId')
        }
    }
    /**
     * Hack to determine the controller to forward to given presence of assay or project ids
     * @return
     */
    String getOwnerController() {
        if (projectId) {
            return 'project'
        } else if (assayId) {
            return 'assayDefinition'
        } else {
            throw new RuntimeException('need either a projectId or assayId to determine owner')
        }
    }
}