package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseDecorator
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import javax.servlet.http.HttpServletResponse
import grails.plugin.remotecontrol.RemoteControl
import org.junit.Ignore

/*
 * Superseded by BardWebInterfaceFunctionalSpec
 */
@Ignore
@Unroll
class QueryApiSpec extends Specification {
    RemoteControl remote = new RemoteControl()
    String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }

    def "Test REST Query API: #label"() {
        given: "there is a service end point to get the root elements"
        String requestUrl = generateRequestUrl(queryType, paramType, paramValue)
        RESTClient http = new RESTClient(requestUrl)

        when: 'We send an HTTP GET request for the root elements'
        HttpResponseDecorator serverResponse = http.get(requestContentType: JSON)

        then: 'We expect a JSON representation of the root elements'
        assert serverResponse.statusLine.statusCode == HttpServletResponse.SC_OK
        println serverResponse.data.toString()
        assert serverResponse.data.size() > 0
        assert !serverResponse.data.any{ it.toString().contains("errorCode") }

        where:
        label                         | queryType        | paramType                | paramValue
        "Get probes for project"      | "searchProject"  | "projectType=Probes"     | "project=1772"
        "Get assays for project"      | "searchProject"  | "projectType=Assays"     | "project=1772"
        "Get projects by AID"         | "searchProject"  | ""                       | "project=1772"
        "Get targets for assay"       | "searchAssay"    | "assayType=Targets"      | "assay=1772"
        "Get publications for assay"  | "searchAssay"    | "assayType=Publications" | "assay=1772"
        "Get compounds for assay"     | "searchAssay"    | "assayType=Compounds"    | "assay=1772"
        "Get assays by AID"           | "searchAssay"    | "assayType=Assay"        | "assay=1772"
        "Get compound by CID"         | "searchCompound" | "compoundType=CID"       | "compound=888706"
        "Get compound by SID"         | "searchCompound" | "compoundType=SID"       | "compound=57578335"
        "Search targets by accession" | "searchTarget"   | "targetType=Accession"   | "target=P01112"
        "Search targets by GeneId"    | "searchTarget"   | "targetType=GeneId"      | "target=3265"
    }

    /*
     * Chain together the test parameters to create a URL for the request
     */
    private String generateRequestUrl(String queryType, String paramType, String paramValue){
        StringBuilder sb = new StringBuilder()

        sb.append(baseUrl)
        sb.append("/queryApi/")
        sb.append("${queryType}?")
        if(paramType.length() > 0) {                // because you can leave the type blank for projects
            sb.append("${paramType}&")
            if(queryType.contains("Compound")) {    // compound query expects a return format --  use default for now
                sb.append("compoundFormat=DEFAULT&")
            }
        }
        sb.append(paramValue)

        return sb.toString()
    }
}
