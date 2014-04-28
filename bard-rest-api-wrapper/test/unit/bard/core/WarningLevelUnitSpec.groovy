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

package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.WarningLevel

@Unroll
class WarningLevelUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Get Description() #label"() {
        when: "We call the getDescription() method on the given #warningLevel"
        final String description = warningLevel.description
        then: "The expected to get back the description at that warning level"
        assert description
        description == expectedDescription
        where:
        label                   | expectedDescription                  | warningLevel
        "WarningLevel.none"     | "none (between 0.0 and 100.0)"       | WarningLevel.none
        "WarningLevel.moderate" | "moderate (between 100.0 and 300.0)" | WarningLevel.moderate
        "WarningLevel.severe"   | "severe (> 300.0)"                   | WarningLevel.severe
    }

    void "test Get WarningLevel #label"() {
        when: "We call the getWarningLevel() method on the given scaffold"
        final WarningLevel warningLevel = WarningLevel.getWarningLevel(pScore)
        then: "The expected to get back the expected warning level"
        assert warningLevel
        warningLevel == expectedWarningLevel
        where:
        label                | pScore             | expectedWarningLevel
        "With pScore=0"      | new Double(0)      | WarningLevel.none
        "With pScore=99.99"  | new Double(99.99)  | WarningLevel.none
        "With pScore=100"    | new Double(100)    | WarningLevel.moderate
        "With pScore=299.99" | new Double(299.99) | WarningLevel.moderate
        "With pScore=300"    | new Double(300)    | WarningLevel.severe
        "With pScore=20000"  | new Double(20000)  | WarningLevel.severe
    }

}

