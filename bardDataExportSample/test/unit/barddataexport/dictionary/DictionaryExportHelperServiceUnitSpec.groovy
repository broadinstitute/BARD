package barddataexport.dictionary

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 */
@Unroll
class DictionaryExportHelperServiceUnitSpec extends Specification {
    DictionaryExportHelperService dictionaryExportHelperService
    LinkGenerator grailsLinkGenerator
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        grailsLinkGenerator = Mock()
        this.dictionaryExportHelperService =
            new DictionaryExportHelperService("xml")
        this.dictionaryExportHelperService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     * DictionaryExportHelperService#generateAttributesForUnitConversion
     */
    void "test Generate Attribute For UnitConversions #label #dto"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForUnitConversion(dto)
        then:
        mapResults == results

        where:
        label                      | dto                                                                                              | results
        "Full Unit Conversion"     | new UnitConversionDTO("fromUnit", "toUnit", "formula", new BigDecimal("1"), new BigDecimal("1")) | [fromUnit: 'fromUnit', toUnit: 'toUnit', multiplier: "1", offset: "1"]
        "No Multiplier, No Offset" | new UnitConversionDTO("fromUnit", "toUnit", "formula", null, null)                               | [fromUnit: 'fromUnit', toUnit: 'toUnit']
    }
    /**
     * DictionaryExportHelperService#generateAttributesForUnit
     */
    void "test Generate Attributes For Unit #label #unitId #parentUnitId #unit"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForUnit(unitId, parentUnitId, unit)
        then:
        mapResults == results

        where:
        label                           | unitId              | parentUnitId        | unit | results
        "Full Unit"                     | new BigDecimal("1") | new BigDecimal("2") | "uM" | [unitId: "1", parentUnitId: "2", unit: "uM"]
        "No parent Unit"                | new BigDecimal("2") | null                | "uM" | [unitId: "2", unit: "uM"]
        "No Unit term"                  | new BigDecimal("3") | new BigDecimal("1") | ""   | [unitId: "3", parentUnitId: "1"]
        "Unit and Parent Unit are null" | null                | null                | "cm" | [unit: "cm"]
    }
    /**
     * DictionaryExportHelperService#generateAttributesForElementHierarchy
     */
    void "test Generate Attributes For Element Hierarchy #label#parentElementId #childElementId"() {

        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForElementHierarchy(parentElementId, childElementId)
        then:
        mapResults == results

        where:
        label               | parentElementId     | childElementId      | results
        "Full Hierarchy"    | new BigDecimal("2") | new BigDecimal("3") | [parentElementId: "2", childElementId: "3"]
        "No parent Element" | null                | new BigDecimal("3") | [childElementId: "3"]
        "No Child Element"  | new BigDecimal("1") | null                | [parentElementId: "1"]
    }
    /**
     * DictionaryExportHelperService#generateAttributesForStage()
     *  final BigDecimal stageId, final BigDecimal parentStageId, final String stageStatus
     */
    void "test generate Attributes For Stage #label #stageId #parentStageId #status"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForStage(stageId, parentStageId, status)
        then:
        mapResults == results

        where:
        label                             | stageId             | parentStageId       | status      | results
        "With all attributes"             | new BigDecimal("1") | new BigDecimal("2") | "Published" | [stageId: "1", parentStageId: "2", stageStatus: "Published"]
        "With No Parent Statge"           | new BigDecimal("2") | null                | "Published" | [stageId: "2", stageStatus: "Published"]
        "With No Status"                  | new BigDecimal("3") | new BigDecimal("1") | null        | [stageId: "3", parentStageId: "1"]
        "With No StageId or Parent Stage" | null                | null                | "Published" | [stageStatus: "Published"]

    }
    /**
     * DictionaryExportHelperService#generateAttributesForResultType
     */
    void "test Generate Attributes For ResultType #label #dto"() {

        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForResultType(dto)
        then:
        assert mapResults == results

        where:
        label                           | dto                                                                                                      | results
        "Result Type with parent Id"    | new ResultTypeDTO(new BigDecimal("1"), new BigDecimal("1"), "name", "des", "abb", "sun", "uM", "Status") | [resultTypeId: "1", parentResultTypeId: "1", abbreviation: 'abb', baseUnit: 'uM', resultTypeStatus: 'Status']
        "Result Type with no Parent Id" | new ResultTypeDTO(null, new BigDecimal("1"), "name", "des", "abb", "sun", "uM", "Status")                | [resultTypeId: "1", abbreviation: 'abb', baseUnit: 'uM', resultTypeStatus: 'Status']

    }

    /**
     * DictionaryExportHelperService#generateAttributesForResultType
     */
    void "test Generate Attributes For Descriptor #label #dto"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForDescriptor(dto)
        then:
        assert mapResults == results

        where:
        label                     | dto                                                                                                                                                           | results
        "Descriptor with only Id" | new DescriptorDTO(null, new BigDecimal("1"), null, null, null, null, null, null, null, null)                                                                  | [descriptorId: "1"]
        "Full Descriptor"         | new DescriptorDTO(new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), "label", "des", "abb", "syn", "http://www.broad.org", "uM", "elementStatus") | [descriptorId: "1", parentDescriptorId: "1", elementId: "1", abbreviation: 'abb', externalUrl: 'http://www.broad.org', unit: 'uM']
    }

    void "test generate Single Descriptor #label #dto"() {
        when:
        this.dictionaryExportHelperService.generateSingleDescriptor(this.markupBuilder, dto)

        then:
        assert results == writer.toString()

        where:
        label                                | dto                                                                                                                                                           | results
        "Should return an empty xml element" | new DescriptorDTO(null, new BigDecimal("1"), null, null, null, null, null, null, null, null)                                                                  | ""
        "Should return a full XML element"   | new DescriptorDTO(new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), "label", "des", "abb", "syn", "http://www.broad.org", "uM", "elementStatus") | '''<elementStatus>elementStatus</elementStatus>
<label>label</label>
<description>des</description>
<synonyms>syn</synonyms>'''
    }

    void "test generate Unit #label #dto"() {
        given:

        when:
        this.dictionaryExportHelperService.generateUnit(this.markupBuilder, dto)

        then:
        assertXml(results)
        where:
        label                      | dto                                                                        | results
        "Full Unit element"        | new UnitDTO(new BigDecimal("1"), new BigDecimal("1"), "cm", "Centimetres") | DictionaryXmlExamples.SINGLE_UNIT
        "Unit With no Parent"      | new UnitDTO(new BigDecimal("1"), null, "cm", "Centimetres")                | DictionaryXmlExamples.SINGLE_UNIT_NO_PARENT
        "Unit With no Description" | new UnitDTO(new BigDecimal("1"), new BigDecimal("1"), "cm", null)          | DictionaryXmlExamples.SINGLE_UNIT_NO_DESCRIPTION


    }
    /**
     * Be sure to use asserts in helper methods
     * @param writer
     * @param results
     */
    void assertXml(final String results) {
        XMLUnit.setIgnoreWhitespace(true)
        final Diff xmlDiff = new Diff(this.writer.toString(), results)
        assert true == xmlDiff.similar()
    }

    void "test generate Lab #label #dto"() {

        when:
        this.dictionaryExportHelperService.generateLab(this.markupBuilder, dto)

        then:
        assertXml(results)
        where:
        label                   | dto                                                                                      | results
        "Full Lab"              | new LaboratoryDTO(new BigDecimal("1"), new BigDecimal("2"), "Desc", "labName", "Status") | DictionaryXmlExamples.LABORATORY_SAMPLE_FULL
        "Full Lab no parent Id" | new LaboratoryDTO(new BigDecimal("1"), null, "Desc", "labName", "Status")                | DictionaryXmlExamples.LABORATORY_SAMPLE_NO_PARENT

    }

    void "test generate Stage #label #dto"() {

        when:
        this.dictionaryExportHelperService.generateStage(this.markupBuilder, dto)
        then:
        assertXml(results)

        where:
        label                     | dto                                                                               | results
        "Full Stage"              | new StageDTO(new BigDecimal("1"), new BigDecimal("2"), "Stage", "desc", "Status") | DictionaryXmlExamples.STAGE_FULL
        "Full Stage no parent Id" | new StageDTO(new BigDecimal("1"), null, "Stage", "desc", "Status")                | DictionaryXmlExamples.STAGE_NO_PARENT
    }

    void "test Generate Element Hierarchy #label #dto"() {
        when:
        this.dictionaryExportHelperService.generateElementHierarchy(this.markupBuilder, dto)

        then:
        assertXml(results)
        where:
        label                         | dto                                                                           | results
        "Full Hierarchy"              | new ElementHierarchyDTO(new BigDecimal("1"), new BigDecimal("2"), "Is Child") | DictionaryXmlExamples.ELEMENT_HIERARCHY_FULL
        "Full Hierarchy no parent Id" | new ElementHierarchyDTO(null, new BigDecimal("2"), "Is Child")                | DictionaryXmlExamples.ELEMENT_HIERARCHY_NO_PARENT

    }

    void "test generate Element #label #dto"() {
        when:
        this.dictionaryExportHelperService.generateElement(this.markupBuilder, dto)
        then:
        assertXml(results)
        where:
        label                         | dto                                                                                                                 | results
        "Full Element"                | new ElementDTO(new BigDecimal("1"), "label", "desc", "abb", "syn", "http://www.broad.org", "cm", "status", "ready") | DictionaryXmlExamples.ELEMENT_FULL
        "Full Element no description" | new ElementDTO(new BigDecimal("1"), "label", null, null, null, "http://www.broad.org", "cm", "status", "ready")     | DictionaryXmlExamples.ELEMENT_NO_DESCRIPTION
    }

    void "test generate Result Type #label #dto"() {
        when:
        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, dto)
        then:
        assertXml(results)
        where:
        label                   | dto                                                                                                                 | results
        "Full Result Type"      | new ResultTypeDTO(new BigDecimal("1"), new BigDecimal("1"), "resultTypeName", "desc", "abb", "syn", "cm", "status") | DictionaryXmlExamples.RESULT_TYPE_FULL
        "Result Type no parent" | new ResultTypeDTO(null, new BigDecimal("1"), "resultTypeName", "desc", "abb", "syn", "cm", "status")                | DictionaryXmlExamples.RESULT_TYPE_NO_PARENT

    }

    void "test generate Descriptor #label #dto #descriptorType"() {


        when:
        this.dictionaryExportHelperService.generateDescriptor(this.markupBuilder, dto, descriptorType)
        then:
        assertXml(results)
        where:
        label                 | dto                                                                                                                                                 | descriptorType                     | results
        "Assay descriptor"    | new DescriptorDTO(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"), "label", "desc", "abb", "syn", "http://broad.org", "cm", "status") | DescriptorType.ASSAY_DESCRIPTOR    | DictionaryXmlExamples.ASSAY_DESCRIPTOR
        "Biology Descriptor"  | new DescriptorDTO(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"), "label", "desc", "abb", "syn", "http://broad.org", "cm", "status") | DescriptorType.BIOLOGY_DESCRIPTOR  | DictionaryXmlExamples.BIOLOGY_DESCRIPTOR
        "Instance Descriptor" | new DescriptorDTO(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"), "label", "desc", "abb", "syn", "http://broad.org", "cm", "status") | DescriptorType.INSTANCE_DESCRIPTOR | DictionaryXmlExamples.INSTANCE_DESCRIPTOR


    }
    /**
     * DictionaryExportHelperService#generateAttributesForUnitConversion
     */
    void "test Generate Unit Conversion #label #dto"() {
        when:
        this.dictionaryExportHelperService.generateUnitConversion(this.markupBuilder, dto)
        then:
        assertXml(results)

        where:
        label                      | dto                                                                                              | results
        "Full Unit Conversion"     | new UnitConversionDTO("fromUnit", "toUnit", "formula", new BigDecimal("1"), new BigDecimal("1")) | DictionaryXmlExamples.UNIT_CONVERSION_FULL
        "No Multiplier, No Offset" | new UnitConversionDTO("fromUnit", "toUnit", "formula", null, null)                               | DictionaryXmlExamples.UNIT_CONVERSION_NO_MULTIPLIER
    }
}
class DictionaryXmlExamples {
    static String UNIT_CONVERSION_FULL = '''
<unitConversion fromUnit='fromUnit' toUnit='toUnit' multiplier='1' offset='1'>
  <formula>formula</formula>
</unitConversion>
'''
    static String UNIT_CONVERSION_NO_MULTIPLIER = '''
    <unitConversion fromUnit='fromUnit' toUnit='toUnit'>
    <formula>formula</formula>
</unitConversion>
'''

    static String INSTANCE_DESCRIPTOR = '''
<instanceDescriptor descriptorId='2' parentDescriptorId='1' elementId='3' abbreviation='abb' externalUrl='http://broad.org' unit='cm'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</instanceDescriptor>
'''

    static String BIOLOGY_DESCRIPTOR = '''
<biologyDescriptor descriptorId='2' parentDescriptorId='1' elementId='3' abbreviation='abb' externalUrl='http://broad.org' unit='cm'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</biologyDescriptor>
'''
    static String ASSAY_DESCRIPTOR = '''
<assayDescriptor descriptorId='2' parentDescriptorId='1' elementId='3' abbreviation='abb' externalUrl='http://broad.org' unit='cm'>
  <elementStatus>status</elementStatus>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
</assayDescriptor>
'''

    static String RESULT_TYPE_NO_PARENT = '''
<resultType resultTypeId='1' abbreviation='abb' baseUnit='cm' resultTypeStatus='status'>
  <resultTypeName>resultTypeName</resultTypeName>
  <description>desc</description>
  <synonyms>syn</synonyms>
</resultType>
'''

    static String RESULT_TYPE_FULL = '''
<resultType resultTypeId='1' parentResultTypeId='1' abbreviation='abb' baseUnit='cm' resultTypeStatus='status'>
  <resultTypeName>resultTypeName</resultTypeName>
  <description>desc</description>
  <synonyms>syn</synonyms>
</resultType>
'''


    static String ELEMENT_FULL = '''
<element elementId='1' readyForExtraction='ready' elementStatus='status' abbreviation='abb' unit='cm'>
  <label>label</label>
  <description>desc</description>
  <synonyms>syn</synonyms>
  <externalUrl>http://www.broad.org</externalUrl>
  <link rel='edit' href='null' type='xml' />
</element>
'''
    static String ELEMENT_NO_DESCRIPTION = '''
<element elementId='1' readyForExtraction='ready' elementStatus='status' unit='cm'>
  <label>label</label>
  <externalUrl>http://www.broad.org</externalUrl>
  <link rel='edit' href='null' type='xml' />
</element>
'''
    static String ELEMENT_HIERARCHY_FULL = '''
<elementHierarchy parentElementId='1' childElementId='2'>
  <relationshipType>Is Child</relationshipType>
</elementHierarchy>
'''

    static String ELEMENT_HIERARCHY_NO_PARENT = '''
<elementHierarchy childElementId='2'>
  <relationshipType>Is Child</relationshipType>
</elementHierarchy>
'''
    static String STAGE_FULL = '''
    <stage stageId='1' parentStageId='2' stageStatus='Status'>
    <stageName>Stage</stageName>
  <description>desc</description>
    </stage>
    '''

    static String STAGE_NO_PARENT = '''
<stage stageId='1' stageStatus='Status'>
  <stageName>Stage</stageName>
  <description>desc</description>
</stage>
'''

    static String SINGLE_UNIT = '''
<unit unitId='1' parentUnitId='1' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
    static String SINGLE_UNIT_NO_DESCRIPTION = '''
<unit unitId='1' parentUnitId='1' unit='cm'>
</unit>
'''
    static String SINGLE_UNIT_NO_PARENT = '''
<unit unitId='1' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
    static String LABORATORY_SAMPLE_FULL = '''
<laboratory laboratoryId='1' parentLaboratoryId='2' laboratoryStatus='Status'>
  <laboratoryName>labName</laboratoryName>
  <description>Desc</description>
</laboratory>
'''

    static String LABORATORY_SAMPLE_NO_PARENT = '''
<laboratory laboratoryId='1' laboratoryStatus='Status'>
  <laboratoryName>labName</laboratoryName>
  <description>Desc</description>
</laboratory>
'''
}