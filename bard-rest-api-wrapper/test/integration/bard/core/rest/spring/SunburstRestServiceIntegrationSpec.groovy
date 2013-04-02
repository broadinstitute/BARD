package bard.core.rest.spring

import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.compounds.TargetClassInfo
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Tests Target service
 */
@Mixin(RESTTestHelper)
@Unroll
class SunburstRestServiceIntegrationSpec extends IntegrationSpec {
    SunburstCacheService sunburstCacheService

    void "test getTargetClassInfo #label"() {
        when:
        TargetClassInfo targetClassInfo = sunburstCacheService.getTargetClassInfo(targetAccessionNumber)

        then:
        assert targetClassInfo
        assert targetAccessionNumber== targetClassInfo.accessionNumber
        assert targetClassInfo.description
        assert targetClassInfo.path
        where:
        label              | targetAccessionNumber
        "No Caching"       | "A0A4Z3"
        "Should hit Cache" | "A0A4Z3"


    }


}