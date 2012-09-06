package dataexport.registration

import bard.db.registration.Assay
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import spock.lang.Unroll

@Unroll
class AssayExportHelperServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_ASSAY_EXPORT_SCHEMA = "test/integration/dataexport/registration/assaySchema.xsd"

    AssayExportHelperService assayExportHelperService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate Measure Contexts #label #assayId"() {
        given: "Given an Assay Id"
        Assay assay = Assay.get(assayId)
        when: "A service call is made to generate measure contexts for that Assay"
        this.assayExportHelperService.generateAssayContexts(this.markupBuilder, assay.assayContexts)
        then: "An XML is generated that conforms to the expected XML"
        println  this.writer.toString()
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                | assayId             | results
        "Existing Measure Context for assay" | new BigDecimal("1") | XmlTestSamples.ASSAY_CONTEXTS
    }

    void "test generate Measure Context Items #label #assayId"() {
        given: "Given an Assay Id " + assayId
        final Assay assay = Assay.get(assayId)

        when: "A service call is made to generate the measure context items for that Assay"
        this.assayExportHelperService.generateAssayContextItems(this.markupBuilder, assay.assayContextItems)
        then: "An XML is generated that conforms to the expected XML"
        XMLAssert.assertXpathEvaluatesTo("1", "count(//assayContextItems)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("3", "count(//assayContextItem)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("4", "count(//link)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("3", "count(//attributeId)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//valueId)", this.writer.toString());

        where:
        label                                      | assayId             | results
        "Existing Measure Context Items for assay" | new BigDecimal("1") | XmlTestSamples.MEASURE_CONTEXT_ITEMS

    }

    void "test generate Measures #label #assayId"() {
        given: "Given an Assay Id " + assayId
        final Assay assay = Assay.get(assayId)

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportHelperService.generateMeasures(this.markupBuilder, assay.measures)
        then: "An XML is generated that conforms to the expected XML"
        println this.writer.toString()
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                         | assayId             | results
        "Existing Measures for assay" | new BigDecimal("1") | XmlTestSamples.MEASURES
    }

    void "test generate AssayDocument #label #assayId"() {
        given: "Given an Assay Id " + assayId
        final Assay assay = Assay.get(assayId)

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportHelperService.generateAssayDocuments(this.markupBuilder, assay.assayDocuments)
        then: "An XML is generated that conforms to the expected XML"
        XMLAssert.assertXpathEvaluatesTo("2", "count(//assayDocument)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("2", "count(//link)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=assayDoc", "//link/@type", this.writer.toString());
        where:
        label                          | assayId             | results
        "Existing documents for assay" | new BigDecimal("1") | XmlTestSamples.ASSAY_DOCUMENTS

    }

    void "test generate Assay #label #assayId"() {
        given: "Given an Assay Id " + assayId
        final Assay assay = Assay.get(assayId)

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportHelperService.generateAssay(this.markupBuilder, assay)
        then: "An XML is generated that conforms to the expected XML"
        println(this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//assayContexts)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//assayContext)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measures)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measure)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("2", "count(//assayContextItems)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("6", "count(//assayContextItem)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("2", "count(//assayDocument)", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("17", "count(//link)", this.writer.toString());

        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_ASSAY_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
        where:
        label            | assayId
        "Existing assay" | new BigDecimal("1")
    }

    void "test generate Assays #label"() {
        given: "Given there is at least one assay ready for extraction"
        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportHelperService.generateAssays(this.markupBuilder)
        then: "An XML is generated that conforms to the expected XML"
        println this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "//assays/@count", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("related", "//link/@rel", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=assay", "//link/@type", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("http://localhost:8080/dataExport/api/assays/1", "//link/@href", this.writer.toString());
        where:
        label                           | results
        "An Assay ready for extraction" | XmlTestSamples.ASSAYS

    }

    void "test generate Links For Assay #label"() {
        given:
        final String xpathTotalNumberOfLinks = "count(//link)"
        final String xpathNumberOfLinksToAssay = "count(//link[@type='application/vnd.bard.cap+xml;type=assay'])"
        final String xpathNumberOfLinksToAssays = "count(//link[@type='application/vnd.bard.cap+xml;type=assays'])"
        Assay assay = Assay.get(new BigDecimal("1"))
        when: "A service call is made to generate a list of links for an assay"
        this.markupBuilder.links() {
            this.assayExportHelperService.generateLinksForAssay(this.markupBuilder, assay)
        }
        then: "An XML is generated that conforms to the expected XML"
        XMLAssert.assertXpathEvaluatesTo("6", xpathTotalNumberOfLinks, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("2", xpathNumberOfLinksToAssay, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", xpathNumberOfLinksToAssays, this.writer.toString())
        where:
        label                           | results
        "An Assay ready for extraction" | XmlTestSamples.ASSAY_LINKS
    }
}
