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

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class HillCurveValueUnitSpec extends Specification {
    @Shared String name = "name"
    @Shared DataSource dataSource = new DataSource(name, "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        HillCurveValue currentHillCurveValue = hillCurveValue
        then:
        currentHillCurveValue.getValue() == null
        where:
        label                               | hillCurveValue
        "1 arg constructor"                 | new HillCurveValue(parent)
        "1 arg constructor with datasource" | new HillCurveValue(dataSource)
        "2 arg constructor with datasource" | new HillCurveValue(dataSource, id)
        "2 arg constructor"                 | new HillCurveValue(parent, id)
        "Empty arg constructor"             | new HillCurveValue()
    }

    void "test compareTo #label"() {
        when:
        int foundValue = hillCurveValue1.compareTo(hillCurveValue2)
        then:
        assert foundValue == expectedValue
        where:
        label             | hillCurveValue1                                          | hillCurveValue2                                            | expectedValue
        "compareTo == 0"  | new HillCurveValue(source: dataSource, id: id, slope: 2) | new HillCurveValue(source: dataSource, id: id, slope: 2)   | 0
        "compareTo == 1"  | new HillCurveValue(source: dataSource, id: id, slope: 2) | new HillCurveValue(source: dataSource, id: "JD", slope: 2) | -1
        "compareTo == -1" | new HillCurveValue(source: dataSource, id: id, slope: 4) | new HillCurveValue(source: dataSource, id: id, slope: 2)   | 1

    }

    void "test add"() {
        given:
        final Double concentration = new Double("2.0")
        final Double response = new Double("4.0")
        final HillCurveValue currentHillCurveValue = new HillCurveValue()
        when:
        currentHillCurveValue.add(concentration, response)
        then:
        assert currentHillCurveValue.getResponse().length > 0
        assert currentHillCurveValue.getConc()[0] == concentration
        assert currentHillCurveValue.getResponse()[0] == response
        assert currentHillCurveValue.size() == 1
        assert !currentHillCurveValue.getConcentrationUnits()

    }

    void "test evaluate #label"() {
        given:
        final Double concentration = new Double("2.0")
        final HillCurveValue currentHillCurveValue = new HillCurveValue()
        currentHillCurveValue.setS0(s0)
        currentHillCurveValue.setSinf(sInf)
        currentHillCurveValue.setCoef(coeff)
        currentHillCurveValue.setSlope(slope)
        when:
        final Double result = currentHillCurveValue.evaluate(concentration)
        then:
        assert result?.intValue() == expectedResult
        assert currentHillCurveValue.getCoef() == coeff
        assert currentHillCurveValue.getS0() == s0
        assert currentHillCurveValue.getSinf() == sInf
        assert currentHillCurveValue.getSlope() == slope
        where:
        label                | coeff | s0   | sInf | slope | expectedResult
        "slope==null"        | 2.0   | null | null | null  | null
        "coeff==null"        | null  | null | null | 2.0   | null
        "slope==coeff==null" | null  | null | null | null  | null
        "s0==null"           | 2.0   | null | null | 4.0   | 19
        "sInf==null"         | 2.0   | 2.0  | null | 4.0   | 21
        "No nulls"           | 2.0   | 2.0  | 2.0  | 4.0   | 2
    }
}

