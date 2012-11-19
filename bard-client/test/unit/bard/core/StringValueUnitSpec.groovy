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

