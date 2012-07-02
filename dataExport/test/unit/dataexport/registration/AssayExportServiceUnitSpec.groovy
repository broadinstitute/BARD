package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import exceptions.NotFoundException
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(AssayExportService)
@Mock([Assay, AssayDocument])
class AssayExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    AssayExportService assayExportService

    void setup() {
        LinkGenerator grailsLinkGenerator = Mock()
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(resultTypeMediaType: "xml", elementMediaType: "xml", assaysMediaType: "xml", assayMediaType: "xml", assayDocMediaType: "xml")

        this.assayExportService = this.service
        AssayExportHelperService assayExportHelperService =
            new AssayExportHelperService(mediaTypesDTO)
        assayExportHelperService.grailsLinkGenerator = grailsLinkGenerator
        this.assayExportService.assayExportHelperService = assayExportHelperService
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test generate Assay #label"() {
        given: "An Assay"
        final Assay assay = new Assay(assayName: assayName, assayVersion: assayVersion, designedBy: designedBy, modifiedBy: "Bard", assayStatus: 'Pending', assayType: 'Regular', readyForExtraction: 'Ready')
        when: "We attempt to generate an Assay XML document"
        Assay.metaClass.static.get = {id -> assay }
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                         | assayName     | assayVersion | designedBy | results
        "Assay with no designer name" | "Assay Name1" | "2.3"        | ""         | XmlTestSamples.ASSAY_NO_DESIGNER_UNIT
        "Assay with designer name"    | "Assay Name2" | "3.3"        | "Broad"    | XmlTestSamples.ASSAY_UNIT

    }

    void "test Generate Assay Document #label"() {
        given:
        final AssayDocument assayDocument = new AssayDocument(documentType: documentType, documentContent: documentContent, documentName: documentName)
        AssayDocument.metaClass.static.get = {id -> assayDocument }
        when: "We attempt to generate an Assay document"
        this.assayExportService.generateAssayDocument(this.markupBuilder, assayDocument.id)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                               | documentType   | documentContent | documentName   | results
        "Document Type, Document Content, no document Name" | "documentType" | "Content"       | ""             | XmlTestSamples.ASSAY_DOCUMENT_NO_DOCUMENT_NAME_UNIT
        "With Document Name"                                | "documentType" | "Content"       | "documentName" | XmlTestSamples.ASSAY_DOCUMENT_WITH_DOCUMENT_NAME_UNIT
    }

    void "test Generate Assay Document Not Found Exception"() {
        given:
        AssayDocument.metaClass.static.get = {id -> null }
        when: "We attempt to generate an Assay document"
        this.assayExportService.generateAssayDocument(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test Generate Assay Not Found Exception"() {
        given:
        Assay.metaClass.static.get = {id -> null }
        when: "We attempt to generate an Assay"
        this.assayExportService.generateAssay(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }
}
