package promiscuity

import bardqueryapi.QueryServiceWrapper
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

@Unroll
class PromiscuityServiceIntegrationSpec extends IntegrationSpec {
    PromiscuityScoreService promiscuityScoreService
    QueryServiceWrapper queryServiceWrapper

    @Before
    void setup() {

    }

    @After
    void tearDown() {

    }

    void "test findPromiscuityScoreForCID"() {
        given: "That we have a full URL to the promiscuity Service"
        Long cid = new Long(38911)
        final String fullURL = "${queryServiceWrapper.promiscuityScoreURL}${cid}"
        when: "We call the Promiscuity Service with the URL"
        Map promiscuityScoreMap = this.promiscuityScoreService.findPromiscuityScoreForCID(fullURL)
        then:
        assert promiscuityScoreMap
        assert promiscuityScoreMap.status == 200
        PromiscuityScore promiscuityScore = promiscuityScoreMap.promiscuityScore
        assert promiscuityScore
        assert promiscuityScore.cid == cid
        assert promiscuityScore.scaffolds

    }

    void "test findPromiscuityScoreForCID with Non-existing CID"() {
        given: "That we have a full URL to the promiscuity Service"
        Long cid = new Long(-1)
        final String fullURL = "${queryServiceWrapper.promiscuityScoreURL}${cid}"
        when: "We call the Promiscuity Service with the URL"
        Map promiscuityScoreMap = this.promiscuityScoreService.findPromiscuityScoreForCID(fullURL)
        then:
        assert promiscuityScoreMap
        assert promiscuityScoreMap.status == 404
        assert !promiscuityScoreMap.promiscuityScore
        assert promiscuityScoreMap.message
    }
}
