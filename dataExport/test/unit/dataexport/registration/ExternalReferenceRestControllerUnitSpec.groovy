package dataexport.registration

import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import dataexport.cap.registration.ExternalReferenceRestController
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 *
 */
@Unroll
@TestFor(ExternalReferenceRestController)
@Mock([ExternalReference, ExternalSystem])
class ExternalReferenceRestControllerUnitSpec extends Specification {

    void setup() {
        controller.externalReferenceExportService = Mock(ExternalReferenceExportService.class)
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     *
     */
    void "test externalReferences #label"() {
        when: "We send an HTTP GET Request to the externalReferences action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.externalReferences()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label               | mimeType                                               | expectedResults
        "Expects 400 Error" | "application/vnd.bard.cap+xml;type=dictionary"         | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"    | "application/vnd.bard.cap+xml;type=externalReferences" | HttpServletResponse.SC_OK
    }

    /**
     *
     */
    void "test externalSystems #label"() {
        when: "We send an HTTP GET Request to the externalSystems action"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.externalSystems()
        then: "We expect a response with the given status code"
        expectedResults == response.status
        where:
        label               | mimeType                                            | expectedResults
        "Expects 400 Error" | "application/vnd.bard.cap+xml;type=dictionary"      | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"    | "application/vnd.bard.cap+xml;type=externalSystems" | HttpServletResponse.SC_OK
    }
    /**
     *
     */
    void "test  external reference fail #label #id"() {
        when: "We send an HTTP GET request for a specific externalReference"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id
        controller.externalReference()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                              | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                                     | HttpServletResponse.SC_BAD_REQUEST
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=externalReference" | HttpServletResponse.SC_BAD_REQUEST
    }

    /**
     *
     */
    void "test  external system fail #label #id"() {
        when: "We send an HTTP GET request for a specific externalSystem"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id
        controller.externalSystem()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                           | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                                  | HttpServletResponse.SC_BAD_REQUEST
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=externalSystem" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     *
     */
    void "test  external reference #label"() {
        when: "We send an HTTP GET request for a specific external Reference"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.externalReference()
        then:
        expectedResults == response.status
        where:
        label                        | id  | mimeType                                              | expectedResults
        "Expects 200 OK status code" | "5" | "application/vnd.bard.cap+xml;type=externalReference" | HttpServletResponse.SC_OK
    }

    /**
     *
     */
    void "test  external system #label"() {
        when: "We send an HTTP GET request for a specific external system"
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.externalSystem()
        then:
        expectedResults == response.status
        where:
        label                        | id  | mimeType                                           | expectedResults
        "Expects 200 OK status code" | "5" | "application/vnd.bard.cap+xml;type=externalSystem" | HttpServletResponse.SC_OK
    }
}
