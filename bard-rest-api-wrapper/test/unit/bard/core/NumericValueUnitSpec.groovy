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

