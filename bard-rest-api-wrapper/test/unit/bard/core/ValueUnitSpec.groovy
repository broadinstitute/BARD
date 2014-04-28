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
class ValueUnitSpec extends Specification {
    @Shared DataSource dataSource = new DataSource("name", "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test compareTo #label"() {
        when:
        int foundValue = longValue1.compareTo(longValue2)
        then:
        assert foundValue == expectedValue
        where:
        label                        | longValue1                              | longValue2                                | expectedValue
        "value1==value2"             | new Value(source: dataSource, id: id)   | new Value(source: dataSource, id: id)     | 0
        "value1==value2 compare ids" | new Value(source: dataSource, id: id)   | new Value(source: dataSource, id: "JD")   | 0
        "value1 > value2"            | new Value(source: dataSource, id: id)   | new Value(source: dataSource, id: "JD")   | 0
        "Value1 < value2"            | new Value(source: dataSource, id: "JD") | new LongValue(source: dataSource, id: id) | 0

    }

    void "test Constructors #label"() {
        when:
        Value currentValue = value
        then:
        currentValue.id == expectedID
        currentValue.value == null
        assert currentValue.isTerminal()
        assert currentValue.getChildCount() == 0
        assert !value.getChild("Some ID")
        assert value.getChildren().isEmpty()

        where:
        label                               | value                     | expectedID
        "2 arg constructor with datasource" | new Value(dataSource, id) | id
        "2 arg constructor"                 | new Value(parent, id)     | id
        "1 arg constructor with datasource" | new Value(dataSource)     | "bard.core.Value"
        "1 arg constructor"                 | new Value(parent)         | "bard.core.Value"
        "No args"                           | new Value()               | null
    }

    void "test setters"() {
        given:
        Value value = new Value()
        when:
        value.setSource(dataSource)
        value.setId(id)
        value.setURL("url")
        then:
        assert value.source
        assert value.id == id
        assert value.getURL() == "url"
    }

    void "test add Children"() {
        given:
        Value value = new Value(dataSource, id)
        Value child = new Value(dataSource, "someOtherId")
        when:
        value.addValue(child)
        then:
        assert value.getChildCount() == 1
        assert !value.getChildren(id).isEmpty()
        assert value.getChild(id)
        assert !value.getChildren().isEmpty()
    }
}

