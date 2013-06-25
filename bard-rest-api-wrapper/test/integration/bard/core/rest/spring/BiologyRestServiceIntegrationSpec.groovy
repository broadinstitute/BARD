package bard.core.rest.spring

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 5/16/13
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */

import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.biology.BiologyEntity
import grails.plugin.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Tests for ProjectRestService
 */
@Mixin(RESTTestHelper)
@Unroll
class BiologyRestServiceIntegrationSpec extends IntegrationSpec {
    BiologyRestService biologyRestService
    @Shared
    List<Long> TEST_BIDS = [1366, 1365]



    void "test biologyRestService "() {
        when: "generate activities directly via post"
        final List <BiologyEntity> biologyEntityList = biologyRestService.convertBiologyId(TEST_BIDS)

        then:
        assert biologyEntityList != null


    }
}