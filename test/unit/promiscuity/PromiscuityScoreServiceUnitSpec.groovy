package promiscuity

import bardqueryapi.QueryHelperService
import com.thoughtworks.xstream.XStream
import grails.test.mixin.TestFor
import groovyx.net.http.RESTClient
import org.apache.commons.lang.time.StopWatch
import spock.lang.Specification
import spock.lang.Unroll
import util.RestClientFactoryService

@Unroll
@TestFor(PromiscuityScoreService)
class PromiscuityScoreServiceUnitSpec extends Specification {
    XStream xstream
    RestClientFactoryService restClientFactoryService
    QueryHelperService queryHelperService

    void setup() {
        this.xstream = Mock(XStream)
        this.restClientFactoryService = Mock(RestClientFactoryService)
        this.queryHelperService = Mock(QueryHelperService)
//        this.service.xstream =  Mock(XStream)
        this.service.restClientFactoryService = this.restClientFactoryService
        this.service.queryHelperService = this.queryHelperService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test findPromiscuityScoreForCID statusCode=200"() {
        given:
        final RESTClient restClient = Mock(RESTClient)
        final StopWatch sw = Mock(StopWatch)
        final PromiscuityScore promiscuityScore = new PromiscuityScore(cid: 38911)

        when: "findPromiscuityScoreForCID with a valid url"
        Map promiscuityScoreMap = service.findPromiscuityScoreForCID("http://mock.com")
        then: ""
        this.restClientFactoryService.createRestClient(_) >> {restClient}
        this.queryHelperService.startStopWatch() >> {sw}
        this.queryHelperService.stopStopWatch(_, _) >> {}
        restClient.get(_) >> {[status: 200, data: promiscuityXML]}
        xstream.fromXML(_) >> {
            promiscuityScore
        }
        assert promiscuityScoreMap.status == 200
        assert promiscuityScoreMap.promiscuityScore.cid == promiscuityScore.cid

    }

    void "test findPromiscuityScoreForCID StatusCode=404"() {
        given:
        final RESTClient restClient = Mock(RESTClient)
        final StopWatch sw = Mock(StopWatch)

        when: "findPromiscuityScoreForCID with a valid url"
        Map promiscuityScoreMap = service.findPromiscuityScoreForCID("http://mock.com")
        then: ""
        this.restClientFactoryService.createRestClient(_) >> {restClient}
        this.queryHelperService.startStopWatch() >> {sw}
        this.queryHelperService.stopStopWatch(_, _) >> {}
        restClient.get(_) >> {[status: 404]}

        assert promiscuityScoreMap.status == 404
        assert !promiscuityScoreMap.promiscuityScore

    }

    final static String promiscuityXML = '''
<compound>
  <cid>38911</cid>
  <hscaf>
    <scafid>43</scafid>
    <pScore>1291.0</pScore>
    <scafsmi>C1CCCCC1</scafsmi>
    <cTested>12476</cTested>
    <cActive>7765</cActive>
    <aTested>692</aTested>
    <aActive>622</aActive>
    <sTested>5309103</sTested>
    <sActive>27185</sActive>
    <inDrug>true</inDrug>
  </hscaf>
 </compound>
  '''

}

