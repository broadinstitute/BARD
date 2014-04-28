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
import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.rest.spring.compounds.Scaffold

@Unroll
class PromiscuityScoreUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "promiscuityScore.toString #label"() {
        given: "A valid PromiscuityScore Object"

        PromiscuityScore promiscuityScore = new PromiscuityScore(cid: cid, scaffolds: scaffolds)
        when: "We call the toString method"
        final String promiscuityScoreString = promiscuityScore.toString()
        then: "The expected string representation is displayed"
        assert promiscuityScoreString
        promiscuityScoreString == expectedString
        where:
        label            | cid | scaffolds                     | expectedString
        "With Scaffolds" | 200 | [new Scaffold(scafsmi: "CC")] | "CID: 200\nCC"
        "No Scaffolds"   | 201 | null                          | "CID: 201"
    }

    void "promiscuityScore.hashCode #label"() {
        given: "A valid PromiscuityScore Object"

        PromiscuityScore promiscuityScore = new PromiscuityScore(cid: cid, scaffolds: scaffolds)
        when: "We call the hashCode method"
        final int hashCode = promiscuityScore.hashCode()
        then: "The expected hashCode is returned"
        assert hashCode
        hashCode == expectedHashCode
        where:
        label            | cid | scaffolds                     | expectedHashCode
        "With Scaffolds" | 200 | [new Scaffold(scafsmi: "CC")] | -481319079
        "No Scaffolds"   | 201 | null                          | 22568
    }

    void "promiscuityScore.compareTo #label"() {
        given: "A valid PromiscuityScore Object"
        PromiscuityScore promiscuityScore1 = new PromiscuityScore(cid: cid1, scaffolds: null)
        PromiscuityScore promiscuityScore2 = new PromiscuityScore(cid: cid2, scaffolds: null)
        when: "We call the compareTo method with promiscuityScore1 and promiscuityScore1"
        final int compareToVal = promiscuityScore1.compareTo(promiscuityScore2)
        then: "We expected the method to return the expected value"
        assert compareToVal == expectedAnswer
        where:
        label         | cid1 | cid2 | expectedAnswer
        "CID1==CID2"  | 200  | 200  | 0
        "CID1 > CID2" | 201  | 200  | 1
        "CID1 < CID2" | 201  | 205  | -1
    }


    void "promiscuityScore.equals #label"() {
        when: "We call the equals method with promiscuityScore1 and promiscuityScore1"
        final boolean returnedValue = promiscuityScore1.equals(promiscuityScore2)
        then: "We expected method to return the expected value"
        assert returnedValue == expectedAnswer
        where:
        label                      | promiscuityScore1              | promiscuityScore2              | expectedAnswer
        "this equals that"         | new PromiscuityScore(cid: 200) | new PromiscuityScore(cid: 200) | true
        "that is null"             | new PromiscuityScore(cid: 201) | null                           | false
        "this != that"             | new PromiscuityScore(cid: 201) | new PromiscuityScore(cid: 205) | false
        "this.class != that.class" | new PromiscuityScore(cid: 201) | 200                            | false
    }
}

