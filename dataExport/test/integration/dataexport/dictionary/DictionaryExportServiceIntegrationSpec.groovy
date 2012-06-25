package dataexport.dictionary

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class DictionaryExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_DICTIONARY_EXPORT_SCHEMA = "test/integration/dataexport/dictionary/dictionarySchema.xsd"
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
