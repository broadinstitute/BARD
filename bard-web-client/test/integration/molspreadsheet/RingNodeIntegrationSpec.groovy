package molspreadsheet

import grails.plugin.spock.IntegrationSpec
import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.compounds.TargetClassInfo

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


    void "test sunburst machinery with the target that has no Panther classes"(){
        given:
        final List<String> targets = ["P03230","Q92698","Q9Y6A5","P69722","P69721","P69723","P10520"]
        LinkedHashMap<String, Integer> accumulatedTargets = ringManagerService.accumulateAccessionNumbers( targets )

        when:
        List<List<TargetClassInfo>> accumulatedMaps = []
        accumulatedTargets.each{k,v->
            List<String> hierarchyDescription = sunburstCacheService.getTargetClassInfo(k)
            if (hierarchyDescription != null){
                accumulatedMaps<<sunburstCacheService.getTargetClassInfo(k)
            }
        }

    then:
        accumulatedMaps.size() == 0
    }


    void "test sunburst machinery with targets that all have associated Panther classes"(){
        given:
        final List<String> targets = ["Q3TKT4","P49615","Q9H3R0","P37840","P21399",
                "P55789","Q99814","P27540","Q16665","P38532","O75030","Q14145","Q16236","Q04206","Q61009"]
        LinkedHashMap<String, Integer> accumulatedTargets = ringManagerService.accumulateAccessionNumbers( targets )

        when:
        List<List<TargetClassInfo>> accumulatedMaps = []
        accumulatedTargets.each{ k,v ->
            List<String> hierarchyDescription = sunburstCacheService.getTargetClassInfo(k)
            if (hierarchyDescription != null){
                accumulatedMaps<<sunburstCacheService.getTargetClassInfo(k)
            }
        }

            then:
        accumulatedMaps.size() == targets.size()
        accumulatedMaps.get(0).size() > 0
    }




    void "test building a tree from a few targets"(){
        given:
        final List<String> targets = ["Q14145","Q16236","Q61009"]
        LinkedHashMap<String, Integer> accumulatedTargets = ringManagerService.accumulateAccessionNumbers( targets )

        when:
        List<List<TargetClassInfo>> accumulatedMaps = []
        accumulatedTargets.each{ k,v ->
            List<String> hierarchyDescription = sunburstCacheService.getTargetClassInfo(k)
            if (hierarchyDescription != null){
                accumulatedMaps<<sunburstCacheService.getTargetClassInfo(k)
            }
        }
        RingNode root = ringManagerService.ringNodeFactory( accumulatedMaps.flatten() as List<TargetClassInfo> )
        then:
        root.toString()
    }




}
