package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class IntValueUnitSpec extends Specification {
    @Shared String name = "name"
    @Shared DataSource dataSource = new DataSource(name, "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test compareTo #label"() {
        when:
        int foundValue = intValue1.compareTo(intValue2)
        then:
        assert foundValue == expectedValue
        where:
        label                        | intValue1                                                    | intValue2                                                    | expectedValue
        "value1==value2"             | new IntValue(source: dataSource, id: id, value: 2)           | new IntValue(source: dataSource, id: id, value: 2)           | 0
        "value1==value2 compare ids" | new IntValue(source: dataSource, id: id, value: 2)           | new IntValue(source: dataSource, id: "JD", value: 2)         | -1
        "value1 > value2"            | new IntValue(source: dataSource, id: id, value: 2)           | new IntValue(source: dataSource, id: "JD", value: 4)         | 1
        "Value1 < value2"            | new IntValue(source: dataSource, id: "JD", value: 4)         | new IntValue(source: dataSource, id: id, value: 2)           | -1
        "[* TO 10] > [10 TO 20]"     | new IntValue(source: dataSource, id: "[* TO 10]", value: 4)  | new IntValue(source: dataSource, id: "[10 TO 20]", value: 2) | -1
        "[0 TO 10] > [10 TO *]"      | new IntValue(source: dataSource, id: "[0 TO 10]", value: 4)  | new IntValue(source: dataSource, id: "[10 TO *]", value: 2)  | -1
        "[0 TO 10] < [10 TO 20]"     | new IntValue(source: dataSource, id: "[0 TO 10]", value: 4)  | new IntValue(source: dataSource, id: "[10 TO 20]", value: 2) | -1
        "[10 TO 20] > [0 TO 10]"     | new IntValue(source: dataSource, id: "[10 TO 20]", value: 4) | new IntValue(source: dataSource, id: "[0 TO 10]", value: 2)  | 1
        "[10 TO 20] > [10 TO 20]"    | new IntValue(source: dataSource, id: "[10 TO 20]", value: 4) | new IntValue(source: dataSource, id: "[10 TO 20]", value: 2) | 0

    }

    void "test Constructors #label"() {
        when:
        IntValue currentIntValue = intValue
        then:
        currentIntValue.getId() == expectedId
        currentIntValue.getSource().getName() == dataSourceName
        currentIntValue.getValue() == expectedValue
        where:
        label                               | intValue                        | expectedId           | dataSourceName | expectedValue
        "3 arg constructor"                 | new IntValue(parent, id, 2)     | id                   | name           | 2
        "3 arg constructor with datasource" | new IntValue(dataSource, id, 2) | id                   | name           | 2
        "2 arg constructor with datasource" | new IntValue(dataSource, id)    | id                   | name           | null
        "2 arg constructor"                 | new IntValue(parent, id)        | id                   | name           | null
        "1 arg constructor"                 | new IntValue(parent)            | "bard.core.IntValue" | name           | null
    }

    void "test Empty Constructors"() {
        when:
        IntValue currentIntValue = intValue
        then:
        currentIntValue.getId() == null
        currentIntValue.getValue() == null
        where:
        label                   | intValue
        "Empty arg constructor" | new IntValue()
    }

    void "test setters"() {
        given:
        IntValue currentIntValue = intValue
        when:
        currentIntValue.setValue(new Integer(2))
        then:
        currentIntValue.getValue() == 2
        where:
        label                   | intValue
        "Empty arg constructor" | new IntValue()
    }

}

