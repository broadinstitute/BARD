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

package bard.db.guidance

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by ddurkin on 1/29/14.
 */
@Unroll
class ContextUtilsUnitSpec extends Specification {


    void "test all rules called #desc"() {
        final List<GuidanceRule> rules = []
        numOfRules.times {
            rules.add(Mock(GuidanceRule))
        }

        when:
        final List<Guidance> guidanceMessages = GuidanceUtils.getGuidance(rules)

        then:
        numOfRules * _.getGuidance() >>> returnMessages // NOTE: >>>  see http://code.google.com/p/spock/wiki/Interactions iterator returns different values for each call
        0 * _._
        guidanceMessages.message == expectedMessages

        where:
        desc                 | numOfRules | expectedMessages           | returnMessages
        '1 rule 2 messages'  | 1          | ['message 1', 'message 2'] | [[new DefaultGuidanceImpl('message 1'), new DefaultGuidanceImpl('message 2')]]
        '2 rules 1 msg each' | 2          | ['message 1', 'message 2'] | [[new DefaultGuidanceImpl('message 1')], [new DefaultGuidanceImpl('message 2')]]
    }
}
