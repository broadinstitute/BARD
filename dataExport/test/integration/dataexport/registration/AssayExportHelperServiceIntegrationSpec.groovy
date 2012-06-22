package dataexport.registration

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert

class AssayExportHelperServiceIntegrationSpec extends IntegrationSpec {
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
        when: "A service call is made to generate the measure context for that Assay"
        this.assayExportHelperService.generateMeasureContexts(this.markupBuilder, assayId)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                     | assayId              | results
        "Existing Measure Context for assay"      | new BigDecimal("1")  | XmlTestSamples.MEASURE_CONTEXTS
        "Assay Exist, but has no Measure Context" | new BigDecimal("2")  | XmlTestSamples.EMPTY_MEASURE_CONTEXTS
        "Assay Does not Exist"                    | new BigDecimal("21") | XmlTestSamples.EMPTY_MEASURE_CONTEXTS
    }

    void "test generate Measure Context Items #label #assayId"() {
        given: "Given an Assay Id " + assayId
        when: "A service call is made to generate the measure context items for that Assay"
        this.assayExportHelperService.generateMeasureContextItems(this.markupBuilder, assayId)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                           | assayId              | results
        "Existing Measure Context Items for assay"      | new BigDecimal("1")  | XmlTestSamples.MEASURE_CONTEXT_ITEMS
        "Assay Exist, but has no Measure Context Items" | new BigDecimal("2")  | XmlTestSamples.EMPTY_MEASURE_CONTEXT_ITEMS
        "Assay Does not Exist"                          | new BigDecimal("21") | XmlTestSamples.EMPTY_MEASURE_CONTEXT_ITEMS

    }

    void "test generate External Assays #label #assayId"() {
        given: "Given an Assay Id " + assayId
        when: "A service call is made to generate external assays for that Assay"
        this.assayExportHelperService.generateExternalAssays(this.markupBuilder, assayId)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                    | assayId              | results
        "Existing External Assays for assay"     | new BigDecimal("1")  | XmlTestSamples.EXTERNAL_ASSAYS
        "Assay Exist, but has no External assay" | new BigDecimal("2")  | XmlTestSamples.EMPTY_EXTERNAL_ASSAYS
        "Assay Does not Exist"                   | new BigDecimal("21") | XmlTestSamples.EMPTY_EXTERNAL_ASSAYS

    }

    void "test generate External Assay #label #externalAssayId"() {
        given: "Given an external assay id " + externalAssayId + " and an external system id " + externalSystemId
        when: "A service call is made to generate an external assay"
        this.assayExportHelperService.generateExternalAssay(this.markupBuilder, externalAssayId, externalSystemId)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                    | externalAssayId | externalSystemId     | results
        "Existing External Assay with System Id" | "aid=644"       | new BigDecimal("1")  | XmlTestSamples.EXTERNAL_ASSAY
        "External Assay not Exist"               | "aid=666"       | new BigDecimal("21") | XmlTestSamples.EMPTY_EXTERNAL_ASSAY
    }

    void "test generate Assays #label"() {
        given: "Given there is at least one assay ready for extraction"
        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportHelperService.generateAssays(this.markupBuilder)
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "//assays/@count", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("related", "//link/@rel", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=assay", "//link/@type", this.writer.toString());
        XMLAssert.assertXpathEvaluatesTo("http://localhost:8080/dataExport/api/assay/1", "//link/@href", this.writer.toString());
        where:
        label                           | results
        "An Assay ready for extraction" | XmlTestSamples.ASSAYS

    }

    void "test generate Links For Assay #label"() {
        given:
        final String xpathTotalNumberOfLinks = "count(//link)"
        final String xpathNumberOfLinksToAssay = "count(//link[@type='application/vnd.bard.cap+xml;type=assay'])"
        final String xpathNumberOfLinksToAssays = "count(//link[@type='application/vnd.bard.cap+xml;type=assays'])"

        when: "A service call is made to generate a list of links for an assay"
        this.markupBuilder.links() {
            this.assayExportHelperService.generateLinksForAssay(this.markupBuilder, new BigDecimal("1"))
        }
        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("3", xpathTotalNumberOfLinks, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("2", xpathNumberOfLinksToAssay, this.writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", xpathNumberOfLinksToAssays, this.writer.toString())
        where:
        label                           | results
        "An Assay ready for extraction" | XmlTestSamples.ASSAY_LINKS

    }
    //generateMeasures
     //generateAssayDocuments
    //generateAssay
    //generateAssay given an id

}
