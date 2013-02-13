package dataexport.dictionary

import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import grails.buildtestdata.mixin.Build
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll
import bard.db.dictionary.*

/**
 *
 */
@Unroll
@Build([UnitTree, Element, UnitConversion, StageTree])
class DictionaryExportHelperServiceUnitSpec extends Specification {
    DictionaryExportHelperService dictionaryExportHelperService = new DictionaryExportHelperService()
    LinkGenerator grailsLinkGenerator
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        grailsLinkGenerator = Mock(LinkGenerator.class)
        this.dictionaryExportHelperService.mediaTypesDTO = new MediaTypesDTO(elementMediaType: "xml")
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
        label                      | unitConversion                                                                                                                                     | results
        "Full Unit Conversion"     | new UnitConversion(fromUnit: new Element(label: 'fromUnit'), toUnit: new Element(label: 'toUnit'), formula: "formula", offset: 1, multiplier: 1.0) | [fromUnit: 'fromUnit', toUnit: 'toUnit', multiplier: "1.0", offset: "1.0"]
        "No Multiplier, No Offset" | new UnitConversion(fromUnit: new Element(label: 'fromUnit'), toUnit: new Element(label: 'toUnit'), formula: "formula")                             | [fromUnit: 'fromUnit', toUnit: 'toUnit']
    }
    /**
     * DictionaryExportHelperService#generateAttributesForUnit
     */
//    void "test Generate Attributes For Unit #label"() {
//        when:
//        final Map<String, String> mapResults =
//            this.dictionaryExportHelperService.generateAttributesForUnit(unit)
//        then:
//        mapResults == results
//
//        where:
//        label            | unit                                                      | results
//        "Full Unit"      | new Units(unit: "uM", elementLabel: "1", parentUnit: "2") | [unitElement: "1", parentUnit: "2", unit: "uM"]
//        "No parent Unit" | new Units(unit: "uM", elementLabel: "2")                  | [unitElement: "2", unit: "uM"]
//        "No Unit term"   | new Units(elementLabel: "3", parentUnit: "1")             | [unitElement: "3", parentUnit: "1"]
//    }

    /**
     * DictionaryExportHelperService#generateAttributesForStage()
     *  final BigDecimal stageId, final BigDecimal parentStageId, final String stageStatus
     */
    void "test generate Attributes For Stage #label"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForStage(stage.call())
        then:
        mapResults == results

        where:
        label                   | stage                                                                                                                | results
        "With all attributes"   | { StageTree.build(element: Element.build(label: "1"), parent: StageTree.build(element: Element.build(label: "2"))) } | [stageElement: "1", parentStageName: "2"]
        "With No Parent Statge" | { StageTree.build(element: Element.build(label: "2")) }                                                              | [stageElement: "2"]

    }
