package dataexport.registration

//import org.springframework.context.ApplicationContext


import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.converters.XML
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.GET
import org.custommonkey.xmlunit.XMLAssert
import bard.db.experiment.Project

import static groovyx.net.http.Method.PUT
import static groovyx.net.http.ContentType.TEXT

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayRestControllerFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    final String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/assays"
    final String assayDocBaseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/assayDocument"
    String assaysAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.assays.xml }
    String assayAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.assay.xml }
    String assayDocumentAcceptContentType = remote { ctx.grailsApplication.config.bard.data.export.assay.doc.xml }

    final String apiKeyHeader = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.header }
    final String apiKeyHashed = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.hashed }

    def 'test GET assays success'() {
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

        given: "there is a service end point to get the the list of assays"
        RESTClient http = new RESTClient(baseUrl)
        when: 'We send an HTTP GET request for the list of assays'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = assaysAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of the assays ready for extraction'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS_FROM_SERVER, responseData)
    }

    def 'test GET unauthorized'() {

        given: "there is a service end point to get the assays"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for assays without the API key'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = assaysAcceptContentType
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 401 (Unauthorized)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_UNAUTHORIZED
    }

    def 'test GET assays fail with wrong Accept Header'() {
        given: "there is a service end point to get the assays"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for the assays with the wrong mime type'
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

    void 'test assay 404 not Found'() {

        given: "there is a service endpoint to get an assay"
        final RESTClient http = new RESTClient("${baseUrl}/333891")

        when: 'We send an HTTP GET request, with the appropriate mime type, for an assay with a non-existing id'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = assayAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test assay 400 not bad request'() {

        given: "there is a service endpoint to get an assay"
        final RESTClient http = new RESTClient("${baseUrl}/10000")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = assaysAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Assay Success'() {
        given: "there is a service endpoint to get an assay with id 1"
        RESTClient http = new RESTClient("${baseUrl}/1")

        when: 'We send an HTTP GET request for that assay with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = assayAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of that assay'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').name == 'ETag'
        assert serverResponse.getFirstHeader('ETag').value == '0'
        final String responseData = serverResponse.data.readLines().join()
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measureContexts)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measureContext)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measures)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measure)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//measureContextItems)", responseData)
        XMLAssert.assertXpathEvaluatesTo("3", "count(//measureContextItem)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//assayDocument)", responseData)
        XMLAssert.assertXpathEvaluatesTo("11", "count(//link)", responseData)
    }


    void 'test Assay Document 404 not Found'() {

        given: "there is a service endpoint to get an assay document"
        final RESTClient http = new RESTClient("${assayDocBaseUrl}/1000000")

        when: 'We send an HTTP GET request, with the appropriate mime type, for an assay doc with a non-existing id'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = assayDocumentAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test assay doc 400 not bad request'() {

        given: "there is a service endpoint to get an assay document"
        final RESTClient http = new RESTClient("${assayDocBaseUrl}/10000000")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = assaysAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Assay document Success'() {
        given: "there is a service endpoint to get Assay document with id 1"
        RESTClient http = new RESTClient("${assayDocBaseUrl}/1")

        when: 'We send an HTTP GET request for that Assay doc with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = assayDocumentAcceptContentType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of that AssayDocument'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').name == 'ETag'
        assert serverResponse.getFirstHeader('ETag').value == '0'
        final String responseData = serverResponse.data.readLines().join()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_DOCUMENT_SERVER, responseData)
    }

    def 'test Update Assay Success'() {
        given: "there is a service endpoint to update the assay with id 1"
        Project project = Project.get(1)
        project.readyForExtraction = 'Ready'
        RESTClient http = new RESTClient("${baseUrl}/1")

        when: 'We send an HTTP PUT request for that Assay with a Status of Complete and an IF_Match header of 0'

        def serverResponse = http.request(PUT, TEXT) {
            headers.'If-Match' = '0'
            body = "Complete"
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an HTTP Status Code of OK, with the status of the Assay now set to Complete'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').value == "1"
    }
}
