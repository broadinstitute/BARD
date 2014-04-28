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
