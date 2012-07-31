package dataexport.experiment

import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

@Unroll
class ResultExportServiceIntegrationSpec extends IntegrationSpec {
    ResultExportService resultExportService

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Result"
        when: "We call the result service to update this result"
        this.resultExportService.update(new Long(100000), 0, "Complete")

        then: "An exception is thrown, indicating that the result does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given a Result with id #id and version #version"
        when: "We call the result service to update this result"
        final BardHttpResponse bardHttpResponse = this.resultExportService.update(resultId, version,status)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode

        where:
        label                                            | expectedStatusCode                         | expectedETag | resultId      | version | status
        "Return OK and ETag 1"                           | HttpServletResponse.SC_OK                  | new Long(1)  | new Long(533) | 0       | "Complete"
        "Return CONFLICT and ETag 0"                     | HttpServletResponse.SC_CONFLICT            | new Long(0)  | new Long(533) | -1      | "Complete"
        "Return PRECONDITION_FAILED and ETag 0"          | HttpServletResponse.SC_PRECONDITION_FAILED | new Long(0)  | new Long(533) | 2       | "Complete"
        "Return OK and ETag 0, Already completed Result" | HttpServletResponse.SC_OK                  | new Long(0)  | new Long(532) | 0       | "Complete"
    }


}
