package dataexport.registration

import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.DocumentType
import bard.db.registration.*
import common.tests.XmlTestAssertions
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Specification
import spock.lang.Unroll

import static common.tests.XmlTestSamples.*
import bard.db.enums.ExpectedValueType

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, AssayContext, AssayContextItem, AssayContextMeasure, AssayDocument, Element, Measure])
@Mock([Assay, AssayContext, AssayContextItem, AssayContextMeasure, AssayDocument, Element, Measure])
@Unroll
class AssayExportHelperServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder
    LinkGenerator grailsLinkGenerator
    AssayExportHelperService assayExportHelperService = new AssayExportHelperService()

    Resource assaySchema = new FileSystemResource(new File("web-app/schemas/assaySchema.xsd"))

    void setup() {
        grailsLinkGenerator = Mock(LinkGenerator.class)
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(elementMediaType: "application/vnd.bard.cap+xml;type=element",
                    assaysMediaType: "application/vnd.bard.cap+xml;type=assays",
                    assayMediaType: "application/vnd.bard.cap+xml;type=assay",
                    assayDocMediaType: "application/vnd.bard.cap+xml;type=assayDoc",
                    resultTypeMediaType: "application/vnd.bard.cap+xml;type=resultType")
        this.assayExportHelperService.mediaTypesDTO = mediaTypesDTO
        this.assayExportHelperService.grailsLinkGenerator = grailsLinkGenerator

        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
        TestDataConfigurationHolder.reset()
    }

    void "test generate AssayContext #label"() {
        given:
        AssayContext assayContext = AssayContext.build(map)
        numItems.times { AssayContextItem.build(assayContext: assayContext) }
        numMeasureRefs.times { AssayContextMeasure.build(assayContext: assayContext) }

        when: "We attempt to generate a measure context in xml"
        this.assayExportHelperService.generateAssayContext(this.markupBuilder, assayContext)

        then: "A valid xml measure context is generated with the expected measure context id and name"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(results, actualXml)
        XmlTestAssertions.validate(assaySchema, actualXml)

        where:
        label                 | results                                     | numItems | numMeasureRefs | map
        "Minimal"             | ASSAY_CONTEXT_MINIMAL                       | 0        | 0              | [contextType: ContextType.UNCLASSIFIED]
        "with name"           | ASSAY_CONTEXT_WITH_CONTEXT_NAME             | 0        | 0              | [contextName: 'contextName',contextType: ContextType.UNCLASSIFIED]
        "with group"          | ASSAY_CONTEXT_WITH_CONTEXT_GROUP            | 0        | 0              | [contextType: ContextType.BIOLOGY]
        "with 1 contextItem"  | ASSAY_CONTEXT_WITH_ONE_CONTEXT_ITEM         | 1        | 0              | [contextType: ContextType.UNCLASSIFIED]
        "with 2 contextItems" | ASSAY_CONTEXT_WITH_TWO_CONTEXT_ITEMS        | 2        | 0              | [contextType: ContextType.UNCLASSIFIED]
        "with measureRefs"    | MINIMAL_ASSAY_CONTEXT_WITH_ONE_MEASURE_REF  | 0        | 1              | [contextType: ContextType.UNCLASSIFIED]
        "with measureRefs"    | MINIMAL_ASSAY_CONTEXT_WITH_TWO_MEASURE_REFS | 0        | 2              | [contextType: ContextType.UNCLASSIFIED]

    }

    void "test generate AssayContextItem #label"() {
        given:
        Element attributeElement = Element.build(label: attributeLabel, expectedValueType: ExpectedValueType.ELEMENT)
        Element valueElement = null
        if (valueLabel) {
            valueElement = new Element(label: valueLabel)
        }
        final AssayContextItem assayContextItem =
            AssayContextItem.build(attributeType: AttributeType.Fixed,
                    attributeElement: attributeElement,
                    valueElement: valueElement,
                    valueDisplay: "Display",
                    modifiedBy: "Bard",
                    )


        when: "We pass in a assay context item we get a good xml document"
        this.assayExportHelperService.generateAssayContextItem(this.markupBuilder, assayContextItem)

        then: "We expect back an xml document"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(results, actualXml)
        XmlTestAssertions.validate(assaySchema, actualXml)

        where:
        label                      | attributeLabel   | valueLabel   | results
        "with attribute and value" | "attributeLabel" | "valueLabel" | ASSAY_CONTEXT_ITEM_WITH_ATTRIBUTE_AND_VALUE
    }

    void "create Attributes For AssayContextItem"() {
        given:
        final Map<String, String> results = [displayOrder: "0", attributeType: "Fixed", valueDisplay: "Display"]

        Element attributeElement = new Element(label: "attributeLabel",expectedValueType: ExpectedValueType.ELEMENT)
        Element valueElement = new Element(label: "valueLabel")
        final AssayContextItem assayContextItem =
            AssayContextItem.build(attributeType: AttributeType.Fixed,
                    attributeElement: attributeElement,
                    valueElement: valueElement,
                    valueDisplay: "Display",
                    modifiedBy: "Bard")

        when: "We pass in a assayContextItem we get an expected map"
        Map<String, String> attributes = this.assayExportHelperService.createAttributesForContextItem(assayContextItem, assayContextItem.attributeType.name(), assayContextItem.id, 'assayContextItem', 0)

        then: "A map with the expected key/value pairs is generated"
        attributes == results
    }

    void "test generate Measure #label"() {
        given:
        Measure measure = Measure.build(mapClosure.call())
        numAssayContextMeasureRefs.times { AssayContextMeasure.build(measure: measure) }

        when: "We attempt to generate a measure in xml"
        this.assayExportHelperService.generateMeasure(this.markupBuilder, measure)
        then: "A valid xml measure is generated with the expected measure attributes, result type and entry unit"
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label                   | results                            | mapClosure                                                 | numAssayContextMeasureRefs
        "minimal"               | MEASURE_MINIMAL                    | { [:] }                                                    | 0
        "with parentMeasureRef" | MEASURE_WITH_PARENT_MEASURE_REF    | { [parentMeasure: Measure.build()] }                       | 0
        "with statsModifierRef" | MEASURE_WITH_STATS_MODIFIER_REF    | { [statsModifier: Element.build(label: "statsModifier")] } | 0
        "with entryUnitRef"     | MEASURE_WITH_ENTRY_UNIT_REF        | { [entryUnit: Element.build(label: "entryUnit")] }         | 0
        "with assayContextRefs" | MEASURE_WITH_ONE_ASSAY_CONTEXT_REF | { [:] }                                                    | 1
        "with assayContextRefs" | MEASURE_WITH_TWO_ASSAY_CONTEXT_REF | { [:] }                                                    | 2
    }

    void "test generate AssayDocument #label"() {
        given:
        AssayDocument assayDocument = AssayDocument.build(map)

        when: "We attempt to generate an Assay document"
        this.assayExportHelperService.generateDocument(this.grailsLinkGenerator,
                this.markupBuilder, assayDocument, 'assayDocument', 'assay',
                assayDocument.id, assayDocument.assay.id,
                this.assayExportHelperService.mediaTypesDTO.assayDocMediaType,
                this.assayExportHelperService.mediaTypesDTO.assayMediaType
        )

        then: "A valid xml document is generated and is similar to the expected document"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(results, actualXml)
        XmlTestAssertions.validate(assaySchema, actualXml)

        where:
        label          | results                     | map
        "minimal"      | ASSAY_DOCUMENT_MINIMAL      | [ documentType:DocumentType.DOCUMENT_TYPE_DESCRIPTION]
        "with content" | ASSAY_DOCUMENT_WITH_CONTENT | [documentContent: 'documentContent', documentType:DocumentType.DOCUMENT_TYPE_DESCRIPTION]
    }
}