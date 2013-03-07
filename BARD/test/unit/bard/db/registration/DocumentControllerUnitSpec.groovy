package bard.db.registration

import bard.db.model.IDocumentType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.model.IDocumentType.*
import static test.TestUtils.assertFieldValidationExpectations

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(DocumentController)
@Build([AssayDocument, Assay])
@Mock([AssayDocument, Assay])
@Unroll
class DocumentControllerUnitSpec extends Specification {
    @Shared Assay assay;
    AssayDocument existingAssayDocument
    DocumentCommand documentCommand

    @Before
    void setup() {
        assay = Assay.build()
        existingAssayDocument = AssayDocument.build(assay: assay, documentType: DOCUMENT_TYPE_DESCRIPTION)
        existingAssayDocument = AssayDocument.findById(existingAssayDocument.id)
        documentCommand = mockCommandObject(DocumentCommand)
        assert flash.message == null
    }

    void tearDown() {
        // Tear down logic here
    }

    void 'test create'() {
        when:
        documentCommand.assayId = assay.id
        def model = controller.create(documentCommand)

        then:
        model.document.assayId == assay.id
    }

    void 'test valid save'() {
        when:
        documentCommand.assayId = assay.id
        documentCommand.documentContent = "content"
        documentCommand.documentType = IDocumentType.DOCUMENT_TYPE_DESCRIPTION
        documentCommand.documentName = "name"
        controller.save(documentCommand)

        then:
        AssayDocument.count() == 2
        AssayDocument assayDocument = assay.documents.last()
        response.redirectedUrl == "/assayDefinition/show/${assay.id}#document-${assayDocument.id}"
        assayDocument.assay == assay
        assayDocument.documentName == "name"
        assayDocument.documentType == IDocumentType.DOCUMENT_TYPE_DESCRIPTION
        assayDocument.documentContent == "content"

    }

    void 'test save Assay Not found'() {
        when:
        documentCommand.assayId = -1
        documentCommand.documentType = IDocumentType.DOCUMENT_TYPE_PUBLICATION
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
        documentCommand.documentType = IDocumentType.DOCUMENT_TYPE_PUBLICATION
        controller.save(documentCommand)

        then:
        view == '/document/create'
        model.document.assayId == assay.id
        model.document.errors.hasErrors()
        assertFieldValidationExpectations(model.document, 'documentName', false, 'nullable')
    }

    void 'test edit no documentId'() {
        when:
        params.documentId = null
        def model = controller.edit()

        then:
        model.document.errors.getAllErrors().collect { it.codes }.flatten().contains('default.not.found.message')
    }

    void 'test edit'() {
        when:
        params.id = existingAssayDocument.id
        def model = controller.edit()

        then:
        model.document.documentId == existingAssayDocument.id
        model.document.documentName == existingAssayDocument.documentName
        model.document.documentType == existingAssayDocument.documentType
        model.document.documentContent == existingAssayDocument.documentContent
    }

    void 'test update succeed'() {
        when:

        assert existingAssayDocument.validate()
        documentCommand.assayId = assay.id
        documentCommand.version = existingAssayDocument.version
        documentCommand.documentId = existingAssayDocument.id
        documentCommand.documentContent = "new content"
        documentCommand.documentType = IDocumentType.DOCUMENT_TYPE_OTHER
        documentCommand.documentName = "new name"
        controller.update(documentCommand)

        then:
        response.redirectedUrl == "/assayDefinition/show/${assay.id}#document-${existingAssayDocument.id}"
        existingAssayDocument.documentContent == "new content"
        existingAssayDocument.documentName == "new name"
        existingAssayDocument.documentType == IDocumentType.DOCUMENT_TYPE_OTHER
    }

    void 'test update fail doc not found'() {
        when:
        documentCommand.documentId = -1
        documentCommand.assayId = assay.id
        controller.update(documentCommand)

        then:
        model.document.hasErrors()
    }

