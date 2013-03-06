package dataexport.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Result
import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert
import spock.lang.Unroll

import static bard.db.enums.ReadyForExtraction.COMPLETE
import static bard.db.enums.ReadyForExtraction.READY
import static javax.servlet.http.HttpServletResponse.*

@Unroll
class ResultExportServiceIntegrationSpec extends IntegrationSpec {
    ResultExportService resultExportService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }


    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Result"
        when: "We call the result service to update this result"
        this.resultExportService.update(new Long(100000), 0, ReadyForExtraction.COMPLETE)

        then: "An exception is thrown, indicating that the result does not exist"
        thrown(NotFoundException)
    }

    void "test update Result, Exception READY_FOR_EXTRACTION value #status is unknown"() {
        given: "Given a Result with id #id and version #version"
        when: "We call the result service to update this result"
        this.resultExportService.update(resultId, version, status)

        then: "An exception is thrown, indicating that the status value of #status is unknown"
        thrown(Exception)
        where:
        resultId      | version | status
        new Long(532) | 0       | "InComplete"
    }

    void "test Generate Result"() {
        given: "Given a Result with id #id and version #version"
        Result result = Result.build(readyForExtraction: ReadyForExtraction.READY)

        when: "We call the result service to generate this result"
        this.resultExportService.generateResult(this.markupBuilder, result.id)

        then: "A result is generated"
        XMLAssert.assertXpathEvaluatesTo("1", "count(//result)", this.writer.toString());
    }

    void "test update #label"() {
        given: "Given a Result with id #id and version #version"
        Result result = Result.build(readyForExtraction: initialReadyForExtraction)

        when: "We call the result service to update this result"
        final BardHttpResponse bardHttpResponse = this.resultExportService.update(result.id, version, ReadyForExtraction.COMPLETE)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert result.readyForExtraction == expectedReadyForExtraction
        where:
        label                                            | expectedStatusCode     | expectedETag | resultId | version | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                           | SC_OK                  | 1            | 533      | 0       | READY                     | COMPLETE
        "Return CONFLICT and ETag 0"                     | SC_CONFLICT            | 0            | 533      | -1      | READY                     | READY
        "Return PRECONDITION_FAILED and ETag 0"          | SC_PRECONDITION_FAILED | 0            | 533      | 2       | READY                     | READY
        "Return OK and ETag 0, Already completed Result" | SC_OK                  | 0            | 532      | 0       | COMPLETE                  | COMPLETE
    }
}