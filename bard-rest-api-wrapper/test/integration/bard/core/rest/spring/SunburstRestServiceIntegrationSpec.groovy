package bard.core.rest.spring

import bard.core.rest.helper.RESTTestHelper

import grails.plugin.spock.IntegrationSpec

import spock.lang.Unroll

import bard.core.rest.spring.compounds.TargetClassInfo
import grails.plugin.cache.GrailsCacheManager

/**
 * Tests Target service
 */
@Mixin(RESTTestHelper)
@Unroll
class SunburstRestServiceIntegrationSpec extends IntegrationSpec {
    SunburstCacheService sunburstCacheService

    void "test getTargetClassInfo #label"() {
        when:
        TargetClassInfo targetClassInfo = sunburstCacheService.getTargetClassInfo(targetClassId)

        then:
        assert targetClassInfo
        assert targetClassId == targetClassInfo.id
        assert targetClassInfo.description
        assert targetClassInfo.path
        where:
        label              | targetClassId
        "No Caching"       | "PC00201"
        "Should hit Cache" | "PC00201"


    }


}