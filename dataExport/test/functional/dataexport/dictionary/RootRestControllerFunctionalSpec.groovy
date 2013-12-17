package dataexport.dictionary

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.converters.XML
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.GET

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class RootRestControllerFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    final String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api"
    String rootAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.bardexport.xml }
    final String apiKeyHeader = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.header }
    final String apiKeyHashed = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.hashed }

    def 'test GET Root Elements'() {
        given: "there is a service end point to get the root elements"
        RESTClient http = new RESTClient(baseUrl)
        when: 'We send an HTTP GET request for the root elements'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = rootAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResultsWithOverrideAttributes(XmlTestSamples.BARD_DATA_EXPORT, responseData)
    }

    def 'test GET root fail with wrong Accept Header'() {
        given: "there is a service end point to get the root elements"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for the root element with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = "some bogus"
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }
}
