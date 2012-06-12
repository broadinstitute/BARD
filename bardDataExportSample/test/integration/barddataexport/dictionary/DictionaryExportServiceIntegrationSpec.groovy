package barddataexport.dictionary

import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Schema
import javax.xml.validation.Validator

class DictionaryExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_DICTIONARY_EXPORT_SCHEMA = "test/integration/barddataexport/dictionary/dictionarySchema.xsd"
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
    /**
     * Use assert == true here
     * @param results
     */
    void assertResults(final String results) {
        XMLUnit.setIgnoreWhitespace(true)
        final Diff xmlDiff = new Diff(this.writer.toString(), results)
        assert true == xmlDiff.similar()
    }

    void "test generate Stage"() {
        when:
        this.dictionaryExportService.generateStage(this.markupBuilder, new BigDecimal("341"))
        then:
        assertResults(results)
        where:
        label   | results
        "Stage" | DictionaryIntegrationXml.STAGE
    }

    void "test generate ResultType"() {
        when:
        this.dictionaryExportService.generateResultType(this.markupBuilder, new BigDecimal("341"))
        then:
        assertResults(results)
        where:
        label         | results
        "Result Type" | DictionaryIntegrationXml.RESULT_TYPE
    }

    void "test generate Element With Element Id"() {
        when:
        this.dictionaryExportService.generateElementWithElementId(this.markupBuilder, new BigDecimal("386"))
        then:
        assertResults(results)
        where:
        label     | results
        "Element" | DictionaryIntegrationXml.ELEMENT
    }

    void "test generate Element"() {

        when:
        this.dictionaryExportService.generateElement(this.markupBuilder, dto)
        then:
        assertResults(results)
        where:
        label      | dto                                                                                             | results
        "Elements" | new ElementDTO(new BigDecimal("386"), 'uM', null, null, null, null, null, 'Published', 'Ready') | DictionaryIntegrationXml.ELEMENT
    }

    void "test generate Dictionary"() {
        when:
        this.dictionaryExportService.generateDictionary(this.markupBuilder)
        then:
        assertResults(results)
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_DICTIONARY_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(results)))
        where:
        label        | results
        "Dictionary" | DictionaryIntegrationXml.DICTIONARY
    }

}
