import org.custommonkey.xmlunit.Validator
import spock.lang.Specification
import spock.lang.Unroll
import wslite.rest.ContentType
import wslite.rest.RESTClient
import wslite.rest.Response

import javax.servlet.http.HttpServletResponse

@Unroll
class ResourcesUpAndValidSpec extends Specification {

    String contentTypeBasis = "application/vnd.bard.cap+xml;type="

    String serverUrl = System.getProperty('server.url') ?: "http://localhost:8080"
    String baseUrl = "${serverUrl}/dataExport/api"
    String apiKeyHeader = System.getProperty('apikey.header') ?: "APIKEY"
    String apiKeyHashed = System.getProperty('apikey.hashed') ?: "changeMe"
    String externalSchemaLocation = System.getProperty('schema.url') ?: "${serverUrl}/dataExport/schemas/BARDexportSchema.xsd"



    def "Test top level resource #label"() {
        given: "A service end point to get to the root elements"
        final String topLevelUrl = [baseUrl, resourcePath].findAll().join('/')
        when: "We send an HTTP GET request for the root elements"
        final Response topLevelResponse = getServerResponse(topLevelUrl, "${contentTypeBasis}${topLevelType}")
        final String topLevelResponseString = topLevelResponse.contentAsString;

        then: "We expect an XML representation of the root elements"
        assert topLevelResponse.statusCode == HttpServletResponse.SC_OK

        and: "topLevelResponse should be valid"
        validateXml(topLevelResponseString)

        and: 'get and validate firstLevelResource'
        if (firstLevelSubResourceClosure) {
            final String firstLevelResponseString = extractAndValidate(topLevelResponseString, firstLevelSubResourceClosure)

            // if there's a second level closure try and get and retrieve that, this would be documents,
            if (secondLevelSubResourceClosure && firstLevelResponseString) {
                extractAndValidate(firstLevelResponseString, secondLevelSubResourceClosure)
            }
        }

        where:
        label                | resourcePath         | topLevelType         | firstLevelSubResourceClosure                                                                             | secondLevelSubResourceClosure
        "Get api root"       | null                 | 'bardexport'         | null                                                                                                     | null
        "externalSystems"    | 'externalSystems'    | 'externalSystems'    | { xml -> def link = xml.externalSystem[0].link.find { it.@rel == 'self' }; [link.@href, link.@type] }    | null
        "externalReferences" | 'externalReferences' | 'externalReferences' | { xml -> def link = xml.externalReference[0].link.find { it.@rel == 'self' }; [link.@href, link.@type] } | null
        "assays"             | 'assays'             | 'assays'             | { xml -> def link = xml.link?.find { it.@rel == 'related' }; [link?.@href, link?.@type] }                | { xml -> def link = xml.link?.find { it.@rel == 'item' && it.@type.toString().contains('assayDoc') }; [link?.@href, link?.@type] }
        "projects"           | 'projects'           | 'projects'           | { xml -> def link = xml.link?.find { it.@rel == 'item' }; [link?.@href, link?.@type] }                   | { xml -> def link = xml.link?.find { it.@rel == 'item' && it.@type.toString().contains('projectDoc') }; [link?.@href, link?.@type] }
        "experiments"        | 'experiments'        | 'experiments'        | { xml -> def link = xml.link.find { it.@rel == 'related' }; [link.@href, link.@type] }                   | null
        "dictionary"         | 'dictionary'         | 'dictionary'         | null                                                                                                     | null
    }

    private String extractAndValidate(String xmlAsString, Closure subResourceClosure) {
        def xmlSlurper = new XmlSlurper().parseText(xmlAsString)
        def (String subResourceUrl, String subResourceType) = subResourceClosure.call(xmlSlurper)

        if (subResourceUrl && subResourceType) {
            final Response subResourceResponse = getServerResponse(subResourceUrl, subResourceType)
            final String subResourceResponseAsString = subResourceResponse.contentAsString

            //'expect response code of 200'
            assert subResourceResponse.statusCode == HttpServletResponse.SC_OK

            //'the subResourceResponse should be valid'
            validateXml(subResourceResponseAsString)
            return subResourceResponseAsString
        }
        return null
    }


    private void validateXml(String responseAsString) {
        println(responseAsString)
        assert responseAsString
        Validator v = new Validator(responseAsString)
        v.useXMLSchema(true)
        v.setJAXP12SchemaSource(externalSchemaLocation)
        assert true == v.isValid()
    }



    Response getServerResponse(String requestUrl, String mimeType) {
        println("requestUrl: $requestUrl")
        println("mimeType: $mimeType")
        getServerResponse(requestUrl, mimeType, true)
    }

    Response getServerResponse(String requestUrl, String mimeType, boolean sslTrustAllCerts) {

        RESTClient http = new RESTClient(requestUrl)
        http.httpClient.sslTrustAllCerts = sslTrustAllCerts
        Map getParamMap = [accept: ContentType.XML,
                headers: [Accept: mimeType, (apiKeyHeader): apiKeyHashed],
                connectTimeout: 5000,
                readTimeout: 30000,
                followRedirects: false,
                useCaches: false,
                sslTrustAllCerts: true]
        println("getParamMap ${getParamMap}")
        Response serverResponse = http.get(getParamMap)

        return serverResponse
    }

}
