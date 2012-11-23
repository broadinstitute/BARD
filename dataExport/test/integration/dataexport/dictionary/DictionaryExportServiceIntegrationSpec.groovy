package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
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
import spock.lang.Unroll

@Unroll
class DictionaryExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_DICTIONARY_EXPORT_SCHEMA = "src/java/dictionarySchema.xsd"
    DictionaryExportService dictionaryExportService

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
        this.dictionaryExportService.update(new Long(100000), 0, "Complete")

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
        "Return OK and ETag 1"                            | HttpServletResponse.SC_OK                  | new Long(1)  | new Long(386) | 0       | "Complete" | ReadyForExtraction.Complete
        "Return CONFLICT and ETag 0"                      | HttpServletResponse.SC_CONFLICT            | new Long(0)  | new Long(386) | -1      | "Complete" | ReadyForExtraction.Ready
        "Return PRECONDITION_FAILED and ETag 0"           | HttpServletResponse.SC_PRECONDITION_FAILED | new Long(0)  | new Long(386) | 2       | "Complete" | ReadyForExtraction.Ready
        "Return OK and ETag 0, Already completed Element" | HttpServletResponse.SC_OK                  | new Long(0)  | new Long(368) | 0       | "Complete" | ReadyForExtraction.Complete
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

}
