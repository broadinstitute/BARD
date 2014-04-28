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
class NumericValueUnitSpec extends Specification {
    @Shared String name = "name"
    @Shared DataSource dataSource = new DataSource(name, "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)
    @Shared int offset = 0
    @Shared int size = 0

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        NumericValue currentNumericValue = numericValue
        then:
        currentNumericValue.getId() == expectedId
        currentNumericValue.getSource().getName() == dataSourceName
        currentNumericValue.getValue() == expectedValue
        where:
        label                               | numericValue                                          | expectedId               | dataSourceName | expectedValue
        "3 arg constructor"                 | new NumericValue(parent, id, new BigDecimal("2"))     | id                       | name           | 2.0
        "3 arg constructor with datasource" | new NumericValue(dataSource, id, new BigDecimal("2")) | id                       | name           | 2.0
        "2 arg constructor with datasource" | new NumericValue(dataSource, id)                      | id                       | name           | null
        "2 arg constructor"                 | new NumericValue(parent, id)                          | id                       | name           | null
        "1 arg constructor"                 | new NumericValue(parent)                              | "bard.core.NumericValue" | name           | null
    }

    void "test compareTo #label"() {
        when:
        int foundValue = numericValue1.compareTo(numericValue2)
        then:
        assert foundValue == expectedValue
        where:
        label                        | numericValue1                                            | numericValue2                                            | expectedValue
        "value1==value2"             | new NumericValue(source: dataSource, id: id, value: 2)   | new NumericValue(source: dataSource, id: id, value: 2)   | 0
        "value1==value2 compare ids" | new NumericValue(source: dataSource, id: id, value: 2)   | new NumericValue(source: dataSource, id: "JD", value: 2) | -1
        "value1 > value2"            | new NumericValue(source: dataSource, id: id, value: 2)   | new NumericValue(source: dataSource, id: "JD", value: 4) | 1
        "Value1 < value2"            | new NumericValue(source: dataSource, id: "JD", value: 4) | new NumericValue(source: dataSource, id: id, value: 2)   | -1

    }

    void "test Empty Constructors"() {
        when:
        NumericValue currentNumericValue = numericValue
        then:
        currentNumericValue.getId() == null
        currentNumericValue.getSource() == null
        currentNumericValue.getValue() == null
        where:
        label                   | numericValue
        "Empty arg constructor" | new NumericValue()
    }

    void "test setters"() {
        given:
        NumericValue currentNumericValue = numericValue
        when:
        currentNumericValue.setValue(new Double(2.0))
        then:
        currentNumericValue.getValue() == 2.0
        where:
        label                   | numericValue
        "Empty arg constructor" | new NumericValue()
    }
}

