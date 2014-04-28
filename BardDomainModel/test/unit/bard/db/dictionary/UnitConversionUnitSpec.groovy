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

package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/15/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Build(UnitConversion)
@Unroll
class UnitConversionUnitSpec extends Specification {

    UnitConversion domainInstance

    @Before
    void doSetup() {
        domainInstance = UnitConversion.buildWithoutSave()
    }

    void "test conversion of valToConvert: #valToConvert offset: #offset  multiplier: #multiplier formula: #formula"() {
        given:
        domainInstance.offset = offset
        domainInstance.multiplier = multiplier
        domainInstance.formula = formula

        when:
        final actualValue = domainInstance.convert(valToConvert)

        then:
        actualValue == expectedValue

        where:
        desc                              | offset  | multiplier | formula                         | valToConvert | expectedValue
        'negative offset'                 | -273.12 | null       | null                            | 10           | -263.12
        'positive offset'                 | 273.12  | null       | null                            | 10           | 283.12
        'positive offset with multiplier' | 1.0     | 2.0        | null                            | 10           | 22.0

        'multiplier '                     | null    | 1          | null                            | 1            | 1
        'multiplier '                     | null    | 0.5        | null                            | 10           | 5
        'multiplier '                     | null    | 0.25       | null                            | 10           | 2.5

        'multiplier '                     | null    | 0.1        | null                            | 1            | 0.1
        'multiplier '                     | null    | 0.01       | null                            | 1            | 0.01
        'multiplier '                     | null    | 0.001      | null                            | 1            | 0.001

        'multiplier '                     | null    | 10         | null                            | 1            | 10
        'multiplier '                     | null    | 100        | null                            | 1            | 100
        'multiplier '                     | null    | 1000       | null                            | 1            | 1000

        'formula'                         | null    | null       | "value * 2"                     | 1            | 2
        'formula'                         | null    | null       | "value / 2"                     | 1            | 0.5
        'formula'                         | null    | null       | "Math.pow(value, 3)"            | 2            | 8
        'formula'                         | null    | null       | "Math.sqrt(value)"              | 25           | 5

        'formula'                         | 2       | 2          | "(value + offset) * multiplier" | 2            | 8
        'formula'                         | -2      | 2          | "(value + offset) * multiplier" | 2            | 0
        'formula'                         | -2      | 2          | "value + offset * multiplier"   | 2            | -2

    }


}
