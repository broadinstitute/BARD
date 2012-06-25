package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class AssayExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_ASSAY_EXPORT_SCHEMA = "test/integration/dataexport/registration/assaySchema.xsd"

    AssayExportService assayExportService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate and validate AssayDocument #label"() {
        given: "Given an Assay "
        final AssayDocument assayDocument = AssayDocument.get(1)

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportService.generateAssayDocument(this.markupBuilder, assayDocument.id)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_ASSAY_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))

    }

    void "test generate and validate Assay #label #assayId"() {
        given: "Given an Assay Id " + assayId
        final Assay assay = Assay.get(assayId)
        when: "A service call is made to generate the measures for that Assay"
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_ASSAY_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
        where:
        label            | assayId
        "Existing assay" | new BigDecimal("1")
    }

    void "test generate and validate Assays #label"() {
        given: "Given there is at least one assay ready for extraction"
        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportService.generateAssays(this.markupBuilder)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_ASSAY_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
    }
}
