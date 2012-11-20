package dataexport.experiment

import bard.db.project.Project
import grails.converters.XML
import grails.plugin.remotecontrol.RemoteControl
import groovyx.net.http.RESTClient
import org.custommonkey.xmlunit.XMLAssert
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.PUT

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectRestControllerFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    final String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL } + "/api/projects"
    String projectsMediaType = remote { ctx.grailsApplication.config.bard.data.export.projects.xml }
    String projectMediaType = remote { ctx.grailsApplication.config.bard.data.export.project.xml }

    final String apiKeyHeader = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.header }
    final String apiKeyHashed = remote { ctx.grailsApplication.config.dataexport.externalapplication.apiKey.hashed }

    def 'test GET projects success'() {
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

        given: "there is a service end point to get the the list of projects with status of ready"
        RESTClient http = new RESTClient(baseUrl)
        when: 'We send an HTTP GET request for the list of projects with status of ready'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = projectsMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of the projects'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        XMLAssert.assertXpathEvaluatesTo("1", "count(//projects)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//project)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//projectStep)", responseData)
        XMLAssert.assertXpathEvaluatesTo("6", "count(//link)", responseData)
    }

    def 'test GET unauthorized'() {

        given: "there is a service end point to get the projects"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for projects without the API key'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = projectsMediaType
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 401 (Unauthorized)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_UNAUTHORIZED
    }

    def 'test GET project fail with wrong Accept Header'() {
        given: "there is a service end point to get projects"
        RESTClient http = new RESTClient(baseUrl)

        when: 'We send an HTTP GET request for the projects with the wrong mime type'
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

    void 'test project 404 not Found'() {

        given: "there is a service endpoint to get a project"
        final RESTClient http = new RESTClient("${baseUrl}/333891")

        when: 'We send an HTTP GET request, with the appropriate mime type, for a project with a non-existing id'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = projectMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 404 (Not Found)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_NOT_FOUND
    }

    void 'test experiments 400 not bad request'() {

        given: "there is a service endpoint to get a project"
        final RESTClient http = new RESTClient("${baseUrl}/10000")

        when: 'We send an HTTP GET request, with the wrong mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = projectsMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }

    def 'test GET Project Success'() {
        given: "there is a service endpoint to get an project with id 1"
        RESTClient http = new RESTClient("${baseUrl}/1")

        when: 'We send an HTTP GET request for that project with the appropriate mime type'
        def serverResponse = http.request(GET, XML) {
            headers.Accept = projectMediaType
            headers."${apiKeyHeader}" = apiKeyHashed
        }
        then: 'We expect an XML representation of that project'

        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        assert serverResponse.getFirstHeader('ETag')
        assert serverResponse.getFirstHeader('ETag').name == 'ETag'
        assert serverResponse.getFirstHeader('ETag').value == '0'
        final String responseData = serverResponse.data.readLines().join()
        XMLAssert.assertXpathEvaluatesTo("1", "count(//project)", responseData)
        XMLAssert.assertXpathEvaluatesTo("1", "count(//projectSteps)", responseData)
        XMLAssert.assertXpathEvaluatesTo("2", "count(//projectStep)", responseData)
        XMLAssert.assertXpathEvaluatesTo("4", "count(//link)", responseData)
    }

    def 'test Update Project Success'() {
        given: "there is a service endpoint to update the Project with id 1"
        Project project = Project.get(1)
        project.readyForExtraction = 'Ready'
        RESTClient http = new RESTClient("${baseUrl}/1")

        when: 'We send an HTTP PUT request for that Project with a Status of Complete and an IF_Match header of 0'

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
