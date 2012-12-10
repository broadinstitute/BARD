package dataexport.registration

import bard.db.dictionary.Element
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import bard.db.registration.*

import static bard.db.enums.ReadyForExtraction.Complete
import static bard.db.enums.ReadyForExtraction.Ready
import static javax.servlet.http.HttpServletResponse.*

@Unroll
class AssayExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_ASSAY_EXPORT_SCHEMA = "classpath:assaySchema.xsd"

    AssayExportService assayExportService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    def grailsApplication
    Resource schemaResource

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
        schemaResource = grailsApplication.mainContext.getResource(BARD_ASSAY_EXPORT_SCHEMA)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Assay"
        when: "We call the assay service to update this assay"
        this.assayExportService.update(new Long(100000), 0, Complete.toString())

        then: "An exception is thrown, indicating that the project does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given an Assay with id #id and version #version"
        Assay.build(readyForExtraction: initialReadyForExtraction)

        when: "We call the assay service to update this assay"
        final BardHttpResponse bardHttpResponse = this.assayExportService.update(assayId, version, "Complete")

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Assay.get(assayId).readyForExtraction == expectedReadyForExtraction

        where:
        label                                           | expectedStatusCode     | expectedETag | assayId | version | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                          | SC_OK                  | 1            | 1       | 0       | Ready                     | Complete
        "Return CONFLICT and ETag 0"                    | SC_CONFLICT            | 0            | 1       | -1      | Ready                     | Ready
        "Return PRECONDITION_FAILED and ETag 0"         | SC_PRECONDITION_FAILED | 0            | 1       | 2       | Ready                     | Ready
        "Return OK and ETag 0, Already completed Assay" | SC_OK                  | 0            | 1       | 0       | Complete                  | Complete
    }

    void "test generate and validate AssayDocument"() {
        given: "Given an Assay "
        final AssayDocument assayDocument = AssayDocument.build()

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportService.generateAssayDocument(this.markupBuilder, assayDocument.id)

        then: "An XML is generated that conforms to the expected XML"
        Resource schemaResource = this.grailsApplication.mainContext.getResource(BARD_ASSAY_EXPORT_SCHEMA)
        XmlTestAssertions.validate(schemaResource, this.writer.toString())

    }

    void "test generate and validate Assay"() {

        given:
        Element element = Element.build()
        Assay assay = Assay.build()
        AssayContext assayContext = AssayContext.build(assay: assay)
        AssayContextItem assayContextItem = AssayContextItem.build(assayContext: assayContext, attributeElement: element)
        AssayDocument.build(assay: assay)

        Measure measure = Measure.build(assay: assay, resultType: element)
        AssayContextMeasure.build(measure: measure, assayContext: assayContext)


        when:
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_FULL_DOC, actualXml)


        XmlTestAssertions.validate(schemaResource, actualXml)
    }



    void "test generate and validate Assays #label"() {
        given: "Given there is at least one assay ready for extraction"
        Assay.build(readyForExtraction: Ready)

        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportService.generateAssays(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"

        def actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }
}
