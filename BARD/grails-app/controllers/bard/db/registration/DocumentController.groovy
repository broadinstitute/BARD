package bard.db.registration

import bard.db.command.BardCommand
import bard.db.enums.DocumentType
import bard.db.model.AbstractDocument
import bard.db.project.InlineEditableCommand
import bard.db.project.Project
import bard.db.project.ProjectDocument
import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse

@Mixin([DocumentHelper, EditingHelper])
@Secured(['isAuthenticated()'])
class DocumentController {
    static allowedMethods = [save: "POST"]

    Map<String, Class> nameToDomain = ["Assay": AssayDocument, "Project": ProjectDocument]
    DocumentService documentService
    def permissionEvaluator
    def springSecurityService

    def create(DocumentCommand documentCommand) {
        documentCommand.clearErrors()
        def entity = null
        if (documentCommand.assayId) {
            entity = Assay.findById(documentCommand.assayId)
        } else if (documentCommand.projectId) {
            entity = Project.findById(documentCommand.projectId)
        }
        if (!canEdit(permissionEvaluator, springSecurityService, entity)) {
            render accessDeniedErrorMessage()
            return
        }
        [document: documentCommand]
    }

    def save(DocumentCommand documentCommand) {

        if (!documentCommand.documentType) {
            documentCommand.documentType = DocumentType.byId(params.documentType)
        }
        def entity = null
        if (documentCommand.assayId) {
            entity = Assay.findById(documentCommand.assayId)
        } else if (documentCommand.projectId) {
            entity = Project.findById(documentCommand.projectId)
        }
        if (!canEdit(permissionEvaluator, springSecurityService, entity)) {
            render accessDeniedErrorMessage()
            return
        }
        documentCommand.documentContent = documentCommand.removeHtmlLineBreaks()

        Object document = documentCommand.createNewDocument()
        if (document) {
            redirectToOwner(document)
        } else {
            render(view: "create", model: [document: documentCommand])
        }
    }
    /**
     * No longer used. remove method and corresponding gsp
     * @param type
     * @param id
     * @return
     */
//    @Deprecated
//    def edit(String type, Long id) {
//        DocumentCommand dc = new DocumentCommand()
//        Class domainClass = nameToDomain[type]
//        if (domainClass == null) {
//            throw new RuntimeException("Not a valid value ${domainClass}")
//        }
//        dc.populateWithExistingDocument(domainClass, id)
//        [document: dc]
//    }

    def editDocument(InlineEditableCommand inlineEditableCommand) {
        if (!inlineEditableCommand.validate()) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: "Field is required and must not be empty", contentType: 'text/plain', template: null
        } else {
            DocumentKind documentKind = params.documentKind as DocumentKind
            DocumentCommand documentCommand =
                createDocumentCommand(
                        params.documentName,
                        inlineEditableCommand.pk,
                        inlineEditableCommand.value.trim(),
                        DocumentType.byId(params.documentType),
                        inlineEditableCommand.version,
                        inlineEditableCommand.owningEntityId,
                        documentKind
                )
            def entity = null
            if (DocumentKind.AssayDocument == documentKind) {
                entity = Assay.findById(documentCommand.assayId)
            } else if (DocumentKind.ProjectDocument == documentKind) {
                entity = Project.findById(documentCommand.projectId)
            }
            if (entity) {
                if (!canEdit(permissionEvaluator, springSecurityService, entity)) {
                    render accessDeniedErrorMessage()
                    return
                }
            } else {
                throw new RuntimeException("Could not find owning entity with id ${inlineEditableCommand.owningEntityId}")
            }
            render(renderDocument(documentCommand))
        }

    }

    def editDocumentName(InlineEditableCommand inlineEditableCommand) {
        if (!inlineEditableCommand.validate()) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: "Field is required and must not be empty", contentType: 'text/plain', template: null
        } else {
            DocumentKind documentKind = params.documentKind as DocumentKind
            DocumentCommand documentCommand =
                createDocumentCommand(
                        inlineEditableCommand.value.trim(),
                        inlineEditableCommand.pk,
                        params.documentName,
                        DocumentType.byId(params.documentType),
                        inlineEditableCommand.version,
                        inlineEditableCommand.owningEntityId,
                        documentKind
                )
            def entity = null
            if (DocumentKind.AssayDocument == documentKind) {
                entity = Assay.findById(documentCommand.assayId)
            } else if (DocumentKind.ProjectDocument == documentKind) {
                entity = Project.findById(documentCommand.projectId)
            }
            if (entity) {
                if (!canEdit(permissionEvaluator, springSecurityService, entity)) {
                    render accessDeniedErrorMessage()
                    return
                }
            }
            render(renderDocument(documentCommand))
        }

    }
    //No longer used
    // @Deprecated
