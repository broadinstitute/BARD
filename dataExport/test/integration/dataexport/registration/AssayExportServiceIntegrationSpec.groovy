package dataexport.registration

import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.DocumentType
import bard.db.registration.*
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import static bard.db.enums.ReadyForExtraction.COMPLETE
import static bard.db.enums.ReadyForExtraction.READY
import static javax.servlet.http.HttpServletResponse.*

@Unroll
class AssayExportServiceIntegrationSpec extends IntegrationSpec {
    AssayExportService assayExportService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/assaySchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

        TestDataConfigurationHolder.reset()
        resetSequenceUtil = new ResetSequenceUtil(dataSource)
        ['ASSAY_ID_SEQ',
                'ASSAY_CONTEXT_ID_SEQ',
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

    void "test update Not Found Status"() {
        given: "Given a non-existing Assay"
        when: "We call the assay service to update this assay"
        this.assayExportService.update(new Long(100000), 0, COMPLETE)

        then: "An exception is thrown, indicating that the project does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given an Assay with id #id and version #version"
        Assay.build(readyForExtraction: initialReadyForExtraction, capPermissionService:null)

        when: "We call the assay service to update this assay"
        final BardHttpResponse bardHttpResponse = this.assayExportService.update(assayId, version, COMPLETE)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Assay.get(assayId).readyForExtraction == expectedReadyForExtraction

        where:
        label                                           | expectedStatusCode     | expectedETag | assayId | version | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                          | SC_OK                  | 1            | 1       | 0       | READY                     | COMPLETE
        "Return CONFLICT and ETag 0"                    | SC_CONFLICT            | 0            | 1       | -1      | READY                     | READY
        "Return PRECONDITION_FAILED and ETag 0"         | SC_PRECONDITION_FAILED | 0            | 1       | 2       | READY                     | READY
        "Return OK and ETag 0, Already completed Assay" | SC_OK                  | 0            | 1       | 0       | COMPLETE                  | COMPLETE
    }

    void "test generate and validate AssayDocument"() {
        given: "Given an Assay "
        final AssayDocument assayDocument = AssayDocument.build(documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION)

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportService.generateAssayDocument(this.markupBuilder, assayDocument.id)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.validate(schemaResource, this.writer.toString())
    }

    void "test generate and validate Assay"() {

        given:
        Element element = Element.build()
        Assay assay = Assay.build(capPermissionService:null)
        AssayContext assayContext = AssayContext.build(assay: assay, contextType: ContextType.UNCLASSIFIED)
        AssayContextItem.build(assayContext: assayContext, attributeElement: element)
        AssayDocument.build(assay: assay)
        when:
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(XmlTestSamples.ASSAY_FULL_DOC, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }



    void "test generate and validate Assays #label"() {
        given: "Given there is at least one assay ready for extraction"
        Assay.build(readyForExtraction: READY,capPermissionService:null)

        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportService.generateAssays(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"

        def actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }
}
