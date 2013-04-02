package bardqueryapi

import bard.core.rest.helper.RESTTestHelper
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
        TargetClassInfo targetClassInfo = sunburstCacheService.getTargetClassInfo(targetAccessionNumber)

        then:
        assert targetClassInfo
        assert targetAccessionNumber== targetClassInfo.accessionNumber
        assert targetClassInfo.description
        assert targetClassInfo.path
        where:
        label              | targetAccessionNumber
        "No Caching"       | "A0AUV4"
        "Should hit Cache" | "A0AUV4"


    }


}