    void 'test delete'() {

        when:
        params.id = existingAssayDocument.id
        controller.delete()

        then:
        AssayDocument.count == 0
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
        desc                        | field             | vut                        | valid | code
        'assayId null'              | 'assayId'         | null                       | false | 'nullable'
        'assayId not null valid'    | 'assayId'         | 3                          | true  | null

        'documentId null valid'     | 'documentId'      | null                       | true  | null
        'documentId not null valid' | 'documentId'      | 3                          | true  | null

        'version null valid'        | 'version'         | null                       | true  | null
        'version not null valid'    | 'version'         | 3                          | true  | null

        'documentType blank'        | 'documentType'    | ''                         | false | 'blank'
        'documentType blank'        | 'documentType'    | ' '                        | false | 'blank'
        'documentType bad val'      | 'documentType'    | 'bad val'                  | false | 'not.inList'
        'documentType valid'        | 'documentType'    | DOCUMENT_TYPE_DESCRIPTION  | true  | null
        'documentType valid'        | 'documentType'    | DOCUMENT_TYPE_COMMENTS     | true  | null
        'documentType valid'        | 'documentType'    | DOCUMENT_TYPE_PROTOCOL     | true  | null
        'documentType valid'        | 'documentType'    | DOCUMENT_TYPE_PUBLICATION  | true  | null
        'documentType valid'        | 'documentType'    | DOCUMENT_TYPE_OTHER        | true  | null
        'documentType valid'        | 'documentType'    | DOCUMENT_TYPE_EXTERNAL_URL | true  | null

        'documentName null'         | 'documentName'    | null                       | false | 'nullable'
        'documentName blank'        | 'documentName'    | ''                         | false | 'blank'
        'documentName blank'        | 'documentName'    | ' '                        | false | 'blank'
        'documentName valid'        | 'documentName'    | 'docName'                  | true  | null

        'documentContent blank'     | 'documentContent' | ''                         | false | 'blank'
        'documentContent blank'     | 'documentContent' | ' '                        | false | 'blank'
        'documentContent null'      | 'documentContent' | null                       | true  | null

        'documentType null'         | 'documentType'    | null                       | false | 'nullable'
        'documentContent valid'     | 'documentContent' | 'docName'                  | true  | null

        //        'assayId not a number'  | 'assayId'         | 'a'                        | false | 'matches.invalid'
//        'assayId null'          | 'assayId'         | ''                         | false | 'blank'
//                'assayId null'          | 'assayId'         | ' '                        | false | 'blank'
    }

    void 'test command object createNewAssayDocument with #desc '() {
        when:
        DocumentCommand documentCommand = mockCommandObject(DocumentCommand)
        documentCommand.assayId = assayId.call()
        documentCommand.documentName = documentName
        documentCommand.documentType = documentType

        AssayDocument assayDocument = documentCommand.createNewAssayDocument()

        then:
        documentCommand?.hasErrors() == hasErrors
        (assayDocument?.id == null) == documentIdNull
        if (assayDocument) {
            assayDocument.documentName == documentName
            assayDocument.documentType == documentType
            assayDocument.documentContent == null
        }

        where:

        desc                        | assayId      | documentName   | documentType           | hasErrors | documentIdNull
        'error due to assayId'      | { null }     | 'documentName' | DOCUMENT_TYPE_COMMENTS | true      | true
        'error due to documentType' | { assay.id } | null           | DOCUMENT_TYPE_COMMENTS | true      | true
        'error due to documentName' | { assay.id } | 'documentName' | 'bad val'              | true      | true
        'valid command object'      | { assay.id } | 'documentName' | DOCUMENT_TYPE_COMMENTS | false     | false
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
        println("documentCommand.$field : ${documentCommand[(field)]}")
        println("assayDocument.$field : ${assayDocument[(field)]}")

        then:
        documentCommand[(field)] == assayDocument[(field)]
        documentCommand.assayId == assayDocument.assay.id

        where:
        field << DocumentCommand.PROPS_FROM_DOMAIN_TO_CMD
    }
}