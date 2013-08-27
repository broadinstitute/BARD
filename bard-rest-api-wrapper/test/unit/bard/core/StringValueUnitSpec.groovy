package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class StringValueUnitSpec extends Specification {
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
        int foundValue = stringValue1.compareTo(stringValue2)
        then:
        assert foundValue == expectedValue
        where:
        label                        | stringValue1                                              | stringValue2                                              | expectedValue
        "value1==value2"             | new StringValue(source: dataSource, id: id, value: "2")   | new StringValue(source: dataSource, id: id, value: "2")   | 0
        "value1==value2 compare ids" | new StringValue(source: dataSource, id: id, value: "2")   | new StringValue(source: dataSource, id: "JD", value: "2") | -1
        "value1 > value2"            | new StringValue(source: dataSource, id: id, value: "2")   | new StringValue(source: dataSource, id: "JD", value: "4") | 2
        "Value1 < value2"            | new StringValue(source: dataSource, id: "JD", value: "4") | new StringValue(source: dataSource, id: id, value: "2")   | -2

    }

    void "test Constructors #label"() {
        when:
        StringValue currentStringValue = stringValue
        then:
        currentStringValue.getId() == expectedId
        currentStringValue.getSource().getName() == dataSourceName
        currentStringValue.getValue() == expectedValue
        where:
        label                               | stringValue                          | expectedId              | dataSourceName | expectedValue
        "3 arg constructor"                 | new StringValue(parent, id, "2")     | id                      | name           | "2"
        "3 arg constructor with datasource" | new StringValue(dataSource, id, "2") | id                      | name           | "2"
        "2 arg constructor with datasource" | new StringValue(dataSource, id)      | id                      | name           | null
        "2 arg constructor"                 | new StringValue(parent, id)          | id                      | name           | null
        "1 arg constructor"                 | new StringValue(parent)              | "bard.core.StringValue" | name           | null
    }

    void "test Empty Constructors"() {
        when:
        StringValue currentStringValue = stringValue
        then:
        currentStringValue.getId() == null
        currentStringValue.getValue() == null
        where:
        label                   | stringValue
        "Empty arg constructor" | new StringValue()
    }

    void "test setters"() {
        given:
        StringValue currentStringValue = stringValue
        when:
        currentStringValue.setValue("someValue")
        then:
        currentStringValue.getValue() == "someValue"
        where:
        label                   | stringValue
        "Empty arg constructor" | new StringValue()
    }

}

