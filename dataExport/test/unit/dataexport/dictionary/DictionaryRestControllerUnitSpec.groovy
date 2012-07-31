package dataexport.dictionary

import bard.db.dictionary.Element
import bard.db.dictionary.ResultType
import bard.db.dictionary.Stage
import dataexport.cap.dictionary.DictionaryRestController
import dataexport.cap.registration.UpdateStatusHelper
import dataexport.registration.BardHttpResponse
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse
import exceptions.NotFoundException

/**
 *
 */
@Unroll
@TestFor(DictionaryRestController)
@Mock([Element, Stage, ResultType])
class DictionaryRestControllerUnitSpec extends Specification {

    void setup() {
        controller.metaClass.mixin(UpdateStatusHelper)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update element, 200 Status"() {
        given: "An Element with an id of 1, a version of 0 and a status of Complete"
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'PUT'
        controller.request.addHeader(HttpHeaders.IF_MATCH, "0")
        controller.params.id = "1"
        controller.request.content = "Complete"
        controller.dictionaryExportService.update(_, _, _) >> {
            new BardHttpResponse(ETag: 1, httpResponseCode: 200)
        }
        when: "We do an HTTP PUT on the DictionaryController, the updateElement method is called which increments the version and applies the status to the element"
        controller.updateElement(new Long(1))

        then: "We expect the status on the element to change to Complete and an HTTP response code of 200"
        response.status == 200
    }

    void "test update element, #label"() {
        given: "An Element with an id of #id, a version of #version and a status of #readyForExtraction"
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'PUT'
        controller.request.addHeader(IF_MATCH, version)
        //controller.params.id = id
        controller.request.content = readyForExtraction

        0 * controller.dictionaryExportService.update(_, _, _) >> {
            new BardHttpResponse(ETag: 1, httpResponseCode: 200)
        }
        when: "We do an HTTP PUT on the DictionaryController, the updateElement method is called which increments the version and applies the status to the element"
        controller.updateElement(id)

        then: "We expect the status on the element to change to Complete and an HTTP response code of 200"
        response.status == expectedHTTPStatusCode

        where:
        label                         | id   | version | readyForExtraction | expectedHTTPStatusCode | IF_MATCH
        "No id supplied"              | null | 0       | "Complete"         | 400                    | HttpHeaders.IF_MATCH
        "No If_Match header supplied" | 1    | 0       | "Complete"         | 400                    | "None"
    }
    void "test update element throw NotFoundException"() {
        given: "An Element with an id of '1', a version of '0' and a status of 'Complete'"
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'PUT'
        controller.request.addHeader(HttpHeaders.IF_MATCH, "0")
        controller.request.content = "Complete"

       1 * controller.dictionaryExportService.update(_, _, _) >> {
          throw new NotFoundException("message")
        }
        when: "We do an HTTP PUT on the DictionaryController, the updateElement method is called which increments the version and applies the status to the element"
        controller.updateElement(2)

        then: "We expect the status on the element to change to Complete and an HTTP response code of 200"
        response.status == HttpServletResponse.SC_NOT_FOUND

    }
    void "test update element throw Exception - Internal Server Error"() {
        given: "An Element with an id of '1', a version of '0' and a status of 'Complete'"
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'PUT'
        controller.request.addHeader(HttpHeaders.IF_MATCH, "0")
        controller.request.content = "Complete"

        1 * controller.dictionaryExportService.update(_, _, _) >> {
            throw new Exception("message")
        }
        when: "We do an HTTP PUT on the DictionaryController, the updateElement method is called which increments the version and applies the status to the element"
        controller.updateElement(2)

        then: "We expect the status on the element to change to Complete and an HTTP response code of 200"
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR

    }
    void "test update element, 400 Status, because status is not understood by server"() {
        given: "An Element with an id of 1, a version of 0 and a status of 'InComplete'"
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'PUT'
        controller.request.addHeader(HttpHeaders.IF_MATCH, "0")
        controller.params.id = "1"
        controller.request.content = "InComplete"
        0 * controller.dictionaryExportService.update(_, _, _) >> {
            new BardHttpResponse(ETag: 1, httpResponseCode: 200)
        }
        when: "We do an HTTP PUT on the DictionaryController, the updateElement method is called which fails because there is status 'InComplete'"
        controller.updateElement(new Long(1))

        then: "We expect  HTTP response code of 400"
        response.status == 400
    }

    void "test update element, 400 Status"() {
        given: "An Element with an id of 1, a version of 0 and no status"
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'PUT'
        controller.request.addHeader(HttpHeaders.IF_MATCH, "0")
        controller.params.id = "1"

        0 * controller.dictionaryExportService.update(_, _, _) >> {
            new BardHttpResponse(ETag: 1, httpResponseCode: 200)
        }
        when: "We do an HTTP PUT on the DictionaryController, the updateElement method is called which fails because there is no status"
        controller.updateElement(new Long(1))

        then: "We expect  HTTP response code of 400"
        response.status == 400
    }
    /**
     * DictionaryRestControllerUnitSpec#dictionary
     */
    void "test  dictionary #label"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        when:
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)

        controller.dictionary()
        then:
        expectedResults == response.status
        where:
        label               | mimeType                                       | expectedResults
        "Expects 400 Error" | "bogus.mime.type"                              | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"    | "application/vnd.bard.cap+xml;type=dictionary" | HttpServletResponse.SC_OK
    }

    /**
     * DictionaryRestControllerUnitSpec#resultType
     */
    void "test  result type #label"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        when:
        controller.resultType()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                       | expectedResults
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=resultType" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                              | HttpServletResponse.SC_BAD_REQUEST
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=resultType" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     * DictionaryRestControllerUnitSpec#stage
     */
    void "test  stage #label #id"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        when:
        controller.stage()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                  | expectedResults
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=stage" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                         | HttpServletResponse.SC_BAD_REQUEST
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=stage" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     * DictionaryRestControllerUnitSpec#element
     */
    void "test  element #label #id"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        params.id = id
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        when:
        controller.element()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                    | expectedResults
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=element" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                           | HttpServletResponse.SC_BAD_REQUEST
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=element" | HttpServletResponse.SC_BAD_REQUEST
    }
}
