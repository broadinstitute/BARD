package dataexport.registration

import bard.db.dictionary.Element
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.buildtestdata.mixin.Build
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll
import bard.db.registration.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, AssayDocument, Element, Measure])
@Unroll
class AssayExportHelperServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder
    LinkGenerator grailsLinkGenerator
    AssayExportHelperService assayExportHelperService

    void setup() {
        grailsLinkGenerator = Mock(LinkGenerator.class)
        final MediaTypesDTO mediaTypesDTO = new MediaTypesDTO(resultTypeMediaType: "xml", elementMediaType: "xml", assaysMediaType: "xml", assayMediaType: "xml", assayDocMediaType: "xml")
        this.assayExportHelperService =
            new AssayExportHelperService(mediaTypesDTO)
        this.assayExportHelperService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    /**
     * uses passed in map but manually sets the ID property as this can't be set the map constructor
     * @return a measure
     */
    Measure createMeasure(Map map = [:]) {
        // resulttype is required so included unless specifically passed in
        if (!map.containsKey('resultType')) {
            map.resultType = new Element(label: "resultType")
        }
        Measure measure = new Measure(map)
        measure.@id = map.id ?: 1
        measure
    }

    /**
     * create a assayContext with required fields
     * @param map
     * @return
     */
    AssayContext createAssayContext(Map map = [:]) {
        if (!map.containsKey('contextName')) {
            map.contextName = 'contextName'
        }
        AssayContext assayContext = AssayContext.build(map)
        assayContext.@id = map.id ?: 1
        assayContext
    }

    AssayContextItem createAssayContextItem(Map map = [:]) {
        AssayContextItem.build(map)
    }

    void "test generate Measure #label"() {
        when: "We attempt to generate a measure in xml"
        this.assayExportHelperService.generateMeasure(this.markupBuilder, measure.call())
        then: "A valid xml measure is generated with the expected measure attributes, result type and entry unit"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                    | measure                                                                                                    | results
        "minimal Measure"                        | {createMeasure()}                                                                                          | XmlTestSamples.MINIMAL_MEASURE
        "minimal Measure with parentMeasureRef"  | {createMeasure(id: 2, parentMeasure: createMeasure())}                                                     | XmlTestSamples.MINIMAL_MEASURE_WITH_PARENT_MEASURE_REF
        "minimal Measure with statsModifierRef"  | {createMeasure(statsModifier: new Element(label: "statsModifier"))}                                        | XmlTestSamples.MINIMAL_MEASURE_WITH_STATS_MODIFIER_REF
        "minimal Measure with entryUnitRef"      | {createMeasure(entryUnit: new Element(label: "entryUnit"))}                                                | XmlTestSamples.MINIMAL_MEASURE_WITH_ENTRY_UNIT_REF
        "minimal Measure with assayContextRefs " | {createMeasure(assayContextMeasures: [new AssayContextMeasure(assayContext: createAssayContext(id: 20))])} | XmlTestSamples.MINIMAL_MEASURE_WITH_ASSAY_CONTEXT_REFS

    }

    void "test generate Assay Context #label"() {
        def localAc = valueUnderTest.call()
        when: "We attempt to generate a measure context in xml"
        this.assayExportHelperService.generateAssayContext(this.markupBuilder, localAc)
        then: "A valid xml measure context is generated with the expected measure context id and name"
        println(this.writer.toString())
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                         | valueUnderTest                                                                                        | results
        "minimal AssayContext "                       | {createAssayContext()}                                                                                | XmlTestSamples.MINIMAL_ASSAY_CONTEXT
        "minimal AssayContext with contextGroup"      | {createAssayContext(contextGroup: 'contextGroup')}                                                    | XmlTestSamples.MINIMAL_ASSAY_CONTEXT_WITH_CONTEXT_GROUP
        "minimal AssayContext with assayContextItems" | {def aci = AssayContextItem.build(); aci.assayContext.contextName = 'contextName'; aci.assayContext } | XmlTestSamples.MINIMAL_ASSAY_CONTEXT_WITH_ASSAY_CONTEXT_ITEM


    }

    void "test generate AssayContextItem #label"() {
        given:
        Element attributeElement = Element.build(label: attributeLabel)
        Element valueElement = null
        if (valueLabel) {
            valueElement = new Element(label: valueLabel)
        }
        final AssayContextItem assayContextItem =
            createAssayContextItem(attributeType: AttributeType.Fixed,
                    attributeElement: attributeElement,
                    valueElement: valueElement,
                    valueDisplay: "Display",
                    valueMax: new Float("7.0"),
                    valueMin: new Float("6.0"),
                    valueNum: new Float("5.0"),
                    modifiedBy: "Bard",
                    qualifier: "< ")


        when: "We pass in a assay context item we get a good xml document"
        this.assayExportHelperService.generateAssayContextItem(this.markupBuilder, assayContextItem)
        then: "We expect back an xml document"
        println(this.writer.toString())
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                      | attributeLabel   | valueLabel   | results
        "with attribute only"      | "attributeLabel" | null         | XmlTestSamples.ASSAY_CONTEXT_ITEM_WITH_ATTRIBUTE
        "with attribute and value" | "attributeLabel" | "valueLabel" | XmlTestSamples.ASSAY_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE
    }

    void "test Generate Assay Document #label"() {
        given:
        final AssayDocument assayDocument = new AssayDocument(documentType: documentType, documentContent: documentContent, documentName: documentName)
        when: "We attempt to generate an Assay document"
        this.assayExportHelperService.generateAssayDocument(this.markupBuilder, assayDocument, true)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                               | documentType   | documentContent | documentName   | results
        "Document Type, Document Content, no document Name" | "documentType" | "Content"       | ""             | XmlTestSamples.ASSAY_DOCUMENT_NO_DOCUMENT_NAME_UNIT
        "With Document Name"                                | "documentType" | "Content"       | "documentName" | XmlTestSamples.ASSAY_DOCUMENT_WITH_DOCUMENT_NAME_UNIT

    }

    void "create Attributes For AssayContextItem"() {
        given: "A DTO"
        final Map<String, String> results = [assayContextItemId: "1" , displayOrder:"0", qualifier: "< ", valueDisplay: "Display", valueNum: "5.0", valueMin: "6.0", valueMax: "7.0"]

        Element attributeElement = new Element(label: "attributeLabel")
        Element valueElement = new Element(label: "valueLabel")
        final AssayContextItem assayContextItem =
                    createAssayContextItem(attributeType: AttributeType.Fixed,
                            attributeElement: attributeElement,
                            valueElement: valueElement,
                            valueDisplay: "Display",
                            valueMax: new Float("7.0"),
                            valueMin: new Float("6.0"),
                            valueNum: new Float("5.0"),
                            modifiedBy: "Bard",
                            qualifier: "< ")

        when: "We pass in a assayContextItem we get an expected map"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForAssayContextItem(assayContextItem)
        then: "A map with the expected key/value pairs is generated"
        attributes == results
    }
}
