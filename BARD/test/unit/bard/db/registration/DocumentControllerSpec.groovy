package bard.db.registration

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.Before
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(DocumentController)
@TestMixin(DomainClassUnitTestMixin)
@Build([AssayDocument, Assay])
class DocumentControllerSpec extends Specification {
    Assay assay;

    @Before
    void setup() {
        assay = Assay.build()
        assert flash.message == null
    }

    void tearDown() {
        // Tear down logic here
    }

    void 'test create'() {
        when:
        params.assayId = assay.id
        def model = controller.create()

        then:
        model.assayId == assay.id
    }

    void 'test valid save'() {
        when:
        params.assayId = assay.id
        params.documentContent = "content"
        params.documentType = "Publication"
        params.documentName = "name"
        controller.save()

        then:
        AssayDocument.count() == 1
        AssayDocument assayDocument = assay.documents.first()
        response.redirectedUrl == "/assayDefinition/edit/${assay.id}#document-${assayDocument.id}"
        assayDocument.assay == assay
        assayDocument.documentName == "name"
        assayDocument.documentType == "Publication"
        assayDocument.documentContent == "content"
    }

    void 'test save Assay Not found'() {
        when:
        params.assayId = assay.id
        params.documentType = "Publication"
        controller.save()

        then:
        view == 'document/create'


    }

    void 'test save invalid doc'() {
        when:
        params.assayId = assay.id
        par
        controller.save()

        then:
        flash.message == "default.not.found.message"
    }

    void 'test update succeed'() {
        when:
        AssayDocument document = AssayDocument.build(assay: assay)
        assert document.validate()
        params.id = document.id
        params.documentContent = "new content"
        params.documentType = "Other"
        params.documentName = "new name"
        controller.update()

        then:
        response.redirectedUrl == "/assayDefinition/edit/${assay.id}#document-${document.id}"
        document.documentContent == "new content"
        document.documentName == "new name"
        document.documentType == "Other"
    }

    void 'test update fail doc not found'() {
        when:
        params.id = -1
        params.assayId = assay.id
        controller.update()

        then:
        flash.message == 'default.not.found.message'
    }

    void 'test edit'() {
        when:
        AssayDocument document = AssayDocument.build(assay: assay)
        params.id = document.id
        def model = controller.edit()

        then:
        model.document == document
    }

    void 'test delete'() {
        when:
        def document = AssayDocument.build()

        then:
        AssayDocument.count == 1

        when:
        params.id = document.id
        controller.delete()

        then:
        AssayDocument.count == 0
    }
}
