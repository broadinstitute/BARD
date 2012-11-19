package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BinaryValueUnitSpec extends Specification {
    @Shared DataSource dataSource = new DataSource("name", "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)
    @Shared byte[] byteValue = id.getBytes()
    @Shared int offset = 0
    @Shared int size = 0

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }
    void "test default Constructors"() {
        when:
        BinaryValue currentBinaryValue = new BinaryValue()
        then:
        assert currentBinaryValue.getValue() == null
      }
    void "test Constructors #label"() {
        when:
        BinaryValue currentBinaryValue = binaryValue
        currentBinaryValue.setURL("url")
        then:
        currentBinaryValue.getURL() == "url"
        currentBinaryValue.offset() == offset
        currentBinaryValue.size() == expectedSize
        where:
        label                               | binaryValue                                              | expectedSize
        "5 arg constructor"                 | new BinaryValue(dataSource, id, byteValue, offset, size) | 2
        "3 arg constructor with datasource" | new BinaryValue(dataSource, id, byteValue)               | 2
        "3 arg constructor"                 | new BinaryValue(parent, id, byteValue)                   | 2
        "2 arg constructor with datasource" | new BinaryValue(dataSource, id)                          | 0
        "2 arg constructor"                 | new BinaryValue(parent, id)                              | 0
        "1 arg constructor"                 | new BinaryValue(parent)                                  | 0
    }


}

