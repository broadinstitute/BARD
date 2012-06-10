package barddataexport.dictionary

import groovy.xml.MarkupBuilder
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

    void setup() {
        this.dictionaryExportHelperService = new DictionaryExportHelperService()
    }

    void tearDown() {
        // Tear down logic here
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
    void "test Generate Attributes For Element Hierarchy #label #elementHierarchyId #parentElementId #childElementId"() {

        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForElementHierarchy(elementHierarchyId, parentElementId, childElementId)
        then:
        mapResults == results

        where:
        label               | elementHierarchyId  | parentElementId     | childElementId      | results
        "Full Hierarchy"    | new BigDecimal("1") | new BigDecimal("2") | new BigDecimal("3") | [elementHierarchyId: "1", parentElementId: "2", childElementId: "3"]
        "No parent Element" | new BigDecimal("2") | null                | new BigDecimal("3") | [elementHierarchyId: "2", childElementId: "3"]
        "No Child Element"  | new BigDecimal("3") | new BigDecimal("1") | null                | [elementHierarchyId: "3", parentElementId: "1"]
        "No Id or Parent"   | null                | null                | new BigDecimal("3") | [childElementId: "3"]
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
        given:
        def writer = new StringWriter()
        final MarkupBuilder xml = new MarkupBuilder(writer)
        when:

        this.dictionaryExportHelperService.generateSingleDescriptor(xml, dto)

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
        def writer = new StringWriter()
        final MarkupBuilder xml = new MarkupBuilder(writer)

        when:
        this.dictionaryExportHelperService.generateUnit(xml, dto)

        then:
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), results)
        assert xmlDiff.similar()
        where:
        label               | dto                                                                        | results
        "Full Unit element" | new UnitDTO(new BigDecimal("1"), new BigDecimal("1"), "cm", "Centimetres") | DictionaryXmlExamples.SINGLE_UNIT

    }

    void "test generate Lab #label #dto"() {

        final BigDecimal labId
        final BigDecimal parentLabId
        final String description
        final String labName
        final String laboratoryStatus
        given:
        def writer = new StringWriter()
        final MarkupBuilder xml = new MarkupBuilder(writer)

        when:
        this.dictionaryExportHelperService.generateLab(xml, dto)

        then:
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), results)
        assert xmlDiff.similar()
        where:
        label                   | dto                                                                                      | results
        "Full Lab"              | new LaboratoryDTO(new BigDecimal("1"), new BigDecimal("2"), "Desc", "labName", "Status") | DictionaryXmlExamples.SINGLE_UNIT
        "Full Lab no parent Id" | new LaboratoryDTO(new BigDecimal("1"), null, "Desc", "labName", "Status")                | DictionaryXmlExamples.SINGLE_UNIT

    }
}
class DictionaryXmlExamples {

    def static SINGLE_UNIT = '''
<unit unitId='1' parentUnitId='1' unit='cm'>
  <description>Centimetres</description>
</unit>
'''
}