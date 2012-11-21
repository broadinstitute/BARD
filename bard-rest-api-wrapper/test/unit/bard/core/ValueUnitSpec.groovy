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
        Value value = new Value(dataSource,id)
        Value child = new Value(dataSource,"someOtherId")
        when:
        value.addValue(child)
        then:
        assert value.getChildCount() == 1
        assert !value.getChildren(id).isEmpty()
        assert value.getChild(id)
        assert !value.getChildren().isEmpty()
    }
}

