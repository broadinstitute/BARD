package barddataexport.dictionary

import grails.converters.XML
import groovyx.net.http.RESTClient
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
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
class DictionaryControllerFunctionalSpec extends Specification {
    GrailsApplication grailsApplication
    // TODO should be configured in Config.groovy, seems like grails.server.url which can be set per environment
    final String baseUrl = "http://localhost:${System.getProperty('server.port') ?: '8080'}/bardDataExportSample/api/dictionary" // your app URL


    void setup() {
        ServletContext servletContext = ServletContextHolder.getServletContext()
        ApplicationContext context = (ApplicationContext) servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
        this.grailsApplication = context.getBean("grailsApplication")
    }



    def 'test GET dictionary success'() {
        given: "there is a service end point to get the dictionary"
        RESTClient http = new RESTClient(baseUrl)
        when: 'We send an HTTP GET request for the dictionary'
        def serverResponse = http.request(GET, XML) {
            headers.'Accept' = grailsApplication.config.bard.data.export.dictionary.xml
        }
        then: 'We expect an XML representation of the entire dictionary'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        final String responseData = serverResponse.data.readLines().join()
        assertResults(DICTIONARY, responseData)
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
        assertResults(RESULT_TYPE, responseData)
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
        assertResults(STAGE, responseData)
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
        assertResults(ELEMENT, responseData)
    }
    void assertResults(final String expectedResults, final String generatedResults) {
        XMLUnit.setIgnoreWhitespace(true)
        Diff xmlDiff = new Diff(expectedResults, generatedResults)
        assert true == xmlDiff.similar()
    }

    static String STAGE = '''
    <stage stageId='341' stageStatus='Published'>
      <stageName>construct variant assay</stageName>
    </stage>
'''
    static String RESULT_TYPE = '''
    <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
      <resultTypeName>IC50</resultTypeName>
    </resultType>
'''
    static String ELEMENT = '''
    <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    '''
    static String DICTIONARY = '''
<dictionary>
  <elements>
    <element elementId='386' readyForExtraction='Ready' elementStatus='Published'>
      <label>uM</label>
      <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/386' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='366' readyForExtraction='Ready' elementStatus='Published'>
      <label>concentration</label>
      <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/366' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='123' readyForExtraction='Ready' elementStatus='Published'>
      <label>unit of measurement</label>
      <description>It is the inite magnitude of a physical quantity or of time. It has a quantity and a unit associated with it.</description>
      <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/123' type='application/vnd.bard.cap+xml;type=element' />
    </element>
    <element elementId='341' readyForExtraction='Ready' elementStatus='Published' unit='uM'>
      <label>IC50</label>
      <link rel='edit' href='http://localhost:8080/bardDataExportSample/api/dictionary/element/341' type='application/vnd.bard.cap+xml;type=element' />
    </element>
  </elements>
  <elementHierarchies>
    <elementHierarchy parentElementId='341' childElementId='366'>
      <relationshipType>derives from</relationshipType>
    </elementHierarchy>
  </elementHierarchies>
  <resultTypes>
    <resultType resultTypeId='341' baseUnit='uM' resultTypeStatus='Published'>
      <resultTypeName>IC50</resultTypeName>
    </resultType>
  </resultTypes>
  <stages>
    <stage stageId='341' stageStatus='Published'>
      <stageName>construct variant assay</stageName>
    </stage>
  </stages>
  <biologyDescriptors>
    <biologyDescriptor descriptorId='4' elementId='366'>
      <elementStatus>Published</elementStatus>
      <label>macromolecule description</label>
      <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
    </biologyDescriptor>
  </biologyDescriptors>
  <assayDescriptors>
    <assayDescriptor descriptorId='287' elementId='386'>
      <elementStatus>Published</elementStatus>
      <label>assay phase</label>
      <description>It refers to whether all the assay components are in solution or some are in solid phase, which determines their ability to scatter light.</description>
    </assayDescriptor>
  </assayDescriptors>
  <instanceDescriptors>
    <instanceDescriptor descriptorId='12' elementId='123'>
      <elementStatus>Published</elementStatus>
      <label>macromolecule description</label>
      <description>A long name for a gene or protein from a trusted international source (e.g., Entrez, UniProt).</description>
    </instanceDescriptor>
  </instanceDescriptors>
  <laboratories>
    <laboratory laboratoryId='341' laboratoryStatus='Published'>
      <laboratoryName>LABORATORY</laboratoryName>
      <description>Singular root to ensure tree viewers work</description>
    </laboratory>
  </laboratories>
  <units>
    <unit unitId='123' unit='UNIT'>
      <description>Singular root to ensure tree viewers work</description>
    </unit>
    <unit unitId='366' unit='concentration' />
    <unit unitId='386' parentUnitId='366' unit='uM' />
  </units>
  <unitConversions>
    <unitConversion fromUnit='uM' toUnit='concentration' multiplier='2.5' offset='2'>
      <formula>2*2</formula>
    </unitConversion>
  </unitConversions>
</dictionary>
'''
}
