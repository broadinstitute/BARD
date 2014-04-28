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

package bard.core.util


import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueUtilUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Get By Value #label"() {

        when:
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnitUtil.getByValue(experimentalValueUnitValue)

        then:
        assert experimentalValueUnit == expectedExperimentValueUnit

        where:
        label         | experimentalValueUnitValue | expectedExperimentValueUnit
        "Molar"       | " M"                       | ExperimentalValueUnitUtil.Molar
        "Millimolar"  | " mM"                      | ExperimentalValueUnitUtil.Millimolar
        "Micromolar"  | " uM"                      | ExperimentalValueUnitUtil.Micromolar
        "Nanomolar"   | " nM"                      | ExperimentalValueUnitUtil.Nanomolar
        "Picomolar"   | " pM"                      | ExperimentalValueUnitUtil.Picomolar
        "Femtomolar"  | " fM"                      | ExperimentalValueUnitUtil.Femtomolar
        "Attamolar"   | " aM"                      | ExperimentalValueUnitUtil.Attamolar
        "Zeptomolar"  | " zM"                      | ExperimentalValueUnitUtil.Zeptomolar
        "Yoctomolar"  | " yM"                      | ExperimentalValueUnitUtil.Yoctomolar
        "unknown"     | null                       | ExperimentalValueUnitUtil.unknown
        "unknown"     | ""                         | ExperimentalValueUnitUtil.unknown
        "Bogus Value" | "bogus value"              | ExperimentalValueUnitUtil.unknown
    }

    void "test performUnitNormalization"() {
        given: "values the calling routine would request"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(0.02, ExperimentalValueUnitUtil.Micromolar, ExperimentalValueTypeUtil.unknown, true)
        final ExperimentalValueUnitUtil originalUnit = experimentalValue.experimentalValueUnit
        experimentalValue.insistOnOutputUnits = ExperimentalValueUnitUtil.unknown

        when: "requesting normalization"
        experimentalValue.performUnitNormalization(ExperimentalValueUnitUtil.unknown, ExperimentalValueUnitUtil.unknown)

        then: "nothing changes"
        assert originalUnit == experimentalValue.experimentalValueUnit

    }

    void "test insistOnOutputUnit is False"() {
        given:
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(0.02, ExperimentalValueUnitUtil.Micromolar, ExperimentalValueTypeUtil.unknown, true)
        experimentalValue.insistOnOutputUnits = ExperimentalValueUnitUtil.unknown
        when: "#label"
        Boolean bool = experimentalValue.insistOnOutputUnit()

        then: "The resulting search filters size must equal the expected value"
        assert !bool

    }



    void "test response to a null ExperimentalValue on the constructor"() {
        given:
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(0.02, null, ExperimentalValueTypeUtil.unknown, true)
        experimentalValue.insistOnOutputUnits = ExperimentalValueUnitUtil.unknown
        when: "#label"
        Boolean bool = experimentalValue.insistOnOutputUnit()

        then: "The resulting search filters size must equal the expected value"
        assert !bool

    }



    void "test roundOff to Desired Precision"() {
        given:
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(initialValue, initialUnit, ExperimentalValueTypeUtil.numeric, true)
        when: "#label"
        String val = experimentalValue.roundoffToDesiredPrecision(initialValue)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert val == stringValue

        where:
        label                    | initialUnit                          | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.02         | "0.02"

    }

    void "test handling of unit conversions"() {
        when: "#label"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(initialValue, initialUnit, ExperimentalValueTypeUtil.numeric, true)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                    | initialUnit                          | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.23         | "1.23 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Millimolar | 1.23         | "1.23 mM"
        "converting unit values" | ExperimentalValueUnitUtil.Molar      | 1.23         | "1.23 M"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123          | "123 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 12.3         | "12.3 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.23         | "1.23 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.123        | "0.123 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.0123       | "12.3 nM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.00123      | "1.23 nM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.000123     | "0.123 nM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.0000123    | "12.3 pM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0.00000123   | "1.23 pM"
    }


    void "test other default ctor"() {
        when: "#label"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(initialValue, printUnits)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                                   | printUnits | initialValue | stringValue
        "converting unit values"                | true       | 1            | "1 M"
        "converting unit values"                | true       | 1.2          | "1.2 M"
        "converting unit values"                | false      | 1.23         | "1.23"
        "converting unit values"                | false      | 1.234        | "1.23"
        "converting unit values negative value" | false      | -1.234       | "-1.23"
    }


    void "test another default ctor"() {
        when: "#label"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(initialValue)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                                   | initialValue | stringValue
        "converting unit values"                | 1D           | "1"
        "converting unit values"                | 1.2D         | "1.2"
        "converting unit values"                | 1.23D        | "1.23"
        "converting unit values"                | 1.235D       | "1.24"
        "converting unit values negative value" | -1.234D      | "-1.23"
    }




    void "test what we do when there is nothing to print"() {
        when: "#label"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(47, true)
        experimentalValue.activity = false
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == "(no activity)"
    }



    void "test ExperimentalValueUnitUtil.getByValue"() {
        when: "#label"
        ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnitUtil.getByValue(val)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValueUnit == result

        where:
        val    | result
        "M"    | ExperimentalValueUnitUtil.Molar
        "mM"   | ExperimentalValueUnitUtil.Millimolar
        "uM"   | ExperimentalValueUnitUtil.Micromolar
        "nM"   | ExperimentalValueUnitUtil.Nanomolar
        "pM"   | ExperimentalValueUnitUtil.Picomolar
        "fM"   | ExperimentalValueUnitUtil.Femtomolar
        "aM"   | ExperimentalValueUnitUtil.Attamolar
        "zM"   | ExperimentalValueUnitUtil.Zeptomolar
        "yM"   | ExperimentalValueUnitUtil.Yoctomolar
        "junk" | ExperimentalValueUnitUtil.unknown
    }


    void "test handling of precision conversions"() {
        when: "#label"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(initialValue, initialUnit, ExperimentalValueTypeUtil.numeric, true)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                    | initialUnit                          | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 0            | "0.00"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1            | "1 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.2          | "1.2 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.23         | "1.23 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.234        | "1.23 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.2345       | "1.23 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 1.23456      | "1.23 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 12           | "12 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 12.3         | "12.3 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 12.34        | "12.3 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 12.345       | "12.3 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 12.3456      | "12.3 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123          | "123 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123.4        | "123 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123.5        | "124 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123.6        | "124 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123.45       | "123 uM"
        "converting unit values" | ExperimentalValueUnitUtil.Micromolar | 123.456      | "123 uM"
    }



    void "test handling of 0"() {
        when: "we receive a zero through any constructor"
        List<ExperimentalValueUtil> experimentalValueList = []
        experimentalValueList << new ExperimentalValueUtil(0.0, ExperimentalValueUnitUtil.Micromolar, ExperimentalValueTypeUtil.numeric, true)
//        experimentalValueList <<  new ExperimentalValue(0.0, ExperimentalValueUnitUtil.Micromolar, ExperimentalValueType.numeric, false)
        experimentalValueList << new ExperimentalValueUtil(0.0, true)
//        experimentalValueList << new ExperimentalValue(0.0, false)
        experimentalValueList << new ExperimentalValueUtil(0 as double)

        then: "The resulting conversion should equal a decimal representation of 0 with no units"
        for (ExperimentalValueUtil experimentalValue in experimentalValueList)
            assert experimentalValue.toString() == "0.00"
    }

    /**
     *  While we try to hold things to a nice, clean decimal representation by shifting the units around,
     *  it is easy to devise corner cases that are outside the range that we can represent in this way.
     *  When this happens, the ExperimentalValues class essentially gives up, and passes the value to
     *  the BigDecimal toEngineeringString method and says "do it you can let this one".  The users
     *  will likely never see any of these results, but just in case the numbers get really weird it's
     *  worth defining what the code should do.
     */
    void "test explicit conversions to unusual units"() {
        when: "#label"
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(initialValue, initialUnit, ExperimentalValueTypeUtil.numeric, true)
        experimentalValue.setInsistOnOutputUnits(outputUnit)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                               | initialUnit                          | outputUnit                           | initialValue | stringValue
        "convert big Molar to yoctomolar"   | ExperimentalValueUnitUtil.Molar      | ExperimentalValueUnitUtil.Yoctomolar | 1.2E31       | "12E+54 yM"
        "convert tiny yoctomolar to Molar"  | ExperimentalValueUnitUtil.Yoctomolar | ExperimentalValueUnitUtil.Molar      | 1.2E-31      | "120E-57 M"
        "conversions with negative numbers" | ExperimentalValueUnitUtil.Micromolar | ExperimentalValueUnitUtil.Micromolar | -123456789   | "-123E+6 uM"
        "conversions with negative numbers" | ExperimentalValueUnitUtil.Micromolar | ExperimentalValueUnitUtil.Millimolar | -123456789   | "-123E+3 mM"
        "conversions with negative numbers" | ExperimentalValueUnitUtil.Micromolar | ExperimentalValueUnitUtil.Molar      | -123456789   | "-123 M"        // small enough to the formatted correctly
        "conversions with negative numbers" | ExperimentalValueUnitUtil.Micromolar | ExperimentalValueUnitUtil.Micromolar | -12345E-9    | "-0.0000123 uM"
        "conversions with negative numbers" | ExperimentalValueUnitUtil.Micromolar | ExperimentalValueUnitUtil.Millimolar | -12345E-9    | "-12.3E-9 mM"
        "conversions with negative numbers" | ExperimentalValueUnitUtil.Micromolar | ExperimentalValueUnitUtil.Molar      | -12345E-9    | "-12.3E-12 M"
    }

    void "test toString #label"() {
        given:
        ExperimentalValueUtil experimentalValue = new ExperimentalValueUtil(new BigDecimal("2.0"), Boolean.FALSE)
        experimentalValue.activity = activity
        experimentalValue.experimentalValueType = experimentValueType

        when: "#label"
        final String stringRepresentation = experimentalValue.toString()
        then: "The resulting search filters size must equal the expected value"
        assert stringRepresentation == expectedStringValue

        where:
        label                                                          | activity | experimentValueType                          | expectedStringValue
        "No Activity"                                                  | false    | ExperimentalValueTypeUtil.greaterThanNumeric | "(no activity)"
        "Experiment Value Type==greaterThanNumeric and activity==true" | true     | ExperimentalValueTypeUtil.greaterThanNumeric | "> 2"
        "No Activity"                                                  | false    | ExperimentalValueTypeUtil.lessThanNumeric    | "(no activity)"
        "Experiment Value Type == lessThanNumeric and activity==true"  | true     | ExperimentalValueTypeUtil.lessThanNumeric    | "< 2"
        "No Activity"                                                  | false    | ExperimentalValueTypeUtil.percentageNumeric  | "(no activity)"
        "Experiment Value Type == percentage and activity==true"       | true     | ExperimentalValueTypeUtil.percentageNumeric  | "2 %"
        "No ExperimentalValueType and No activity"                     | true     | null                                         | "2.0"
    }



}

