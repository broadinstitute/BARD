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

    def "Test REST Query API: #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl([param1, param2, param3])
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the root elements'
        HttpResponseDecorator serverResponse = http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        println serverResponse.data.toString()
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any{ it.toString().contains("errorCode") }

        where:
        label                                        | param1       | param2       | param3
        "Find compounds for assay"                   | "assay=604"  | null         | null
        "Find compounds for assay w/ offset and max" | "assay=644"  | "offset=100" | "max=50"
        "Show compound by CID"                       | "cid=571349" | null         | null
    }

    /*
     * Chain together the test parameters to create a URL for the request
     */
    private String generateRequestUrl(List<String> paramList){
        String allParams = paramList?.join("&")

        StringBuilder sb = new StringBuilder()

        sb.append(baseUrl)
        sb.append("/bardWebInterface/?${allParams}")

        return sb.toString()
    }
}
