package dataexport.dictionary

import bard.db.dictionary.Element
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder

import javax.servlet.http.HttpServletResponse
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseDecorator

import static groovyx.net.http.ContentType.JSON

class DictionaryExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_DICTIONARY_EXPORT_SCHEMA = "test/integration/dataexport/dictionary/dictionarySchema.xsd"
    DictionaryExportService dictionaryExportService
    private static final String BASE_URL = "http://bard.nih.gov/api/v1"
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Element"
        when: "We call the dictionary service to update this element"
        this.dictionaryExportService.update(new Long(100000), 0, "element")

        then: "An exception is thrown, indicating that the element does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given an Element with id #id and version #version"
        when: "We call the dictionary service to update this project"
        final BardHttpResponse bardHttpResponse = this.dictionaryExportService.update(elementId, version, status)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Element.get(elementId).readyForExtraction == expectedStatus

        where:
        label                                             | expectedStatusCode                         | expectedETag | elementId     | version | status     | expectedStatus
        "Return OK and ETag 1"                            | HttpServletResponse.SC_OK                  | new Long(1)  | new Long(386) | 0       | "Complete" | "Complete"
        "Return CONFLICT and ETag 0"                      | HttpServletResponse.SC_CONFLICT            | new Long(0)  | new Long(386) | -1      | "Complete" | "Ready"
        "Return PRECONDITION_FAILED and ETag 0"           | HttpServletResponse.SC_PRECONDITION_FAILED | new Long(0)  | new Long(386) | 2       | "Complete" | "Ready"
        "Return OK and ETag 0, Already completed Element" | HttpServletResponse.SC_OK                  | new Long(0)  | new Long(368) | 0       | "Complete" | "Complete"
    }

    void "test generate Stage"() {
        when:
        this.dictionaryExportService.generateStage(this.markupBuilder, element)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label   | element         | results
        "Stage" | new Long("341") | XmlTestSamples.STAGE
    }

    void "test generate Stage with non-existing id"() {
        when:
        this.dictionaryExportService.generateStage(this.markupBuilder, element)
        then:
        thrown(NotFoundException)
        where:
        label   | element
        "Stage" | new Long("300000000")
    }

    void "test generate ResultType"() {
        when:
        this.dictionaryExportService.generateResultType(this.markupBuilder, element)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label         | element         | results
        "Result Type" | new Long("341") | XmlTestSamples.RESULT_TYPE
    }

    void "test generate ResultType with non existing id"() {
        when:
        this.dictionaryExportService.generateResultType(this.markupBuilder, element)
        then:
        thrown(NotFoundException)
        where:
        label         | element
        "Result Type" | new Long("3410000000")
    }

    void "test generate Element With non existing Element Id"() {
        when:
        this.dictionaryExportService.generateElement(this.markupBuilder, element)
        then:
        thrown(NotFoundException)
        where:
        label     | element
        "Element" | new Long("30000000")
    }

    void "test generate Element"() {

        when:
        this.dictionaryExportService.generateElement(this.markupBuilder, element)
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())
        where:
        label      | element         | results
        "Elements" | new Long("386") | XmlTestSamples.ELEMENT
    }

    void "test generate Dictionary"() {
        when:
        this.dictionaryExportService.generateDictionary(this.markupBuilder)
        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_DICTIONARY_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(results)))
        where:
        label        | results
        "Dictionary" | XmlTestSamples.DICTIONARY
    }
    def "Test REST Query API: #label"() {
        given: "A service end point to get to the root elements"
        String requestUrl = "${BASE_URL}/${resourceString}"
        println("requestUrl: $requestUrl")

        RESTClient http = new RESTClient(requestUrl)

        when: "We send an HTTP GET request for the root elements"
        HttpResponseDecorator serverResponse = http.get(requestContentType: JSON)

        then: "We expect a JSON representation of the root elements"
        serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        serverResponse.data.size() > 0
        println(serverResponse.data)
//        println("serverResponse.data.size() : ${serverResponse.data.size()}")


        where:
        label                                    | resourceString
        "Get all projects"                       | "projects"
        "Get project by AID"                     | "projects/1772"
//        "Get assays for project"                 | "projects/1772/assays" // seems broken
        "Get probes for project"                 | "projects/1772/probes"
//        "Get targets for project"                | "projects/1772/targets" // seems broken
        "Get assay by assay ID"                  | "assays/604"
        "Get publications for assay"             | "assays/604/publications"
        "Get targets for assay"                  | "assays/604/targets"
        "Get experiments for assay"              | "assays/604/experiments"
//        "Get experiment details"                 | "assays/1048/experiment/1048"  //seems broken
        "Get experiment by experiment ID"        | "experiments/2480"
//        "Get results for experiment"             | "experiments/2480/activities"  //seems broken
        "Get compounds for experiment"           | "experiments/2480/compounds"
        "Get substances for experiment"          | "experiments/2480/substances"
        "Get experiment data entities"           | "experiments/2480/exptdata"
        "Get experiment data entity"             | "exptdata/2480"
        "Get all targets"                        | "targets"
        "Get target by accession"                | "targets/accession/P01112"
        "Get target by GeneID"                   | "targets/geneid/3265"
        "Get publications by accession"          | "targets/accession/P01112/publications"
        "Get document description by PubMed ID"  | "documents/1868473"
//        "Get document description by DOI"        | "documents/doi/10.1002%2Fcyto.a.10035" //seems broken
        "Get compound by CID"                    | "compounds/888706"
        "Get compound by SID"                    | "compounds/sid/57578335"
//        "Get compound by probe ID"               | "compounds/probeid/ML099" // slow at 24 seconds
        "Get experiment data for CID"            | "compounds/888706/exptdata"
        "Get experiments for CID"                | "compounds/888706/experiments"
        "Get substance by SID"                   | "substances/57578335"
        "Get substances by CID"                  | "substances/cid/888706"
        "Get experiment data for SID"            | "substances/57578335/exptdata"
        "Get experiments for SID"                | "substances/57578335/experiments"
    }

}
