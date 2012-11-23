package dataexport.experiment

import grails.converters.XML
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.RESTClient
import org.custommonkey.xmlunit.XMLAssert
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.GET

import static groovyx.net.http.Method.PUT
import static groovyx.net.http.ContentType.TEXT
import bard.db.experiment.Experiment

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExperimentRestControllerFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    final String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/experiments"
    final String experimentBaseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/experiments"

    String experimentsMediaType = remote { ctx.grailsApplication.config.bard.data.export.experiments.xml }
    String experimentMediaType = remote { ctx.grailsApplication.config.bard.data.export.experiment.xml }

    final String apiKeyHeader = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.header }
    final String apiKeyHashed = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.hashed }


    def 'test GET Experiments success'() {
        /**
         * This is the code used in case the Remote Control feature is not used and both the functional test and Grails application
         * run in the SAME jvm.
         *
         * When the Grails application runs in a separate jvm, the RemoteControl object is populated properly BUT the ServletContext is not.
         *
         * For conclusion: the test has to choose ONE of the two options: single jvm (with the configuration below) or remote-contorl.
         *
         * ServletContext servletContext = ServletContextHolder.getServletContext()
         * ApplicationContext context = (ApplicationContext) servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
         * GrailsApplication grailsApp = context.getBean("grailsApplication")

         */

        given: "there is a service end point to get the the list of experiments with status of ready"
        RESTClient http = new RESTClient("${baseUrl}")
        when: 'We send an HTTP GET request for the list of experiments with status of ready'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = experimentsMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of the experiments'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XMLAssert.assertXpathEvaluatesTo("1", "count(//experiments)", responseData)
        XMLAssert.assertXpathEvaluatesTo("0", "count(//experiment)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//link)", responseData)
        XMLAssert.assertXpathEvaluatesTo('application/vnd.bard.cap+xml;type=experiment', "//link/@type", responseData)
        XMLAssert.assertXpathEvaluatesTo('related', "//link/@rel", responseData)

    }

    def 'test GET unauthorized'() {

        given: "there is a service end point to get the experiments"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for experiments without the API key'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = experimentsMediaType
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 401 (Unauthorized)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_UNAUTHORIZED
    }

    def 'test GET experiment fail with wrong Accept Header'() {
        given: "there is a service end point to get experiments"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for the experiments with the wrong mime type'
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

    void 'test experiment 404 not Found'() {

        given: "there is a service endpoint to get a experiment"
        final RESTClient http = new RESTClient("${baseUrl}/333891")

        when: 'We send an HTTP GET request, with the appropriate mime type, for a experiment with a non-existing id'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = experimentMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test experiment 400 not bad request'() {

        given: "there is a service endpoint to get a experiment"
        final RESTClient http = new RESTClient("${experimentBaseUrl}/10000")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = experimentsMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Experiment Success'() {
        given: "there is a service endpoint to get an experiment with id 1"
        RESTClient http = new RESTClient("${experimentBaseUrl}/1")

        when: 'We send an HTTP GET request for that experiment with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = experimentMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of that experiment'

        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').name == 'ETag'
        assert serverResponse.getFirstHeader('ETag').value == '0'
        final String responseData = serverResponse.data.readLines().join()
        XMLAssert.assertXpathEvaluatesTo("1", "count(//experiment)", responseData)
        XMLAssert.assertXpathEvaluatesTo("0", "count(//experiments)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//experimentContextItems)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//experimentContextItem)", responseData)
        XMLAssert.assertXpathEvaluatesTo("Number of points", "//experimentContextItem/attribute/@label", responseData)
        XMLAssert.assertXpathEvaluatesTo("application/vnd.bard.cap+xml;type=element", "//experimentContextItem/attribute/link/@type", responseData)
        XMLAssert.assertXpathEvaluatesTo("8", "count(//link)", responseData)
    }

    def 'test Update Experiment Success'(){
        given: "there is a service endpoint to update the Experiment with id 386"
        Experiment experiment = Experiment.get(1)
        experiment.readyForExtraction = 'Ready'
        RESTClient http = new RESTClient("${experimentBaseUrl}/1")

        when: 'We send an HTTP PUT request for that Experiment with a Status of Complete and an ETAG Header of 1'

        def serverResponse = http.request(PUT, TEXT) {
            headers.'If-Match' = '0'
            body = "Complete"
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an HTTP Status Code of OK, with the status of the Element now set to Complete'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').value == "1"

    }

}
