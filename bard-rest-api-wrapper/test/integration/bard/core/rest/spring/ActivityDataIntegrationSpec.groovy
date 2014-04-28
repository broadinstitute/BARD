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

import spock.lang.Unroll
import grails.plugin.spock.IntegrationSpec
import bard.core.rest.spring.experiment.ActivityData

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/19/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ActivityDataIntegrationSpec extends IntegrationSpec {


    void "getDictionaryLabel #label"() {
        given:
        ActivityData activityData = new ActivityData(dictElemId: elementId)


        when:
        final String foundLabel = activityData.getDictionaryLabel()

        then:
        assert foundLabel == expectedLabel

        where:
        label                                       | elementId | expectedLabel
        "Element Id=3"                              | 3         | "assay protocol"
        "Element Id=4"                              | 4         | "assay component"
        "Element Id=3 again. Should only hit cache" | 3         | "assay protocol"
        "Element Id=4 again. Should only hit cache" | 4         | "assay component"


    }

}
