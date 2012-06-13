package barddataexport.dictionary

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
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

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/15/12
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class RootRestControllerFunctionalSpec extends Specification {
    GrailsApplication grailsApplication
    // TODO should be configured in Config.groovy, seems like grails.server.url which can be set per environment
    final String baseUrl = "http://localhost:${System.getProperty('server.port') ?: '8080'}/bardDataExportSample/api" // your app URL


    void setup() {
        ServletContext servletContext = ServletContextHolder.getServletContext()
        ApplicationContext context = (ApplicationContext) servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
        this.grailsApplication = context.getBean("grailsApplication")
    }



    def 'test GET Root Elements'() {
        given: "there is a service end point to get the root elements"
        RESTClient http = new RESTClient(baseUrl)
        when: 'We send an HTTP GET request for the root elements'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = grailsApplication.config.bard.data.export.bardexport.xml
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
            response.failure = { resp ->
                resp
            }
        }
        then: 'We expect a status code of 400 (Bad Request)'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_BAD_REQUEST
    }
}
