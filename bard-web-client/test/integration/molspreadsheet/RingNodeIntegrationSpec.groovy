package molspreadsheet

import grails.plugin.spock.IntegrationSpec
import bard.core.rest.spring.SunburstCacheService

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 4/2/13
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
class RingNodeIntegrationSpec  extends IntegrationSpec {
    SunburstCacheService sunburstCacheService
    RingManagerService ringManagerService

    void "test sunburst machinery"(){
        given:
        final List<String> targets = ["P03230","Q92698","Q3TKT4","P49615","Q9H3R0","P10520","P37840","P21399",
                "P55789","Q99814","P27540","Q9Y6A5","Q16665","P38532","O75030","P69722","P69720","P69721","P69723","Q14145","Q16236","Q04206","Q61009"]
        LinkedHashMap<String, Integer> accumulatedTargets = ringManagerService.accumulateAccessionNumbers( targets )
        when:
        List<String> accumulatedMaps = []
        accumulatedTargets.each{k,v->
            accumulatedMaps<<sunburstCacheService.getTargetClassInfo(k)}
        println  accumulatedMaps.toString()
        then:
        assert 1==1
    }


}
