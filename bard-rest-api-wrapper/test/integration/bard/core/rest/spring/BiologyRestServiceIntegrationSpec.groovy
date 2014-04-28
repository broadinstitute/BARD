/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import grails.converters.JSON
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


    void "test biologyRestService "() {
        given:
        String ncgcBaseURL = applicationContext.getBean("grailsApplication").config.ncgc.server.root.url
        def result = this.biologyRestService.getForObject("${ncgcBaseURL}/biology?top=10", String.class)
        def resultJSON = JSON.parse(result)
        List<Long> TEST_BIDS = [(resultJSON.collection[0] - '/biology/').toLong(), (resultJSON.collection[1] - '/biology/').toLong()]

        when: "generate activities directly via post"
        final List <BiologyEntity> biologyEntityList = biologyRestService.convertBiologyId(TEST_BIDS)

        then:
        assert biologyEntityList != null


    }
}
