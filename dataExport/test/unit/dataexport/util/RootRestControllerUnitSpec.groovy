package dataexport.util

import dataexport.cap.util.RootRestController
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 *
 */
@Unroll
@TestFor(RootRestController)
class RootRestControllerUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     *
     */
    void "test  root #label"() {
        given: "There is a service end point at the Url"
        controller.rootService = Mock(RootService.class)
        when: "We make an HTTP GET Request to the api method"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.api()
        then: "We expect the status to match the expected status"
        expectedResults == response.status
        where:
        label               | mimeType                                       | expectedResults
        "Expects 400 Error" | "bogus.mime.type"                              | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"    | "application/vnd.bard.cap+xml;type=bardexport" | HttpServletResponse.SC_OK
    }
    /**
     *
     */
    void "test index #label fail"() {
        given: "There is a service end point at the Url"
        controller.rootService = Mock(RootService.class)
        when: "We make an HTTP GET Request to the index method"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.index()
        then: "We expect the status to match the expected status"
        expectedResults == response.status
        where:
        label                              | mimeType                                       | expectedResults
        "Expects 405 Error"                | "bogus.mime.type"                              | HttpServletResponse.SC_METHOD_NOT_ALLOWED
        "Expects  405 Error with mimetype" | "application/vnd.bard.cap+xml;type=bardexport" | HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }
}
