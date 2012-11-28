package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ElementHierarchy
import bard.db.dictionary.ElementStatus
import bard.db.dictionary.UnitConversion
import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
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
        grailsLinkGenerator = Mock(LinkGenerator.class)
        this.dictionaryExportHelperService =
            new DictionaryExportHelperService(new MediaTypesDTO(elementMediaType: "xml"))
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
    void "test Generate Attribute For UnitConversions #label"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForUnitConversion(unitConversion)
        then:
        mapResults == results

        where:
        label                      | unitConversion                                                                                                                     | results
        "Full Unit Conversion"     | new UnitConversion(fromUnit: "fromUnit", toUnit: "toUnit", formula: "formula", offset: new Float("1"), multiplier: new Float("1")) | [fromUnit: 'fromUnit', toUnit: 'toUnit', multiplier: "1.0", offset: "1.0"]
        "No Multiplier, No Offset" | new UnitConversion(fromUnit: "fromUnit", toUnit: "toUnit", formula: "formula")                                                     | [fromUnit: 'fromUnit', toUnit: 'toUnit']
    }
    /**
     * DictionaryExportHelperService#generateAttributesForUnit
     */
    void "test Generate Attributes For Unit #label"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForUnit(unit)
        then:
        mapResults == results

        where:
        label            | unit                                                      | results
        "Full Unit"      | new Units(unit: "uM", elementLabel: "1", parentUnit: "2") | [unitElement: "1", parentUnit: "2", unit: "uM"]
        "No parent Unit" | new Units(unit: "uM", elementLabel: "2")                  | [unitElement: "2", unit: "uM"]
        "No Unit term"   | new Units(elementLabel: "3", parentUnit: "1")             | [unitElement: "3", parentUnit: "1"]
    }

    /**
     * DictionaryExportHelperService#generateAttributesForStage()
     *  final BigDecimal stageId, final BigDecimal parentStageId, final String stageStatus
     */
    void "test generate Attributes For Stage #label"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForStage(stage)
        then:
        mapResults == results

        where:
        label                   | stage                                                       | results
        "With all attributes"   | new Stage(stageName: "stage1", label: "1", parentName: "2") | [stageElement: "1", parentStageName: "2"]
        "With No Parent Statge" | new Stage(stageName: "stage1", label: "2")                  | [stageElement: "2"]

    }
    /**
     * DictionaryExportHelperService#generateAttributesForResultType
     */
    void "test Generate Attributes For ResultType #label"() {
        given:
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForResultType(dto)
        then:
        assert mapResults == results

        where:
        label                                       | dto                                                                                                                                          | results
        "Result Type attaributes no abbreviation"   | new ResultType(resultTypeName: "name", description: "des", synonyms: "sun", baseUnit: "uM", resultTypeStatus: "Status")                      | [baseUnit: 'uM', resultTypeStatus: 'Status']
        "Result Type attaributes with abbreviation" | new ResultType(resultTypeName: "name", description: "des", abbreviation: "abb", synonyms: "sun", baseUnit: "uM", resultTypeStatus: "Status") | [abbreviation: 'abb', baseUnit: 'uM', resultTypeStatus: 'Status']

    }

    /**
     * DictionaryExportHelperService#generateAttributesForResultType
     */
    void "test Generate Attributes For Descriptor #label"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForDescriptor(dto)
        then:
        assert mapResults == results

        where:
        label                     | dto                                                                                                                                                                                                                                                                 | results
        "Descriptor with only Id" | new DescriptorDTO(label: "label", descriptor: 'assay')                                                                                                                                                                                                              | [descriptor: 'assay']
        "Full Descriptor"         | new DescriptorDTO(descriptorElement: "label", descriptor: 'assay', parentDescriptorLabel: "parentLabel", label: "label", description: "des", abbreviation: "abb", synonyms: "syn", externalUrl: "http://www.broad.org", unit: "uM", elementStatus: "elementStatus") | [parentDescriptorLabel: "parentLabel", descriptorElement: "label", abbreviation: 'abb', externalUrl: 'http://www.broad.org', unit: 'uM', descriptor: 'assay']
    }

    void "test generate Single Descriptor #label"() {
        when:
        this.dictionaryExportHelperService.generateSingleDescriptor(this.markupBuilder, dto)

        then:
        assert results == writer.toString()

        where:
        label                                | dto                                                                                                                                                                                    | results
        "Should return an empty xml element" | new DescriptorDTO(descriptor: 'assay')                                                                                                                                                 | ""
        "Should return a full XML element"   | new DescriptorDTO(descriptor: 'assay', label: "label", description: "des", abbreviation: "abb", synonyms: "syn", externalUrl: "http://broad.org", unit: "cm", elementStatus: "status") | '''<elementStatus>status</elementStatus>
<label>label</label>
<description>des</description>
<synonyms>syn</synonyms>'''
    }

    void "test generate Unit #label"() {
        given:

        when:
        this.dictionaryExportHelperService.generateUnit(this.markupBuilder, unit)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                      | unit                                                                     | results
        "Full Unit element"        | new Units(elementLabel: "label", unit: "cm", description: "Centimetres") | XmlTestSamples.SINGLE_UNIT
        "Unit With no Parent"      | new Units(elementLabel: "label", unit: "cm", description: "Centimetres") | XmlTestSamples.SINGLE_UNIT_NO_PARENT
        "Unit With no Description" | new Units(elementLabel: "label", unit: "cm")                             | XmlTestSamples.SINGLE_UNIT_NO_DESCRIPTION


    }


    void "test generate Lab #label"() {

        when:
        this.dictionaryExportHelperService.generateLab(this.markupBuilder, laboratory)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                   | laboratory                                                                                                              | results
        "Full Lab"              | new Laboratory(elementLabel: "Lab Element", parentLaboratory: "Parent", description: "Desc", laboratoryName: "labName") | XmlTestSamples.LABORATORY_SAMPLE_FULL
        "Full Lab no parent Id" | new Laboratory(elementLabel: "Lab Element", description: "Desc", laboratoryName: "labName")                             | XmlTestSamples.LABORATORY_SAMPLE_NO_PARENT

    }

    void "test generate Stage #label"() {

        when:
        this.dictionaryExportHelperService.generateStage(this.markupBuilder, stage)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                     | stage                                                                                                | results
        "Full Stage"              | new Stage(stageName: "Stage", description: "desc", parentName: "parentStage", label: "elementLabel") | XmlTestSamples.STAGE_FULL
        "Full Stage no parent Id" | new Stage(stageName: "Stage", description: "desc", label: "elementLabel")                            | XmlTestSamples.STAGE_NO_PARENT
    }

    void "test Generate Element Hierarchy #label"() {
        when:
        this.dictionaryExportHelperService.generateElementHierarchy(this.markupBuilder, elementHierarchy)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                         | elementHierarchy                                                                                                                           | results
        "Full Hierarchy"              | new ElementHierarchy(parentElement: new Element(label: "parent"), childElement: new Element(label: "child"), relationshipType: "Is Child") | XmlTestSamples.ELEMENT_HIERARCHY_FULL
        "Full Hierarchy no parent Id" | new ElementHierarchy(childElement: new Element(label: "child"), relationshipType: "Is Child")                                              | XmlTestSamples.ELEMENT_HIERARCHY_NO_PARENT

    }

    void "test generate Element #label"() {
        when:
        this.dictionaryExportHelperService.generateElement(this.markupBuilder, element)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                         | element                                                                                                                                                                                                                     | results
        "Full Element"                | new Element(label: "label", description: "desc", abbreviation: "abb", synonyms: "syn", externalURL: "http://www.broad.org", unit: "cm", elementStatus: ElementStatus.Pending, readyForExtraction: ReadyForExtraction.Ready) | XmlTestSamples.ELEMENT_FULL
        "Full Element no description" | new Element(label: "label", externalURL: "http://www.broad.org", unit: "cm", elementStatus: ElementStatus.Pending, readyForExtraction: ReadyForExtraction.Ready)                                                            | XmlTestSamples.ELEMENT_NO_DESCRIPTION
    }

    void "test generate Result Type #label"() {
        when:
        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, resultType)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                   | resultType                                                                                                                                                                                                                | results
        "Full Result Type"      | new ResultType(parentResultTypeName: "resultTypeName", resultTypeLabel: "label", resultTypeName: "resultTypeName", description: "desc", abbreviation: "abb", synonyms: "syn", resultTypeStatus: "status", baseUnit: "cm") | XmlTestSamples.RESULT_TYPE_FULL
        "Result Type no parent" | new ResultType(resultTypeLabel: "label", resultTypeName: "resultTypeName", description: "desc", abbreviation: "abb", synonyms: "syn", resultTypeStatus: "status")                                                         | XmlTestSamples.RESULT_TYPE_NO_PARENT

    }

    void "test generate Descriptor #label"() {


        when:
        this.dictionaryExportHelperService.generateDescriptor(this.markupBuilder, descriptor)
        then:

        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                 | descriptor                                                                                                                                                                                 | results
        "Assay descriptor"    | new DescriptorDTO(descriptor: 'assay', label: "label", description: "desc", abbreviation: "abb", synonyms: "syn", externalUrl: "http://broad.org", unit: "cm", elementStatus: "status")    | XmlTestSamples.ASSAY_DESCRIPTOR_UNIT
        "Biology Descriptor"  | new DescriptorDTO(descriptor: 'biology', label: "label", description: "desc", abbreviation: "abb", synonyms: "syn", externalUrl: "http://broad.org", unit: "cm", elementStatus: "status")  | XmlTestSamples.BIOLOGY_DESCRIPTOR_UNIT
        "Instance Descriptor" | new DescriptorDTO(descriptor: 'instance', label: "label", description: "desc", abbreviation: "abb", synonyms: "syn", externalUrl: "http://broad.org", unit: "cm", elementStatus: "status") | XmlTestSamples.INSTANCE_DESCRIPTOR_UNIT


    }
    /**
     * DictionaryExportHelperService#generateAttributesForUnitConversion
     */
    void "test Generate Unit Conversion #label"() {
        when:
        this.dictionaryExportHelperService.generateUnitConversion(this.markupBuilder, unitConversion)
        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label                      | unitConversion                                                                                                                     | results
        "Full Unit Conversion"     | new UnitConversion(fromUnit: "fromUnit", toUnit: "toUnit", formula: "formula", offset: new Float("1"), multiplier: new Float("1")) | XmlTestSamples.UNIT_CONVERSION_FULL
        "No Multiplier, No Offset" | new UnitConversion(fromUnit: "fromUnit", toUnit: "toUnit", formula: "formula")                                                     | XmlTestSamples.UNIT_CONVERSION_NO_MULTIPLIER
    }
}