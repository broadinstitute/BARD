package bard.db.registration

import bard.db.enums.DocumentType
import bard.db.project.InlineEditableCommand
import bard.db.project.Project
import bard.db.project.ProjectDocument
import bard.taglib.TextFormatTagLib
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import org.springframework.security.access.PermissionEvaluator
import spock.lang.Shared
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static test.TestUtils.assertFieldValidationExpectations

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(DocumentController)
@Build([AssayDocument, Assay, Project])
@Mock([AssayDocument, Assay, ProjectDocument, TextFormatTagLib])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class DocumentControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {
    @Shared Assay assay;
    @Shared Project project;
    AssayDocument existingAssayDocument
    DocumentCommand documentCommand

    void accessDeniedRoleMock() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
    }

    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.metaClass.mixin(DocumentHelper)
        controller.metaClass.mixin(EditingHelper)
        controller.documentService = Mock(DocumentService)
        assay = Assay.build()
        project = Project.build()
        existingAssayDocument = AssayDocument.build(assay: assay, documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION)
        existingAssayDocument = AssayDocument.findById(existingAssayDocument.id)
        documentCommand = mockCommandObject(DocumentCommand)
        controller.permissionEvaluator = Mock(PermissionEvaluator)
        controller.springSecurityService = Mock(SpringSecurityService)
        assert flash.message == null
    }

    void 'test render Assay Document - exception'() {
        given:
        Long assayId = null
        Long documentId = existingAssayDocument.id
        Long version = existingAssayDocument.version
        String documentName = existingAssayDocument.documentName
        DocumentType documentType = existingAssayDocument.documentType
        String documentContent = existingAssayDocument.documentContent
        Date dateCreated = existingAssayDocument.dateCreated
        Date lastUpdated = existingAssayDocument.lastUpdated
        String modifiedBy = existingAssayDocument.modifiedBy
        DocumentCommand documentCommand =
            new DocumentCommand(
                    assayId: assayId,
                    documentId: documentId,
                    version: version,
                    documentContent: documentContent,
                    documentName: documentName,
                    documentType: documentType,
                    dateCreated: dateCreated,
                    lastUpdated: lastUpdated,
                    modifiedBy: modifiedBy
            )
        when:
        Map map = controller.renderDocument(documentCommand)
        then:
        assert map.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        assert !map.template
        assert map.text == "An internal server error occurred while you were editing this page. " +
                "Please refresh your browser and try again. " +
                "If you still encounter issues please report it to the BARD team (bard-users@broadinstitute.org)"
        assert map.contentType == 'text/plain'
    }


    void 'test render Assay Document - optimistic lock error'() {
        given:
        Long assayId = existingAssayDocument.assay.id
        Long documentId = existingAssayDocument.id
        Long version = existingAssayDocument.version
        String documentName = " "
        DocumentType documentType = existingAssayDocument.documentType
        String documentContent = existingAssayDocument.documentContent
        Date dateCreated = existingAssayDocument.dateCreated
        Date lastUpdated = existingAssayDocument.lastUpdated
        String modifiedBy = existingAssayDocument.modifiedBy
        DocumentCommand documentCommand =
            new DocumentCommand(
                    assayId: assayId,
                    documentId: documentId,
                    version: version,
                    documentContent: documentContent,
                    documentName: documentName,
                    documentType: documentType,
                    dateCreated: dateCreated,
                    lastUpdated: lastUpdated,
                    modifiedBy: modifiedBy
            )
        when:
        Map map = controller.renderDocument(documentCommand)
        then:
        assert map.status == HttpServletResponse.SC_CONFLICT
        assert !map.template
        assert map.text == "default.optimistic.locking.failure"
        assert map.contentType == 'text/plain'
    }


    void 'test render Assay Document - Success #desc'() {
        given:
        Long assayId = existingAssayDocument.assay.id
        existingAssayDocument.documentType = documentType
        Long documentId = existingAssayDocument.id
        Long version = existingAssayDocument.version
        String documentName = existingAssayDocument.documentName
        DocumentType docType = existingAssayDocument.documentType
        String documentContent = existingAssayDocument.documentContent
        Date dateCreated = existingAssayDocument.dateCreated
        Date lastUpdated = existingAssayDocument.lastUpdated
        String modifiedBy = existingAssayDocument.modifiedBy

        DocumentCommand documentCommand =
            new DocumentCommand(
                    assayId: assayId,
                    documentId: documentId,
                    version: version,
                    documentContent: documentContent,
                    documentName: documentName,
                    documentType: docType,
                    dateCreated: dateCreated,
                    lastUpdated: lastUpdated,
                    modifiedBy: modifiedBy
            )
        when:
        Map map = controller.renderDocument(documentCommand)
        then:
        assert response.headers("version").get(0) == "1"
        assert response.headers("entityId").get(0)
        assert map.status == expectedStatus
        assert (map.template != null) == hasTemplate
        assert (map.model != null) == hasModel
        where:
        desc                    | documentType                            | expectedStatus            | hasTemplate | hasModel
        "Description Doc Type"  | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | HttpServletResponse.SC_OK | true        | true
        "External URL Doc Type" | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | HttpServletResponse.SC_OK | false       | false
        "Protocol Doc Type"     | DocumentType.DOCUMENT_TYPE_PROTOCOL     | HttpServletResponse.SC_OK | true        | true
        "Comments Doc Type"     | DocumentType.DOCUMENT_TYPE_COMMENTS     | HttpServletResponse.SC_OK | true        | true
        "Publication Doc Type"  | DocumentType.DOCUMENT_TYPE_PUBLICATION  | HttpServletResponse.SC_OK | false       | false
        "Other Doc Type"        | DocumentType.DOCUMENT_TYPE_OTHER        | HttpServletResponse.SC_OK | true        | true

    }

    void 'test editDocumentName - error'() {
        given:
        String name = DocumentKind.AssayDocument.toString()
        String value = "New Value"
        Long version = 0
        Long owningEntityId = 1
        params.documentType = DocumentType.DOCUMENT_TYPE_EXTERNAL_URL.id
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(name: name, value: value,
                    version: version, owningEntityId: owningEntityId)
        when:
        controller.editDocumentName(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.contentType == "text/plain;charset=utf-8"
        assert response.text == "Field is required and must not be empty"
    }

    void 'test editDocumentName - access denied error'() {
        given:
        accessDeniedRoleMock()
        Long pk = existingAssayDocument.id
        String name = DocumentKind.AssayDocument.toString()
        String value = existingAssayDocument.documentName
        Long version = existingAssayDocument.version
        Long owningEntityId = existingAssayDocument.assay.id
        params.documentName = existingAssayDocument.documentName
        params.documentType = existingAssayDocument.documentType.id
        params.documentKind = DocumentKind.AssayDocument.toString()
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: pk, name: name, value: value, version: version, owningEntityId: owningEntityId)
        when:
        controller.editDocumentName(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.contentType == "text/plain;charset=utf-8"
        assert response.text == "editing.forbidden.message"
    }

    void 'test editDocumentName - success'() {
        given:
        Long pk = existingAssayDocument.id
        String name = DocumentKind.AssayDocument.toString()
        String value = existingAssayDocument.documentName
        Long version = existingAssayDocument.version
        Long owningEntityId = existingAssayDocument.assay.id
        params.documentName = existingAssayDocument.documentName
        params.documentType = existingAssayDocument.documentType.id
        params.documentKind = DocumentKind.AssayDocument.toString()
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: pk, name: name, value: value, version: version, owningEntityId: owningEntityId)
        when:
        controller.editDocumentName(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert response.text == "${System.getProperty("line.separator")}<p>${value}</p>${System.getProperty("line.separator")}"
        assert response.headers("version").get(0) == "1"
        assert response.headers("entityId").get(0) == existingAssayDocument.id.toString()
    }

    void 'test editDocument - error'() {
        given:
        String name = DocumentKind.AssayDocument.toString()
        String value = "New Value"
        Long version = 0
        Long owningEntityId = 1
        params.documentType = DocumentType.DOCUMENT_TYPE_EXTERNAL_URL.id
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(name: name, value: value, version: version, owningEntityId: owningEntityId)
        when:
        controller.editDocument(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.contentType == "text/plain;charset=utf-8"
        assert response.text == "Field is required and must not be empty"
    }

    void 'test editDocument - access denied'() {
        given:
        accessDeniedRoleMock()
        Long pk = existingAssayDocument.id
        String name = DocumentKind.AssayDocument.toString()
        String value = "New Value"
        Long version = existingAssayDocument.version
        Long owningEntityId = existingAssayDocument.assay.id
        params.documentName = existingAssayDocument.documentName
        params.documentType = existingAssayDocument.documentType.id
        params.documentKind = DocumentKind.AssayDocument.toString()

        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: pk, name: name, value: value, version: version, owningEntityId: owningEntityId)
        when:
        controller.editDocument(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.contentType == "text/plain;charset=utf-8"
        assert response.text == "editing.forbidden.message"

    }

    void 'test editDocument - success'() {
        given:
        Long pk = existingAssayDocument.id
        String name = DocumentKind.AssayDocument.toString()
        String value = "New Value"
        Long version = existingAssayDocument.version
        Long owningEntityId = existingAssayDocument.assay.id
        params.documentName = existingAssayDocument.documentName
        params.documentType = existingAssayDocument.documentType.id
        params.documentKind = DocumentKind.AssayDocument.toString()

        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: pk, name: name, value: value, version: version, owningEntityId: owningEntityId)
        when:
        controller.editDocument(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert response.text == "${System.getProperty("line.separator")}<p>${value}</p>${System.getProperty("line.separator")}"
        assert response.headers("version").get(0) == "1"
        assert response.headers("entityId").get(0) == existingAssayDocument.id.toString()
    }

    void 'test create Document command #desc'() {
        given:
        final String documentName = "documentName"
        final Long documentId = 20L
        final String documentContent = "documentContent"
        final Long version = 0L
        when:

        final DocumentCommand documentCommand =
            controller.createDocumentCommand(documentName, documentId, documentContent, documentType, version, owningEntityId, documentKind)
        then:
        assert documentCommand
        assert documentCommand.assayId == expectedAssayId
        assert documentCommand.projectId == expectedProjectId
        assert documentCommand.documentType == expectedDocumentType

        where:
        desc                    | documentKind                 | owningEntityId | documentType                            | expectedDocumentType                    | expectedAssayId | expectedProjectId
        "Assay Document Type"   | DocumentKind.AssayDocument   | 10             | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | 10              | null
        "Project Document Type" | DocumentKind.ProjectDocument | 10             | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | null            | 10
    }

    void 'test create'() {
        when:
        documentCommand.assayId = assay.id
        def model = controller.create(documentCommand)

        then:
        model.document.assayId == assay.id
    }

    void 'test create- access denied'() {
        given:
        accessDeniedRoleMock()
        when:
        documentCommand.assayId = assay.id
        controller.create(documentCommand)

        then:
        assertAccesDeniedErrorMessage()
    }

    void 'test valid save assay doc'() {
        when:
        documentCommand.assayId = assay.id
        documentCommand.documentContent = "content"
        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_DESCRIPTION
        documentCommand.documentName = "name"
        controller.save(documentCommand)

        then:
        AssayDocument.count() == 2
        AssayDocument assayDocument = assay.documents.last()
        response.redirectedUrl == "/assayDefinition/show/${assay.id}#document-${assayDocument.id}"
        assayDocument.assay == assay
        assayDocument.documentName == "name"
        assayDocument.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION
        assayDocument.documentContent == "content"

    }

    void 'test valid save assay doc - access denied'() {
        given:
        accessDeniedRoleMock()
        when:
        documentCommand.assayId = assay.id
        documentCommand.documentContent = "content"
        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_DESCRIPTION
        documentCommand.documentName = "name"
        controller.save(documentCommand)

        then:
        assertAccesDeniedErrorMessage()

    }

    void 'test valid save proj doc'() {
        when:
        documentCommand.projectId = project.id
        documentCommand.documentContent = "content"
        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_DESCRIPTION
        documentCommand.documentName = "name"
        controller.save(documentCommand)

        then:
        ProjectDocument.count() == 1
        ProjectDocument projectDocument = project.documents.last()
        response.redirectedUrl == "/project/show/${project.id}#document-${projectDocument.id}"
        projectDocument.project == project
        projectDocument.documentName == "name"
        projectDocument.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION
        projectDocument.documentContent == "content"
    }

    void 'test valid save proj doc - access denied'() {
        given:
        accessDeniedRoleMock()
        when:
        documentCommand.projectId = project.id
        documentCommand.documentContent = "content"
        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_DESCRIPTION
        documentCommand.documentName = "name"
        controller.save(documentCommand)

        then:
        assertAccesDeniedErrorMessage()
    }

    void 'test save Assay Not found'() {
        when:
        documentCommand.assayId = -1
        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_PUBLICATION
        documentCommand.documentName = "name"
        controller.save(documentCommand)

        then:
        view == "/document/create"
        model.document.assayId == -1
        model.document.errors.getAllErrors().collect { it.codes }.flatten().contains('nullable')

    }

    void 'test save invalid doc, null documentName'() {
        when:
        documentCommand.assayId = assay.id
        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_PUBLICATION
        controller.save(documentCommand)

        then:
        view == '/document/create'
        model.document.assayId == assay.id
        model.document.errors.hasErrors()
        assertFieldValidationExpectations(model.document, 'documentName', false, 'nullable')
    }
//    void 'test update succeed'() {
//        when:
//
//        assert existingAssayDocument.validate()
//        documentCommand.assayId = assay.id
//        documentCommand.version = existingAssayDocument.version
//        documentCommand.documentId = existingAssayDocument.id
//        documentCommand.documentContent = "new content"
//        documentCommand.documentType = DocumentType.DOCUMENT_TYPE_OTHER
//        documentCommand.documentName = "new name"
//        controller.update(documentCommand)
//
//        then:
//        response.redirectedUrl == "/assayDefinition/show/${assay.id}#document-${existingAssayDocument.id}"
//        existingAssayDocument.documentContent == "new content"
//        existingAssayDocument.documentName == "new name"
//        existingAssayDocument.documentType == DocumentType.DOCUMENT_TYPE_OTHER
//    }
//
//    void 'test update fail doc not found'() {
//        when:
//        documentCommand.documentId = -1
//        documentCommand.assayId = assay.id
//        controller.update(documentCommand)
//
//        then:
//        model.document.hasErrors()
//    }

    void 'test delete'() {

        when:
        params.type = "Assay"
        params.id = existingAssayDocument.id
        controller.delete()

        then:
        controller.documentService.deleteAssayDocument(_, _) >> {}
        assert response.redirectedUrl.startsWith("/assayDefinition/show/")
    }

    void 'test delete - access denied'() {
        given:
        accessDeniedRoleMock()
        when:
        params.type = "Assay"
        params.id = existingAssayDocument.id
        controller.delete()

        then:
        controller.documentService.deleteAssayDocument(_, _) >> {}
        assert response.redirectedUrl.startsWith("/assayDefinition/show/")
    }

    void 'test command object constraints #desc'() {
        given:
        DocumentCommand documentCommand = mockCommandObject(DocumentCommand)
        documentCommand.copyFromDomainToCmd(existingAssayDocument)
        assert documentCommand.validate()

        when:
        documentCommand[(field)] = vut
        println(documentCommand.dump())
        documentCommand.validate()

        then:
        assertFieldValidationExpectations(documentCommand, field, valid, code)

        where:
        desc                        | field             | vut                                     | valid | code
        'assayId not null valid'    | 'assayId'         | 3                                       | true  | null

        'documentId null valid'     | 'documentId'      | null                                    | true  | null
        'documentId not null valid' | 'documentId'      | 3                                       | true  | null

        'version null valid'        | 'version'         | null                                    | true  | null
        'version not null valid'    | 'version'         | 3                                       | true  | null

        'documentType valid'        | 'documentType'    | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | true  | null
        'documentType valid'        | 'documentType'    | DocumentType.DOCUMENT_TYPE_COMMENTS     | true  | null
        'documentType valid'        | 'documentType'    | DocumentType.DOCUMENT_TYPE_PROTOCOL     | true  | null
        'documentType valid'        | 'documentType'    | DocumentType.DOCUMENT_TYPE_PUBLICATION  | true  | null
        'documentType valid'        | 'documentType'    | DocumentType.DOCUMENT_TYPE_OTHER        | true  | null
        'documentType valid'        | 'documentType'    | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | true  | null

        'documentName null'         | 'documentName'    | null                                    | false | 'nullable'
        'documentName blank'        | 'documentName'    | ''                                      | false | 'blank'
        'documentName blank'        | 'documentName'    | ' '                                     | false | 'blank'
        'documentName valid'        | 'documentName'    | 'docName'                               | true  | null

        'documentContent blank'     | 'documentContent' | ''                                      | false | 'blank'
        'documentContent blank'     | 'documentContent' | ' '                                     | false | 'blank'
        'documentContent null'      | 'documentContent' | null                                    | true  | null

        'documentContent valid'     | 'documentContent' | 'docName'                               | true  | null
    }

    void 'test command object createNewDocument with #desc '() {
        when:
        DocumentCommand documentCommand = mockCommandObject(DocumentCommand)
        documentCommand.assayId = assayId.call()
        documentCommand.documentName = documentName
        documentCommand.documentType = documentType

        AssayDocument assayDocument = documentCommand.createNewDocument()

        then:
        documentCommand?.hasErrors() == hasErrors
        (assayDocument?.id == null) == documentIdNull
        if (assayDocument) {
            assayDocument.documentName == documentName
            assayDocument.documentType == documentType
            assayDocument.documentContent == null
        }

        where:

        desc                        | assayId      | documentName   | documentType                        | hasErrors | documentIdNull
        'error due to documentType' | { assay.id } | null           | DocumentType.DOCUMENT_TYPE_COMMENTS | true      | true
        'valid command object'      | { assay.id } | 'documentName' | DocumentType.DOCUMENT_TYPE_COMMENTS | false     | false
    }

    void 'test command object fromAssayDocument copied field: #field'() {
        given:
        AssayDocument assayDocument = AssayDocument.build(assay: assay,
                documentContent: 'documentContent',
                lastUpdated: new Date(),
                modifiedBy: 'Foo')
        when:
        DocumentCommand documentCommand = mockCommandObject(DocumentCommand)
        documentCommand.copyFromDomainToCmd(assayDocument)

        then:
        documentCommand[(field)] == assayDocument[(field)]
        documentCommand.assayId == assayDocument.assay.id

        where:
        field << DocumentCommand.PROPS_FROM_DOMAIN_TO_CMD
    }

    void 'test command object removeHtmlLineBreaks #desc'() {
        given:
        DocumentCommand documentCommand = new DocumentCommand(documentType: documentType,
                documentContent: "http://aaa.ccc.com<div>  <br> </div>")
        when:
        String externalURL = documentCommand.removeHtmlLineBreaks()
        then:
        assert externalURL == expectedDocumentContent

        where:
        desc              | documentType                            | expectedDocumentContent
        "External URL"    | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | "http://aaa.ccc.com"
        "Publication URL" | DocumentType.DOCUMENT_TYPE_PUBLICATION  | "http://aaa.ccc.com"
        "Other Documents" | DocumentType.DOCUMENT_TYPE_OTHER        | "http://aaa.ccc.com<div>  <br> </div>"
    }
}