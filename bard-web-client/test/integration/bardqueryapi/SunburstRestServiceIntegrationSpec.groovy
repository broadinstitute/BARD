package bardqueryapi

import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.compounds.TargetClassInfo
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Tests Target service
 */
@Unroll
class SunburstRestServiceIntegrationSpec extends IntegrationSpec {
    SunburstCacheService sunburstCacheService

    void "test getTargetClassInfo #label"() {

        when:
        List<TargetClassInfo> targetClassInfos = sunburstCacheService.getTargetClassInfo(targetAccessionNumber)

        then:
        assert targetClassInfos
        TargetClassInfo targetClassInfo = targetClassInfos.get(0)
        assert targetAccessionNumber == targetClassInfo.accessionNumber
        assert targetClassInfo.description
        assert targetClassInfo.path
        where:
        label              | targetAccessionNumber
        "No Caching"       | "A0AUV4"
        "Should hit Cache" | "A0AUV4"


    }

    void "test put #label"() {
        given:
        String input = "PC00220\ttransferase\tEnzymes transferring a group from one compound (donor) to another compound (acceptor).  Kinase is a separate category, so it is not included here.\t1.20.00.00.00\tpanther\tA0A4Z3\t\\transferase\\"
        List<String> data = input.split("\t") as List<String>
        final TargetClassInfo targetClassInfo = new TargetClassInfo()
        targetClassInfo.id = data.get(0).trim()
        targetClassInfo.name = data.get(1).trim()
        targetClassInfo.description = data.get(2).trim()
        targetClassInfo.levelIdentifier = data.get(3).trim()
        targetClassInfo.source = data.get(4).trim()
        targetClassInfo.accessionNumber = data.get(5).trim()
        targetClassInfo.path = data.get(6).trim()
        when:
        sunburstCacheService.save(targetClassInfo)

        then:
        List<TargetClassInfo> targetClassInfos = this.sunburstCacheService.getTargetClassInfo(targetAccessionNumber)
        assert targetClassInfos
        TargetClassInfo targetClassInfo1 = targetClassInfos.get(0)
        assert targetAccessionNumber == targetClassInfo1.accessionNumber
        where:
        label        | targetAccessionNumber
        "No Caching" | "A0A4Z3"


    }

}