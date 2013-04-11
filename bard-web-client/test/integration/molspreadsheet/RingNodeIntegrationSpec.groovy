package molspreadsheet

import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.compounds.CompoundSummary
import bard.core.rest.spring.compounds.TargetClassInfo
import grails.plugin.spock.IntegrationSpec
import bard.core.rest.spring.util.RingNode

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
    CompoundRestService compoundRestService


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
       accumulatedMaps.size() == targets.size()
       accumulatedMaps.flatten().size() == 0
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
        accumulatedMaps.flatten().size() > 0
    }




    void "test building a tree from a few targets withou actives/inactives to consider"(){
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
        root.toString().contains("nucleic acid binding")
        root.toString().find(/nucleic acid[^\n]+/).find(/size\":\d/).find(/\d/) == '2'
        root.toString().find(/hydrolase[^\n]+/).find(/children/) == 'children'
        root.toString().find(/receptor[^\n]+/).find(/size\":\d/).find(/\d/) == '1'
    }






    void "test building a tree from a few targets but with actives/inactives to consider"(){
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
        RingNode root = ringManagerService.ringNodeFactory( accumulatedMaps.flatten() as List<TargetClassInfo>,
                                                            ['hits':["Q14145","Q16236"],'misses':["Q61009"]])
        then:
        root.toString().contains("nucleic acid binding")
        root.toString().find(/nucleic acid[^\n]+/).find(/size\":\d/).find(/\d/) == '2'
        root.toString().find(/hydrolase[^\n]+/).find(/children/) == 'children'
        root.toString().find(/receptor[^\n]+/).find(/size\":\d/).find(/\d/) == '1'
    }





    /**
     * For now use dummy routine to pull back real data from v12 of the API
     */
    void "test working with Out of date compound summary information"(){
        given:
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompoundFROM_PREVIOUS_VERSION(0L)

        when:
        LinkedHashMap activeInactiveData = ringManagerService.retrieveActiveInactiveDataFromCompound(compoundSummary)

        then:
        activeInactiveData ["hits"].size ()   > 0
        activeInactiveData ["misses"].size ()   > 0
    }


    /**
     * For now use dummy routine to pull back real data from v12 of the API
     */
    void "test working with Current compound summary information"(){
        given:
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompound(2382353L)

        when:
        LinkedHashMap activeInactiveData = ringManagerService.retrieveActiveInactiveDataFromCompound(compoundSummary)

        then:
        activeInactiveData ["hits"].size ()   == 0
        activeInactiveData ["misses"].size ()   == 0
    }



    void "test class data exists for this compound where the answer is true"(){
        given:
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompoundFROM_PREVIOUS_VERSION(0L)

        when:
        Boolean classDataExists = ringManagerService.classDataExistsForThisCompound(compoundSummary)

        then:
        classDataExists
    }


    /**
     * For now use dummy routine to pull back real data from v12 of the API
     */
    void "test class data exists for this compound where the answer is false"(){
        given:
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompound(2382353L)

        when:
        Boolean classDataExists = ringManagerService.classDataExistsForThisCompound(compoundSummary)

        then:
        classDataExists == false
    }






    void "test sunburst with real (though old) classes, but with no active/inactive distinctions"(){
        given:
        CompoundSummary compoundSummary = compoundRestService.getSummaryForCompoundFROM_PREVIOUS_VERSION(0L)
        LinkedHashMap activeInactiveData = ringManagerService.retrieveActiveInactiveDataFromCompound(compoundSummary)
        final List<String> targets = []
        activeInactiveData["hits"].each {targets <<  it }
        activeInactiveData["misses"].each {targets <<  it }
        LinkedHashMap<String, Integer> accumulatedTargets = ringManagerService.accumulateAccessionNumbers( targets )

        when:
        List<List<TargetClassInfo>> accumulatedMaps = []
        accumulatedTargets.each{k,v->
            List<String> hierarchyDescription = sunburstCacheService.getTargetClassInfo(k)
            if (hierarchyDescription != null){
                accumulatedMaps<<sunburstCacheService.getTargetClassInfo(k)
            }
        }
        RingNode ringNode = ringManagerService.ringNodeFactory(accumulatedMaps.flatten())

        then:
        ringNode.toString().size() > 0
        ringNode.toString().contains("actin family cytoskeletal protein")
        ringNode.toString().find(/basic helix-loop-helix transcription factor[^\n]+/).find(/size\":\d/).find(/\d/) == '5'
        ringNode.toString().find(/microtubule family cytoskeletal protein[^\n]+/).find(/children/) == 'children'
        ringNode.toString().find(/actin and actin related protein[^\n]+/).find(/size\":\d/).find(/\d/) == '6'
    }


    void "test convertCompoundIntoSunburst"() {
        when:
        RingNode ringNode = ringManagerService.convertCompoundIntoSunburst (2382353L , includeHits, includeNonHits )

        then:
        ringNode.toString().size() > 0

        where:
        includeHits     |    includeNonHits
        true            |       false
        false           |       true
        true            |       true
        false           |       false

    }



}
