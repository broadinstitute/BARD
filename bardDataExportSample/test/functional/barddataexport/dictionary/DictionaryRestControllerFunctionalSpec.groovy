package barddataexport.dictionary

import grails.converters.XML
import groovyx.net.http.RESTClient
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.GET
import common.tests.XmlTestAssertions

import common.tests.XmlTestSamples
import grails.plugin.remotecontrol.RemoteControl

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class DictionaryRestControllerFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    final String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/dictionary"
    String dictionaryAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.dictionary.xml }

    def 'test GET dictionary success'() {
        given: "there is a service end point to get the dictionary"
        RESTClient http = new RESTClient(baseUrl)
        when: 'We send an HTTP GET request for the dictionary'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = dictionaryAcceptContentType
        }
        then: 'We expect an XML representation of the entire dictionary'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.DICTIONARY, responseData)
    }

    def 'test GET dictionary fail with wrong Accept Header'() {
        given: "there is a service end point to get the dictionary"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for a dictionary with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = "some bogus"
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    void 'test result Type 404 not Found'() {

        given: "there is a service endpoint to get a result type"
        final RESTClient http = new RESTClient("${baseUrl}/resultType/1")

        when: 'We send an HTTP GET request, with the appropriate mime type, for a result type with a non-existing id of 1'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=resultType'
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test result Type 400 not bad request'() {

        given: "there is a service endpoint to get a result type"
        final RESTClient http = new RESTClient("${baseUrl}/resultType/1")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=element'
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Result Type Success'() {
        given: "there is a service endpoint to get result type with id 341"
        RESTClient http = new RESTClient("${baseUrl}/resultType/341")

        when: 'We send an HTTP GET request for that result type with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=resultType'
        }
        then: 'We expect an XML representation of that Result Type'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.RESULT_TYPE, responseData)
    }


    void 'test Stage 404 not Found'() {

        given: "there is a service endpoint to get a stage"
        final RESTClient http = new RESTClient("${baseUrl}/stage/1")

        when: 'We send an HTTP GET request, with the appropriate mime type, for a stage with a non-existing id of 1'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=stage'
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test Stage 400 not bad request'() {

        given: "there is a service endpoint to get a stage"
        final RESTClient http = new RESTClient("${baseUrl}/stage/1")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=element'
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Stage Success'() {
        given: "there is a service endpoint to get Stage with id 341"
        RESTClient http = new RESTClient("${baseUrl}/stage/341")

        when: 'We send an HTTP GET request for that Stage with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=stage'
        }
        then: 'We expect an XML representation of that Stage'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.STAGE, responseData)
    }

    void 'test Element 404 not Found'() {

        given: "there is a service endpoint to get an Element"
        final RESTClient http = new RESTClient("${baseUrl}/element/1")

        when: 'We send an HTTP GET request, with the appropriate mime type, for an Element with a non-existing id of 1'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=element'
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test Element 400 not bad request'() {

        given: "there is a service endpoint to get an Element"
        final RESTClient http = new RESTClient("${baseUrl}/element/1")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=stage'
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Element Success'() {
        given: "there is a service endpoint to get an Element with id 386"
        RESTClient http = new RESTClient("${baseUrl}/element/386")

        when: 'We send an HTTP GET request for that Element with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = 'application/vnd.bard.cap+xml;type=element'
        }
        then: 'We expect an XML representation of that Element'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.ELEMENT, responseData)
    }



}
