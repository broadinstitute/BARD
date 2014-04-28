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

package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.PromiscuityScaffold
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.WarningLevel

@Unroll
class PromiscuityScaffoldUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String PROMISCUITY_SCAFFOLD = '''
    {
            "wTested": 17064,
            "sActive": 51,
            "wActive": 1876,
            "aTested": 479,
            "sTested": 51,
            "pScore": 2445,
            "scafid": 46705,
            "aActive": 222,
            "inDrug": true,
            "smiles": "O=C1C=CC(=N)c2ccccc12"
    }
    '''

    void "test serialization to Promiscuity Scaffold"() {
        when:
        final PromiscuityScaffold promiscuityScaffold = objectMapper.readValue(PROMISCUITY_SCAFFOLD, PromiscuityScaffold.class)
        then:
        assert 17064 == promiscuityScaffold.testedWells
        assert 51 == promiscuityScaffold.activeSubstances
        assert 1876 == promiscuityScaffold.activeWells
        assert 479 == promiscuityScaffold.testedAssays
        assert 51 == promiscuityScaffold.testedSubstances
        assert 2445 == promiscuityScaffold.promiscuityScore
        assert 46705 == promiscuityScaffold.scaffoldId
        assert 222 == promiscuityScaffold.activeAssays
        assert promiscuityScaffold.inDrug
        assert "O=C1C=CC(=N)c2ccccc12" == promiscuityScaffold.smiles
    }

    void "test Get WarningLevel #label"() {
        when: "We call the getWarningLevel() method on the given scaffold"
        final WarningLevel warningLevel = scaffold.warningLevel
        then: "The expected to get back the expected warning level"
        assert warningLevel
        warningLevel == expectedWarningLevel
        where:
        label                          | scaffold                                                                    | expectedWarningLevel
        "With promiscuityScore=0"      | new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(0))      | WarningLevel.none
        "With promiscuityScore=99.99"  | new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(99.99))  | WarningLevel.none
        "With promiscuityScore=100"    | new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(100))    | WarningLevel.moderate
        "With promiscuityScore=299.99" | new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(299.99)) | WarningLevel.moderate
        "With promiscuityScore=300"    | new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(300))    | WarningLevel.severe
        "With promiscuityScore=20000"  | new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(20000))  | WarningLevel.severe
    }

    void "scaffold.hashCode #label"() {
        given: "A valid PromiscuityScaffold"
        PromiscuityScaffold scaffold =
            new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(0), testedSubstances: 1, scaffoldId: 1, activeSubstances: 1,
                    activeAssays: 1,testedAssays: 1, testedWells: 1, activeWells: 1, inDrug: false)

        when: "We call the hashCode method"
        final int hashCode = scaffold.hashCode()
        then: "The expected hashCode is returned"
        assert hashCode
        hashCode == 371101637
    }

    void "scaffold.compareTo #label"() {
        given: "Valid Scafold Objects"
        PromiscuityScaffold scaffold1 =
            new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(0), testedSubstances: testedSubstances1, scaffoldId: 1, activeSubstances: 1,
                    activeAssays: 1, testedAssays: 1, testedWells: 1, activeWells: 1, inDrug: false)
        PromiscuityScaffold scaffold2 =
            new PromiscuityScaffold(smiles: "CC", promiscuityScore: new Double(0), testedSubstances: testedSubstances2, scaffoldId: 1, activeSubstances: 1,
                    activeAssays: 1, testedAssays: 1, testedWells: 1, activeWells: 1, inDrug: false)
        when: "We call the compareTo method with objects"
        final int compareToVal = scaffold1.compareTo(scaffold2)
        then: "We expected the method to return the expected value"
        assert compareToVal == expectedAnswer
        where:
        label                 | testedSubstances1 | testedSubstances2 | expectedAnswer
        "cTested1==cTested2"  | 200               | 200               | 0
        "cTested1 > cTested2" | 201               | 200               | 1
        "cTested1 < cTested2" | 201               | 205               | -1
    }



    void "scaffold.equals #label"() {
        when: "We call the equals method with scaffold1 and scaffold2"
        final boolean returnedValue = scaffold1.equals(scaffold2)
        then: "We expected method to return the expected value"
        assert returnedValue == expectedAnswer
        where:
        label                      | scaffold1                                      | scaffold2                                      | expectedAnswer
        "this equals that"         | new PromiscuityScaffold(testedSubstances: 200) | new PromiscuityScaffold(testedSubstances: 200) | true
        "that is null"             | new PromiscuityScaffold(testedSubstances: 201) | null                                           | false
        "this != that"             | new PromiscuityScaffold(testedSubstances: 201) | new PromiscuityScaffold(testedSubstances: 205) | false
        "this.class != that.class" | new PromiscuityScaffold(testedSubstances: 201) | 200                                            | false
    }


}

