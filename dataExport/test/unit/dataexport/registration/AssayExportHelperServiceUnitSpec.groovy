package dataexport.registration

import bard.db.dictionary.Element
import bard.db.dictionary.Unit
import common.tests.XmlTestAssertions
import grails.test.mixin.Mock
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import bard.db.registration.*
import common.tests.XmlTestSamples

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock([Measure, MeasureContextItem, MeasureContext, Assay, AssayDocument])
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
        this.assayExportHelperService.generateMeasure(this.markupBuilder, measure)
        then: "A valid xml measure is generated with the expected measure attributes, result type and entry unit"
        println  this.writer.toString()
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                           | measure                                                                                                                                          | results
        "Measure with Parent No Child Elements"         | new Measure(measureContext: new MeasureContext(contextName: "label"))                                                                            | XmlTestSamples.MEASURE_1_UNIT
        "Measure with Parent,ResultType and Entry Unit" | new Measure(measureContext: new MeasureContext(contextName: "label"), element: new Element(label: "resultType"), entryUnit: new Unit(unit: "%")) | XmlTestSamples.MEASURE_2_UNIT

    }


    void "test create Attributes For Measure #label"() {
        when: "We pass in a measure to Create a attributes for a Measure"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForMeasure(measure)
        then: "A map with the expected key/value pairs is generated"
        results == attributes
        where:
        label                             | measure                                                                                                                                                         | results
        "Measure With Measure Context "   | new Measure(measureContext: new MeasureContext(contextName: "label"), element: new Element(label: "label"), entryUnit: new Unit(unit: "%"), modifiedBy: "Bard") | [measureContextRef: "label"]
        "Measure with No Measure Context" | new Measure(element: new Element(label: "label"), entryUnit: new Unit(unit: "%"), modifiedBy: "Bard")                                                           | [:]

    }

    void "test Generate Measure Context #label"() {
        given:
        MeasureContext measureContext = new MeasureContext(contextName: contextName)
        when: "We attempt to generate a measure context in xml"
        this.assayExportHelperService.generateMeasureContext(this.markupBuilder, measureContext)
        then: "A valid xml measure context is generated with the expected measure context id and name"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label              | contextName | results
        "Measure context " | "TestName1" | XmlTestSamples.MEASURE_CONTEXT_1_UNIT

    }

    void "test generate Measure Context Item #label"() {
        given: "A DTO"
        MeasureContextItem parentGroup = new MeasureContextItem()
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

        MeasureContext measureContext = new MeasureContext(contextName: "measureContext")
        final String qualifier = "<"
        final MeasureContextItem measureContextItem =
            new MeasureContextItem(attributeElement: attributeElement,
                    attributeType: attributeType,
                    measureContext: measureContext,
                    valueElement: valueElement,
                    parentGroup: parentGroup,
                    valueDisplay: valueDisplay,
                    valueMax: valueMax,
                    valueMin: valueMin,
                    valueNum: valueNum,
                    modifiedBy: modifiedBy,
                    qualifier: qualifier)

        when: "We pass in a measurce context item to create Measure Context Item xml document"
        this.assayExportHelperService.generateMeasureContextItem(this.markupBuilder, measureContextItem)
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

    void "create Attributes For MeasureContextItem"() {
        given: "A DTO"
        final Map<String, String> results = [measureContextRef: "measureContext", qualifier: "<", valueDisplay: "Display", valueNum: "5.0", valueMin: "6.0", valueMax: "7.0"]
        MeasureContextItem parentGroup = new MeasureContextItem()
        AttributeType attributeType = AttributeType.Fixed
        String valueDisplay = "Display"
        Float valueNum = new Float("5.0")
        Float valueMin = new Float("6.0")
        Float valueMax = new Float("7.0")
        String modifiedBy = "Bard"
        Element attributeElement = new Element(label: "attributeLabel")
        Element valueElement = new Element(label: "valueLabel")
        MeasureContext measureContext = new MeasureContext(contextName: "measureContext")
        final String qualifier = "<"
        final MeasureContextItem measureContextItem =
            new MeasureContextItem(attributeElement: attributeElement,
                    attributeType: attributeType,
                    measureContext: measureContext,
                    valueElement: valueElement,
                    parentGroup: parentGroup,
                    valueDisplay: valueDisplay,
                    valueMax: valueMax,
                    valueMin: valueMin,
                    valueNum: valueNum,
                    modifiedBy: modifiedBy,
                    qualifier: qualifier)
        when: "We pass in a dto to create Measure Context Item Attributes"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForMeasureContextItem(measureContextItem)
        then: "A map with the expected key/value pairs is generated"
        attributes == results
    }
}
