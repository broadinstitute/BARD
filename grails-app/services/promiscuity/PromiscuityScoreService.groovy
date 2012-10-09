package promiscuity

import bardqueryapi.QueryHelperService
import com.thoughtworks.xstream.XStream
import grails.converters.XML
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.commons.lang.time.StopWatch
import util.RestClientFactoryService

class PromiscuityScoreService {
    final XStream xstream
    RestClientFactoryService restClientFactoryService
    QueryHelperService queryHelperService

    PromiscuityScoreService() {
        //initialize xstream
        this.xstream = new XStream()
        //step up deserialization. Read XStream docs if you get confused here
        this.xstream.alias("compound", PromiscuityScore);
        this.xstream.alias("hscaf", Scaffold);
        this.xstream.addImplicitCollection(PromiscuityScore, "scaffolds");
    }
    /**
     *
     * @param fullURL -  something like 'http://bard.nih.gov/api/v4/plugins/badapple/prom/cid/233'
     * @return Map of objects
     *
     * Success would return [status: resp.status, message: 'Success', promiscuityScore: promiscuityScore]
     * Failure would return  eturn [status: HHTTP Error Code, message: "Error getting Promiscuity Score for ${fullURL}", promiscuityScore: null]
     */
    public Map findPromiscuityScoreForCID(final String fullURL) {
        def resp = null
        try {
            final RESTClient restClient = this.restClientFactoryService.createRestClient(fullURL)
            final StopWatch sw = queryHelperService.startStopWatch()

            resp = restClient.get(query: [expand: 'true'], contentType: XML,
                    headers: [Accept: 'application/xml'])
            final Map loggingMap = [url: "${fullURL}?expand=true"]
            this.queryHelperService.stopStopWatch(sw, loggingMap.toString())

            if (resp.status == 200) {
                final PromiscuityScore promiscuityScore = (PromiscuityScore) xstream.fromXML(resp.data);
                return [status: resp.status, message: 'Success', promiscuityScore: promiscuityScore]
            }
            return [status: resp.status, message: "Error getting Promiscuity Score for ${fullURL}", promiscuityScore: null]
        } catch (HttpResponseException ee) {

            return [status: ee.statusCode, message: "Error getting Promiscuity Score for ${fullURL}", promiscuityScore: null]
        }
    }

}