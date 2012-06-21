package barddataexport.registration

import barddataexport.cap.registration.AssayRestController
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 *
 */
@Unroll
@TestFor(AssayRestController)
class AssayRestControllerUnitSpec extends Specification {

    void setup() {
        controller.assayExportService = Mock(AssayExportService.class)
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     *
     */
    void "test assays #label"() {
        when: "We send an HTTP GET Request to the assays action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.assays()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label               | mimeType                                       | expectedResults
        "Expects 400 Error" | "application/vnd.bard.cap+xml;type=dictionary" | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"    | "application/vnd.bard.cap+xml;type=assays"     | HttpServletResponse.SC_OK
    }
    /**
     *
     */
    void "test  assay #label #id"() {
        when: "We send an HTTP GET request for a specific assay"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.assay()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                  | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                         | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=assay" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=assay" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     *
     */
    void "test  assayDocument #label #id"() {
        when:
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.assayDocument()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                     | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                            | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=assayDoc" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=assayDoc" | HttpServletResponse.SC_BAD_REQUEST
    }
}