//    /**
//     * DictionaryExportHelperService#generateAttributesForResultType
//     */
//    void "test Generate Attributes For ResultType #label"() {
//        given:
//        when:
//        final Map<String, String> mapResults =
//            this.dictionaryExportHelperService.generateAttributesForResultType(dto)
//        then:
//        assert mapResults == results
//
//        where:
//        label                                       | dto                                                                                                                                          | results
//        "Result Type attaributes no abbreviation"   | new ResultType(resultTypeName: "name", description: "des", synonyms: "sun", baseUnit: "uM", resultTypeStatus: "Status")                      | [baseUnit: 'uM', resultTypeStatus: 'Status']
//        "Result Type attaributes with abbreviation" | new ResultType(resultTypeName: "name", description: "des", abbreviation: "abb", synonyms: "sun", baseUnit: "uM", resultTypeStatus: "Status") | [abbreviation: 'abb', baseUnit: 'uM', resultTypeStatus: 'Status']
//
//    }

    /**
     * DictionaryExportHelperService#generateAttributesForResultType
     */
    void "test Generate Attributes For Descriptor #label"() {
        when:
        final Map<String, String> mapResults =
            this.dictionaryExportHelperService.generateAttributesForDescriptor(descriptor)
        then:
        assert mapResults == results

        where:
        label                        | descriptor                                                                                                                                                                                                                                                                  | results
        "Descriptor with only label" | new AssayDescriptor(element: new Element(label: 'assay'))                                                                                                                                                                                                                   | [descriptor: 'assay', descriptorElement: 'assay']
        "Full Descriptor"            | new AssayDescriptor(parent: new AssayDescriptor(label: "parentLabel"), element: new Element(label: 'assay', description: "des", abbreviation: "abb", synonyms: "syn", externalURL: "http://www.broad.org", unit: new Element(label: "uM"), elementStatus: "elementStatus")) | [parentDescriptorLabel: "parentLabel", descriptorElement: "assay", abbreviation: 'abb', externalUrl: 'http://www.broad.org', unit: 'uM', descriptor: 'assay']
    }


    void "test generate Single Descriptor #label"() {
        when:
        this.dictionaryExportHelperService.generateSingleDescriptor(this.markupBuilder, descriptor)

        then:
        assert writer.toString() == results

        where:
        label                                | descriptor                                                                                                                                           | results
        "Should return an empty xml element" | new AssayDescriptor(element: new Element(label: 'assay'))                                                                                            | XmlTestSamples.SINGLE_DESCRIPTOR_ELEMENTS
        "Should return a full XML element"   | new AssayDescriptor(element: new Element(label: 'assay', description: "des", abbreviation: "abb", synonyms: "syn", externalURL: "http://broad.org")) | XmlTestSamples.SINGLE_DESCRIPTOR_ALL_ELEMENTS
    }

    void "test generate Unit #label"() {
        given:

        when:
        this.dictionaryExportHelperService.generateUnit(this.markupBuilder, unit)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label                      | unit                                                                                                                                          | results
        "Full Unit element"        | new UnitTree(element: new Element(label: "cm", description: "Centimetres"), parent: new UnitTree(element: new Element(label: 'length unit'))) | XmlTestSamples.SINGLE_UNIT
        "Unit With no Parent"      | new UnitTree(element: new Element(label: "cm", description: "Centimetres"))                                                                   | XmlTestSamples.SINGLE_UNIT_NO_PARENT
        "Unit With no Description" | new UnitTree(element: new Element(label: "cm"))                                                                                               | XmlTestSamples.SINGLE_UNIT_NO_DESCRIPTION
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
        label                     | stage                                                                                                                                              | results
        "Full Stage"              | new StageTree(element: new Element(label: "elementLabel", description: "desc"), parent: new StageTree(element: new Element(label: "parentStage"))) | XmlTestSamples.STAGE_FULL
        "Full Stage no parent Id" | new StageTree(element: new Element(label: "elementLabel", description: "desc"))                                                                    | XmlTestSamples.STAGE_NO_PARENT
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
        "Full Element"                | new Element(label: "label", description: "desc", abbreviation: "abb", synonyms: "syn", externalURL: "http://www.broad.org", unit: "cm", elementStatus: ElementStatus.Pending, readyForExtraction: ReadyForExtraction.READY) | XmlTestSamples.ELEMENT_FULL
        "Full Element no description" | new Element(label: "label", externalURL: "http://www.broad.org", unit: "cm", elementStatus: ElementStatus.Pending, readyForExtraction: ReadyForExtraction.READY)                                                            | XmlTestSamples.ELEMENT_NO_DESCRIPTION
    }

//    void "test generate Result Type #label"() {
//        when:
//        this.dictionaryExportHelperService.generateResultType(this.markupBuilder, resultType)
//        then:
//        XmlTestAssertions.assertResults(results, this.writer.toString())
//        where:
//        label                   | resultType                                                                                                                                                                                                                | results
//        "Full Result Type"      | new ResultType(parentResultTypeName: "resultTypeName", resultTypeLabel: "label", resultTypeName: "resultTypeName", description: "desc", abbreviation: "abb", synonyms: "syn", resultTypeStatus: "status", baseUnit: "cm") | XmlTestSamples.RESULT_TYPE_FULL
//        "Result Type no parent" | new ResultType(resultTypeLabel: "label", resultTypeName: "resultTypeName", description: "desc", abbreviation: "abb", synonyms: "syn", resultTypeStatus: "status")                                                         | XmlTestSamples.RESULT_TYPE_NO_PARENT
//
//    }

    void "test generate Descriptor #label"() {


        when:
        this.dictionaryExportHelperService.generateDescriptor(this.markupBuilder, descriptor)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label              | descriptor                                                                                                                                                                            | results
        "Assay descriptor" | new AssayDescriptor(element: new Element(label: "assay", description: "desc", abbreviation: "abb", synonyms: "syn", externalURL: "http://broad.org", unit: new Element(label: 'cm'))) | XmlTestSamples.ASSAY_DESCRIPTOR_UNIT


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
        label                      | unitConversion                                                                                                                                     | results

        "Full Unit Conversion"     | new UnitConversion(fromUnit: new Element(label: 'fromUnit'), toUnit: new Element(label: 'toUnit'), formula: "formula", offset: 1, multiplier: 1.0) | XmlTestSamples.UNIT_CONVERSION_FULL
        "No Multiplier, No Offset" | new UnitConversion(fromUnit: new Element(label: 'fromUnit'), toUnit: new Element(label: 'toUnit'), formula: "formula")                             | XmlTestSamples.UNIT_CONVERSION_NO_MULTIPLIER
    }
}