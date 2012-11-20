package dataexport.registration


import bard.db.dictionary.Element
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.test.mixin.Mock
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
@Mock([Measure, AssayContextItem, AssayContext, Assay, AssayDocument])
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


    void "test generate Measure #label"() {
        when: "We attempt to generate a measure in xml"
        this.assayExportHelperService.generateMeasure(this.markupBuilder, measure)
        then: "A valid xml measure is generated with the expected measure attributes, result type and entry unit"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                           | measure                                                                                                                         | results
        "Measure with Parent No Child Elements"         | new Measure(id:'1')                                                                          | XmlTestSamples.MEASURE_1_UNIT
        "Measure with Parent,ResultType and Entry Unit" | new Measure(assayContext: new AssayContext(contextName: "label"), resultType: new Element(label: "resultType"), entryUnit: "%") | XmlTestSamples.MEASURE_2_UNIT

    }


    void "test create Attributes For Measure #label"() {
        when: "We pass in a measure to Create a attributes for a Measure"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForMeasure(measure)
        then: "A map with the expected key/value pairs is generated"
        results == attributes
        where:
        label                             | measure                                                                                                                                     | results
        "Measure With Measure Context "   | new Measure(assayContext: new AssayContext(contextName: "label"), element: new Element(label: "label"), entryUnit: "%", modifiedBy: "Bard") | [assayContextRef: "label"]
        "Measure with No Measure Context" | new Measure(element: new Element(label: "label"), entryUnit: "%", modifiedBy: "Bard")                                                       | [:]

    }

    void "test Generate Measure Context #label"() {
        given:
        AssayContext assayContext = new AssayContext(contextName: contextName)
        when: "We attempt to generate a measure context in xml"
        this.assayExportHelperService.generateAssayContext(this.markupBuilder, assayContext)
        then: "A valid xml measure context is generated with the expected measure context id and name"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label              | contextName | results
        "Measure context " | "TestName1" | XmlTestSamples.MEASURE_CONTEXT_1_UNIT

    }

    void "test generate Measure Context Item #label"() {
        given: "A DTO"
        AttributeType attributeType = AttributeType.Fixed
        String valueDisplay = "Display"
        Float valueNum = new Float("5.0")
        Float valueMin = new Float("6.0")
        Float valueMax = new Float("7.0")
        String modifiedBy = "Bard"
        Element attributeElement = new Element(label: attributeLabel)
        Element valueElement = null
        if (valueLabel) {
            valueElement = new Element(label: valueLabel)
        }

        AssayContext assayContext = new AssayContext(contextName: "assayContext")
        final String qualifier = "<"
        final AssayContextItem assayContextItem =
            new AssayContextItem(attributeElement: attributeElement,
                    attributeType: attributeType,
                    assayContext: assayContext,
                    valueElement: valueElement,
                    valueDisplay: valueDisplay,
                    valueMax: valueMax,
                    valueMin: valueMin,
                    valueNum: valueNum,
                    modifiedBy: modifiedBy,
                    qualifier: qualifier)

        when: "We pass in a measurce context item to create Measure Context Item xml document"
        this.assayExportHelperService.generateAssayContextItem(this.markupBuilder, assayContextItem)
        then: "We expect back an xml document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                           | attributeLabel   | valueLabel   | results
        "Measure context Item with attribute and value" | "attributeLabel" | "valueLabel" | XmlTestSamples.MEASURE_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE_UNIT
        "Measure context Item with attribute only"      | "attributeLabel" | null         | XmlTestSamples.MEASURE_CONTEXT_ITEM_WITH_ATTRIBUTE_UNIT

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
        final Map<String, String> results = [assayContextRef: "assayContext", qualifier: "<", valueDisplay: "Display", valueNum: "5.0", valueMin: "6.0", valueMax: "7.0"]
        AttributeType attributeType = AttributeType.Fixed
        String valueDisplay = "Display"
        Float valueNum = new Float("5.0")
        Float valueMin = new Float("6.0")
        Float valueMax = new Float("7.0")
        String modifiedBy = "Bard"
        Element attributeElement = new Element(label: "attributeLabel")
        Element valueElement = new Element(label: "valueLabel")
        AssayContext assayContext = new AssayContext(contextName: "assayContext")
        final String qualifier = "<"
        final AssayContextItem assayContextItem =
            new AssayContextItem(attributeElement: attributeElement,
                    attributeType: attributeType,
                    assayContext: assayContext,
                    valueElement: valueElement,
                    valueDisplay: valueDisplay,
                    valueMax: valueMax,
                    valueMin: valueMin,
                    valueNum: valueNum,
                    modifiedBy: modifiedBy,
                    qualifier: qualifier)
        when: "We pass in a dto to create Measure Context Item Attributes"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForAssayContextItem(assayContextItem)
        then: "A map with the expected key/value pairs is generated"
        attributes == results
    }
}
