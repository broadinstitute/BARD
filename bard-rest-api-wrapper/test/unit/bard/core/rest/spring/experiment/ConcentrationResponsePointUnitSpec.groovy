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

package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConcentrationResponsePointUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @Shared
    String JSON_DATA = '''
     {
        "testConc":9.85385E-4,"value":"1.9624",
        "childElements":
        [
           {
              "displayName":"Rep2ForExperiment3_1000_uM",
              "dictElemId":1016,
              "testConcUnit":"uM",
              "testConc":1000.0,
              "value":"105.693"
           },
           {
              "displayName":"Rep1ForExperiment3_1000_uM",
              "dictElemId":1016,
              "testConcUnit":"uM",
              "testConc":1000.0,
              "value":"102.669"
           },
           {
              "displayName":"StddevForExperiment3_1000uM",
              "dictElemId":613,
              "testConcUnit":"uM",
              "testConc":1000.0,
              "value":"2.13829"
           }
        ]
     }
    '''

    void "test concentration response point serialization"() {
        when:
        ConcentrationResponsePoint concentrationResponsePoint = objectMapper.readValue(JSON_DATA, ConcentrationResponsePoint.class)
        then:
        assert concentrationResponsePoint.testConcentration
        assert concentrationResponsePoint.value
        assert concentrationResponsePoint.toDisplay("uM") == "1.96 @ 0.985 nM"
        assert concentrationResponsePoint.displayActivity() == "1.96"
        assert concentrationResponsePoint.displayConcentration("uM") == "0.985 nM"
        assert concentrationResponsePoint.toDisplay("uM")
        final List<ActivityData> childElements = concentrationResponsePoint.childElements
        assert childElements
        assert childElements.size() == 3
        for (ActivityData childElement in childElements) {
            assert childElement.displayName
            assert childElement.dictElemId
            assert childElement.testConcentration
            assert childElement.testConcentrationUnit
            assert childElement.value
        }
    }


    void "test toDisplay #label"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(value: responseValue, testConcentration: concentrationValue)
        when:
        String display = concentrationResponsePoint.toDisplay("um")
        then:
        assert display.trim() == expectedDisplay

        where:
        label                                    | responseValue | concentrationValue | expectedDisplay
        "With Response Value"                    | "2.0"         | null               | "2"
        "With Concentration Value"               | ""            | 2.0                | "@ 2 uM"
        "With Response and Concentration values" | "2.0"         | 2.0                | "2 @ 2 uM"
        "With No Value"                          | null          | null               | ""
    }

    void "test displayActivity"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(value: responseValue)
        when:
        String display = concentrationResponsePoint.displayActivity()
        then:
        assert display.trim() == expectedDisplay
        where:
        label                               | responseValue | expectedDisplay
        "With Response Value"               | "2.0"         | "2"
        "With Response Value, not a number" | "No Number"   | ""
        "With Null Response Value"          | null          | ""

    }

    void "test displayConcentration"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(testConcentration: concentrationValue)
        when:
        String display = concentrationResponsePoint.displayConcentration("um")
        then:
        assert display.trim() == expectedDisplay
        where:
        label                      | concentrationValue | expectedDisplay
        "With Concentration Value" | 2.0                | "2 uM"
        "With No Value"            | null               | ""

    }


}

