package barddataexport.registration

import common.tests.XmlTestAssertions
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
class AssayExportHelperServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder
    LinkGenerator grailsLinkGenerator
    AssayExportHelperService assayExportHelperService

    void setup() {
        grailsLinkGenerator = Mock()
        this.assayExportHelperService =
            new AssayExportHelperService(new AssayDefinitionMediaTypesDTO("xml", "xml", "xml", "xml","xml"))
        this.assayExportHelperService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }


    void "test generate Measure #label"() {
        when: "We attempt to generate a measure in xml"
        this.assayExportHelperService.generateMeasure(this.markupBuilder, measureDTO)
        then: "A valid xml measure is generated with the expected measure attributes, result type and entry unit"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                           | measureDTO                                                                                               | results
        "Measure with Parent No Child Elements"         | new MeasureDTO(new BigDecimal("1"), new BigDecimal("2"), null, new BigDecimal("3"), null)                | MEASURE_1
        "Measure with Parent And ResultType"            | new MeasureDTO(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("4"), new BigDecimal("3"), null) | MEASURE_3
        "Measure with Parent,ResultType and Entry Unit" | new MeasureDTO(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("4"), new BigDecimal("3"), "%")  | MEASURE_2

    }

    void "test create Attributes For Measure #label"() {
        when: "We pass in a Measure DTO to Create a attributes for a Measure"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForMeasure(measureDTO)
        then: "A map with the expected key/value pairs is generated"
        results == attributes
        where:
        label                                       | measureDTO                                                                                | results
        "Measure With Parent "                      | new MeasureDTO(new BigDecimal("1"), new BigDecimal("2"), null, new BigDecimal("3"), null) | [measureId: "1", measureContextRef: "2", measureRef: "3"]
        "Measure with No Parent"                    | new MeasureDTO(new BigDecimal("1"), new BigDecimal("2"), null, null, null)                | [measureId: "1", measureContextRef: "2"]
        "Measure With Parent "                      | new MeasureDTO(new BigDecimal("1"), new BigDecimal("2"), null, new BigDecimal("3"), null) | [measureId: "1", measureContextRef: "2", measureRef: "3"]
        "Measure with No Parent No Measure Context" | new MeasureDTO(new BigDecimal("1"), null, null, null, null)                               | [measureId: "1"]

    }

    void "test Generate Measure Context #label"() {
        when: "We attempt to generate a measure context in xml"
        this.assayExportHelperService.generateMeasureContext(this.markupBuilder, contextId, contextName)
        then: "A valid xml measure context is generated with the expected measure context id and name"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                       | contextId           | contextName | results
        "Measure context with Id 1" | new BigDecimal("1") | "TestName1" | MEASURE_CONTEXT_1
        "Measure context with Id 2" | new BigDecimal("2") | "TextName2" | MEASURE_CONTEXT_2

    }

    void "test generate Measure Context Item #label"() {
        given: "A DTO"
        final MeasureContextItemDTO dto =
            new MeasureContextItemDTO(
                    new BigDecimal("1"), new BigDecimal("2"),
                    new BigDecimal("3"), attributeId,
                    valueId, new BigDecimal("5"),
                    new BigDecimal("6"), new BigDecimal("7"),
                    "Display", "<", "Fixed"
            )
        when: "We pass in a dto to create Measure Context Item document"
        this.assayExportHelperService.generateMeasureContextItem(this.markupBuilder, dto)
        then: "We expect back an xml document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                           | attributeId          | valueId              | results
        "Measure context Item with attribute and value" | new BigDecimal("10") | new BigDecimal("11") | MEASURE_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE
        "Measure context Item with attribute only"      | new BigDecimal("2")  | null                 | MEASURE_CONTEXT_ITEM_WITH_ATTRIBUTE

    }

    void "test Generate External System #label"() {
        when: "We attempt to generate an External System xml"
        this.assayExportHelperService.generateExternalSystem(this.markupBuilder, dto)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                | dto                                                                        | results
        "System Name Only"   | new ExternalSystemDTO("systemName", null, null, null)                      | EXTERNAL_SYSTEM_1
        "System Owner Only"  | new ExternalSystemDTO(null, "systemOwner", null, null)                     | EXTERNAL_SYSTEM_2
        "System Url Only"    | new ExternalSystemDTO(null, null, "systemUrl", null)                       | EXTERNAL_SYSTEM_3
        "With all variables" | new ExternalSystemDTO("systemName", "systemOwner", "systemUrl", "aid=234") | EXTERNAL_SYSTEM_4
    }

    void "test Generate Assay Document #label"() {
        when: "We attempt to generate an Assay document"
        this.assayExportHelperService.generateAssayDocument(this.markupBuilder, dto)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                | dto                                                                    | results
        "No Document Name"   | new AssayDocumentDTO(null, "Comments", new BigDecimal("6"))            | ASSAY_DOCUMENT_NO_DOCUMENT_NAME
        "With Document Name" | new AssayDocumentDTO("Document Name", "Comments", new BigDecimal("6")) | ASSAY_DOCUMENT_WITH_DOCUMENT_NAME

    }

    void "create Attributes For MeasureContextItem"() {
        given: "A DTO"
        final Map<String, String> results = [measureContextItemId: "1", measureContextItemRef: "2", measureContextRef: "3", qualifier: "<", valueDisplay: "Display", valueNum: "5", valueMin: "6", valueMax: "7"]
        MeasureContextItemDTO dto =
            new MeasureContextItemDTO(
                    new BigDecimal("1"), new BigDecimal("2"),
                    new BigDecimal("3"), new BigDecimal("4"),
                    new BigDecimal("5"), new BigDecimal("5"),
                    new BigDecimal("6"), new BigDecimal("7"),
                    "Display", "<", "attributeType"
            )
        when: "We pass in a dto to create Measure Context Item Attributes"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForMeasureContextItem(dto)
        then: "A map with the expected key/value pairs is generated"
        attributes == results

    }
    static final String ASSAY_DOCUMENT_WITH_DOCUMENT_NAME='''
    <assayDocument documentType='Comments'>
    <documentName>Document Name</documentName>
  <link rel='item' href='null' type='xml' />
    </assayDocument>
    '''
    static final String ASSAY_DOCUMENT_NO_DOCUMENT_NAME='''
   <assayDocument documentType='Comments'>
  <link rel='item' href='null' type='xml' />
</assayDocument>
'''
    static final String EXTERNAL_SYSTEM_4 = '''
<externalSystem>
  <systemName>systemName</systemName>
  <owner>systemOwner</owner>
  <systemUrl>systemUrlaid=234</systemUrl>
</externalSystem>
'''
    static final String EXTERNAL_SYSTEM_3 = '''
<externalSystem>
  <systemUrl>systemUrlnull</systemUrl>
</externalSystem>
'''
    static final String EXTERNAL_SYSTEM_2 = '''
<externalSystem>
  <owner>systemOwner</owner>
</externalSystem>
'''
    static final String EXTERNAL_SYSTEM_1 = '''
<externalSystem>
  <systemName>systemName</systemName>
</externalSystem>
'''
    static final String MEASURE_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE = '''<measureContextItem measureContextItemId='1' measureContextItemRef='2' measureContextRef='3' qualifier='&lt;' valueDisplay='Display' valueNum='5' valueMin='6' valueMax='7'>
    <valueId>
    <link rel='related' href='null' type='xml' />
    </valueId>
  <attributeId attributeType='Fixed'>
    <link rel='related' href='null' type='xml' />
    </attributeId>
</measureContextItem>'''

    static final String MEASURE_CONTEXT_ITEM_WITH_ATTRIBUTE = '''<measureContextItem measureContextItemId='1' measureContextItemRef='2' measureContextRef='3' qualifier='&lt;' valueDisplay='Display' valueNum='5' valueMin='6' valueMax='7'>
  <attributeId attributeType='Fixed'>
    <link rel='related' href='null' type='xml' />
    </attributeId>
</measureContextItem>'''

    static final String MEASURE_3 = '''
<measure measureId='1' measureContextRef='2' measureRef='3'>
  <resultTypeRef>
    <link rel='related' href='null' type='xml' />
  </resultTypeRef>
</measure>
'''
    static final String MEASURE_2 = '''<measure measureId='1' measureContextRef='2' measureRef='3'>
  <resultTypeRef>
    <link rel='related' href='null' type='xml' />
  </resultTypeRef>
  <entryUnit entryUnit='%' />
</measure>'''
    static final String MEASURE_1 = '''<measure measureId='1' measureContextRef='2' measureRef='3' />'''
    static final String MEASURE_CONTEXT_1 = '''<measureContext measureContextId='1'>
  <contextName>TestName1</contextName>
</measureContext>'''
    static final String MEASURE_CONTEXT_2 = '''
     <measureContext measureContextId='2'>
  <contextName>TextName2</contextName>
</measureContext>'''
}
