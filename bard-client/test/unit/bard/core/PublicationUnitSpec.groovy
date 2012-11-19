package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class PublicationUnitSpec extends Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }


    void "test Constructors #label"() {
        when:
        Publication currentPublication = publication
        then:
        assert currentPublication.name == expectedName
        where:
        label         | publication             | expectedName
        "No arg ctor" | new Publication()       | null
        "1 arg ctor"  | new Publication("name") | "name"
    }


}

