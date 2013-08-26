package dataexport.registration

import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.DocumentType
import bard.db.enums.ExpectedValueType
import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import bard.db.registration.*
import org.springframework.core.io.FileSystemResource

@Unroll
class AssayExportHelperServiceIntegrationSpec extends IntegrationSpec {
    AssayExportHelperService assayExportHelperService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/assaySchema.xsd"))
    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
        resetSequenceUtil = new ResetSequenceUtil(dataSource)

        TestDataConfigurationHolder.reset()

        ['ASSAY_ID_SEQ',
                'ASSAY_CONTEXT_ID_SEQ',
                'ASSAY_CONTEXT_ITEM_ID_SEQ',
                'ASSAY_CONTEXT_MEASURE_ID_SEQ',
                'ASSAY_DOCUMENT_ID_SEQ',
                'ELEMENT_ID_SEQ',
                'MEASURE_ID_SEQ'].each {
            this.resetSequenceUtil.resetSequence(it)
        }
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate AssayContext with contextItems"() {
        given: "Given an Assay Id"
        Assay assay = Assay.build(capPermissionService:null)
        AssayContext assayContext = AssayContext.buildWithoutSave(assay: assay, contextName: 'Context for IC50', contextType: ContextType.UNCLASSIFIED)
        final Element freeTextAttribute = Element.build(label: 'software', expectedValueType: ExpectedValueType.FREE_TEXT)
        AssayContextItem.build(assayContext: assayContext, attributeElement: freeTextAttribute, attributeType: AttributeType.Fixed, valueDisplay: 'Assay Explorer')
        AssayContextItem.build(assayContext: assayContext, attributeElement: Element.build(label: 'a label'), attributeType: AttributeType.Fixed)

        when: "A service call is made to generate measure contexts for that Assay"
        this.assayExportHelperService.generateAssayContexts(this.markupBuilder, assay.assayContexts)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_CONTEXTS, this.writer.toString())
    }

    void "test generate AssayContext with measureRefs"() {
        given: "Given an Assay Id"
        Assay assay = Assay.build(capPermissionService:null)
        AssayContext assayContext = AssayContext.buildWithoutSave(assay: assay, contextName: 'Context for IC50', contextType: ContextType.UNCLASSIFIED)
        Measure measure = Measure.build(assay: assay, resultType: Element.build(label: 'IC50'))
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(measure: measure, assayContext: assayContext)

        when: "A service call is made to generate measure contexts for that Assay"
        this.assayExportHelperService.generateAssayContexts(this.markupBuilder, assay.assayContexts)

        then: "An XML is generated that conforms to the expected XML"

        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_CONTEXT_WITH_MEASURES, this.writer.toString())
    }

    void "test generate Full Assay"() {
        given:
        Element element = Element.build()
        Assay assay = Assay.build(capPermissionService:null)
        AssayContext assayContext = AssayContext.build(assay: assay, contextType: ContextType.UNCLASSIFIED)
        AssayContextItem assayContextItem = AssayContextItem.build(assayContext: assayContext, attributeElement: element)
        AssayDocument.build(assay: assay)

        Measure measure = Measure.build(assay: assay, resultType: element)
        AssayContextMeasure.build(measure: measure, assayContext: assayContext)


        when:
        this.assayExportHelperService.generateAssay(this.markupBuilder, assay)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(XmlTestSamples.ASSAY_FULL_DOC, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }

    void "test generate Assays readyForExtraction"() {
        given: "Given there is at least one assay ready for extraction"
        Assay.build(readyForExtraction: ReadyForExtraction.READY,capPermissionService:null)

        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportHelperService.generateAssays(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"

        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS, this.writer.toString())
    }

    void "test generate AssayDocument"() {
        given:
        AssayDocument assayDocument = AssayDocument.build(documentName: 'a doc', documentType: DocumentType.DOCUMENT_TYPE_PROTOCOL, documentContent: 'content')

        when:
        this.assayExportHelperService.generateDocument(this.markupBuilder, assayDocument)

        then:
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_DOCUMENT, this.writer.toString())


    }

}