//    def update(DocumentCommand documentCommand) {
//        Object document = documentCommand.updateExistingDocument()
//        if (document) {
//            redirectToOwner(document)
//        } else {
//            render(view: "edit", model: [document: documentCommand])
//        }
//    }

    def delete(String type, Long id) {
        Class domainClass = nameToDomain[type]
        def document = domainClass.get(id)
        if (!document) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayDocument.label', default: 'AssayDocument'), params.id])
            return
        }
        try {
            if (document instanceof AssayDocument) {
                documentService.deleteAssayDocument(document.assay.id, document)
            } else if (document instanceof ProjectDocument) {
                documentService.deleteProjectDocument(document.project.id, document)
            } else {
                throw new RuntimeException("UnHandled Document Type " + domainClass.toString())
            }
        } catch (AccessDeniedException ade) {
            render accessDeniedErrorMessage()
        }
        redirectToOwner(document)
    }

    private def redirectToOwner(document) {
        if (document.getOwner() instanceof Assay) {
            Assay assay = document.getOwner()
            redirect(controller: "assayDefinition", action: "show", id: assay.id, fragment: "document-${document.id}")
        } else if (document.getOwner() instanceof Project) {
            Project project = document.getOwner()
            redirect(controller: "project", action: "show", id: project.id, fragment: "document-${document.id}")
        } else {
            throw new RuntimeException("document owner ${document.getOwner} is neither an assay nor project")
        }
    }
}

class DocumentHelper {


    def renderDocument(DocumentCommand documentCommand) {
        try {
            def document = documentCommand.updateExistingDocument()
            if (documentCommand.hasErrors()) {
                StringBuilder b = new StringBuilder()
                b.append(g.message(code: 'default.optimistic.locking.failure'))
                return [status: HttpServletResponse.SC_CONFLICT, text: "${b.toString()}", contentType: 'text/plain', template: null]
            } else {
                response.setHeader('version', document.version.toString())
                response.setHeader('entityId', document.id.toString())
                if (document.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL || document.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION) {
                    return [status: HttpServletResponse.SC_OK, text: "${document.documentName}", contentType: 'text/plain', template: null]
                } else {
                    return [status: HttpServletResponse.SC_OK, template: "/document/docsWithLineBreaks", model: [documentContent: document.documentContent]]
                }
            }
        } catch (Exception ee) {
            log.error(ee)
            return [status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "An internal server error occurred while you were editing this page. Please refresh your browser and try again. If you still encounter issues please report it to the BARD team (bard-users@broadinstitute.org)", contentType: 'text/plain', template: null]
        }
    }
    /**
     *
     * @param documentName
     * @param documentId
     * @param documentContent
     * @param documentType
     * @param version
     * @param owningEntityId
     * @param currentDocumentKind
     * @return {@link DocumentCommand}
     */
    DocumentCommand createDocumentCommand(final String documentName,
                                          final Long documentId,
                                          final String documentContent,
                                          final DocumentType documentType,
                                          final Long version,
                                          final Long owningEntityId,
                                          final DocumentKind currentDocumentKind) {
        Long assayId = null
        Long projectId = null
        if (DocumentKind.AssayDocument == currentDocumentKind) {
            assayId = owningEntityId
        }
        if (DocumentKind.ProjectDocument == currentDocumentKind) {
            projectId = owningEntityId
        }

        return new DocumentCommand(
                documentName: documentName,
                documentId: documentId,
                documentContent: documentContent,
                documentType: documentType,
                version: version,
                assayId: assayId,
                projectId: projectId
        )
    }
}
@InheritConstructors
@Validateable
class DocumentCommand extends BardCommand {

    Long assayId
    Long projectId

    Long documentId
    Long version
    String documentName
    DocumentType documentType
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

    DocumentCommand() {}

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







    void copyFromCmdToDomain(AbstractDocument document) {
        for (String field in PROPS_FROM_CMD_TO_DOMAIN) {
            document[(field)] = this[(field)]
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
            return assayId
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

    String removeHtmlLineBreaks() {

        if (this.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL ||
                this.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION) {
            String content = this.documentContent
            return content?.replaceAll("<div>", "")?.replaceAll("</div>", "")?.replaceAll("<br>", "")?.replaceAll("\n", "")?.replaceAll("\r", "")?.trim()
        }
        return this.documentContent
    }
}
enum DocumentKind {
    AssayDocument,
    ProjectDocument
}