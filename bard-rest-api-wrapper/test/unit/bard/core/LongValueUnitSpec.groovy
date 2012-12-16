package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class LongValueUnitSpec extends Specification {
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
        LongValue currentLongValue = longValue
        then:
        currentLongValue.getId() == expectedId
        currentLongValue.getSource().getName() == dataSourceName
        currentLongValue.getValue() == expectedValue
        where:
        label                               | longValue                        | expectedId            | dataSourceName | expectedValue
        "3 arg constructor"                 | new LongValue(parent, id, 2)     | id                    | name           | 2
        "3 arg constructor with datasource" | new LongValue(dataSource, id, 2) | id                    | name           | 2
        "2 arg constructor with datasource" | new LongValue(dataSource, id)    | id                    | name           | null
        "2 arg constructor"                 | new LongValue(parent, id)        | id                    | name           | null
        "1 arg constructor"                 | new LongValue(parent)            | "bard.core.LongValue" | name           | null
    }

    void "test compareTo #label"() {
        when:
        int foundValue = longValue1.compareTo(longValue2)
        then:
        assert foundValue == expectedValue
        where:
        label                        | longValue1                                            | longValue2                                            | expectedValue
        "value1==value2"             | new LongValue(source: dataSource, id: id, value: 2)   | new LongValue(source: dataSource, id: id, value: 2)   | 0
        "value1==value2 compare ids" | new LongValue(source: dataSource, id: id, value: 2)   | new LongValue(source: dataSource, id: "JD", value: 2) | -1
        "value1 > value2"            | new LongValue(source: dataSource, id: id, value: 2)   | new LongValue(source: dataSource, id: "JD", value: 4) | 1
        "Value1 < value2"            | new LongValue(source: dataSource, id: "JD", value: 4) | new LongValue(source: dataSource, id: id, value: 2)   | -1

    }

    void "test Empty Constructors"() {
        when:
        LongValue currentLongValue = longValue
        then:
        currentLongValue.getId() == null
        currentLongValue.getValue() == null
        where:
        label                   | longValue
        "Empty arg constructor" | new LongValue()
    }

    void "test setters"() {
        given:
        LongValue currentLongValue = longValue
        when:
        currentLongValue.setValue(new Long(2))
        then:
        currentLongValue.getValue() == 2
        where:
        label                   | longValue
        "Empty arg constructor" | new LongValue()
    }

}

