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

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
class ActivityOutcomeUnitSpec extends Specification {
    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test getLabel"() {
        when:
        final String returnedLabel = activityOutcome.label
        then:
        assert returnedLabel == expectedLabel
        where:
        label          | activityOutcome              | expectedLabel
        "Active"       | ActivityOutcome.ACTIVE       | "Active"
        "Inactive"     | ActivityOutcome.INACTIVE     | "Inactive"
        "Inconclusive" | ActivityOutcome.INCONCLUSIVE | "Inconclusive"
        "Probe"        | ActivityOutcome.PROBE        | "Probe"
        "Unspecified"  | ActivityOutcome.UNSPECIFIED  | "Unspecified"
    }

    /**
     * {@link ActivityOutcome#findActivityOutcome(int)}
     */
    void "test findActivityOutcome"() {
        when:
        final ActivityOutcome returnedActivityOutcome =ActivityOutcome.findActivityOutcome(pubChemValue)
        then:
        assert returnedActivityOutcome == activityOutcome
        where:
        label          | activityOutcome              | pubChemValue
        "Active"       | ActivityOutcome.ACTIVE       | 2
        "Inactive"     | ActivityOutcome.INACTIVE     | 1
        "Inconclusive" | ActivityOutcome.INCONCLUSIVE | 3
        "Probe"        | ActivityOutcome.PROBE        | 5
        "Unspecified"  | ActivityOutcome.UNSPECIFIED  | 4
    }
    /**
     * {@link ActivityOutcome#getPubChemValue()}
     */
    void "test getPubChemValue"() {
        given:
        when:
        final int pubChemValue = activityOutcome.pubChemValue
        then:
        assert pubChemValue == expectedPubChemValue
        where:
        label          | activityOutcome              | expectedPubChemValue
        "Active"       | ActivityOutcome.ACTIVE       | 2
        "Inactive"     | ActivityOutcome.INACTIVE     | 1
        "Inconclusive" | ActivityOutcome.INCONCLUSIVE | 3
        "Probe"        | ActivityOutcome.PROBE        | 5
        "Unspecified"  | ActivityOutcome.UNSPECIFIED  | 4
    }
}
