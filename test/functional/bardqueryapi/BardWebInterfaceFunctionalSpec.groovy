package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseDecorator
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import javax.servlet.http.HttpServletResponse
import grails.plugin.remotecontrol.RemoteControl

@Unroll
class BardWebInterfaceFunctionalSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }

    def "Test ElasticSearch querying: #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([param1])
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the search action'
        HttpResponseDecorator serverResponse = http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        println serverResponse.data.toString()
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }

        where:
        label                  | param1
        "Find assay by id"     | "644"
        "Find assay by name"   | "Dose-response"
    }
    def "Test Auto Complete querying"() {
        given: "there is a service end point to do autocomplete on Assay names"

        StringBuilder sb = new StringBuilder()
        sb.append(baseUrl)
        sb.append("/bardWebInterface/autoCompleteAssayNames?term=Bro*")

        final String requestUrl = sb.toString()
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the auto complete action'
        HttpResponseDecorator serverResponse = http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the assay names'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        println serverResponse.data.toString()
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any { it.toString().contains("errorCode") }
    }

    /*
     * Chain together the test parameters to create a URL for the request
     */

    private String generateRequestUrl(List<String> paramList) {
        String allParams = paramList?.join("&")

        StringBuilder sb = new StringBuilder()

        sb.append(baseUrl)
        sb.append("/bardWebInterface/search?searchString=${allParams}")

        return sb.toString()
    }
}
