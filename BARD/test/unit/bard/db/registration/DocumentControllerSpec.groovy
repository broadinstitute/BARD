package bard.db.registration

import bard.db.dictionary.Element
import grails.buildtestdata.mixin.Build
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

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

    void 'test save'() {
        when:
        params.assayId = assay.id
        params.documentContent = "content"
        params.documentType = "Publication"
        params.documentName = "name"
        controller.save()

        then:
        response.redirectedUrl == '/assayDefinition/show/'+assay.id
        AssayDocument.count() == 1
        def doc = AssayDocument.getAll().get(0)
        doc.assay == assay
        doc.documentName == "name"
        doc.documentType == "Publication"
        doc.documentContent=="content"
    }

    void 'test update'() {
        when:
        AssayDocument document = AssayDocument.build(assay: assay)
        assert document.validate()
        params.id = document.id
        params.documentContent = "new content"
        params.documentType = "Other"
        params.documentName = "new name"
        def model = controller.update()

        then:
        response.redirectedUrl == '/assayDefinition/show/'+assay.id
        document.documentContent=="new content"
        document.documentName == "new name"
        document.documentType == "Other"
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
