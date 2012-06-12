package barddataexport.dictionary

import barddataexport.cap.dictionary.DictionaryRestController
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 *
 */
@Unroll
@TestFor(DictionaryRestController)
class DictionaryRestControllerUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
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
    void "test  result type #label #id"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        when:
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.resultType()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                       | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                              | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=resultType" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=resultType" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     * DictionaryRestControllerUnitSpec#stage
     */
    void "test  stage #label #id"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        when:
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.stage()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                  | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                         | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=stage" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=stage" | HttpServletResponse.SC_BAD_REQUEST
    }
    /**
     * DictionaryRestControllerUnitSpec#element
     */
    void "test  element #label #id"() {
        given:
        controller.dictionaryExportService = Mock(DictionaryExportService.class)
        when:
        request.method = 'GET'
        controller.request.addHeader(HttpHeaders.ACCEPT, mimeType)
        controller.params.id = id

        controller.element()
        then:
        expectedResults == response.status
        where:
        label                     | id   | mimeType                                    | expectedResults
        "Expects 400 Bad request" | "2"  | "bogus.mime.type"                           | HttpServletResponse.SC_BAD_REQUEST
        "Expects 200 OK"          | "5"  | "application/vnd.bard.cap+xml;type=element" | HttpServletResponse.SC_OK
        "Expects 400 Bad request" | null | "application/vnd.bard.cap+xml;type=element" | HttpServletResponse.SC_BAD_REQUEST
    }
}
