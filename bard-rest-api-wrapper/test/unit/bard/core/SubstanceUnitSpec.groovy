package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SubstanceUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        Substance currentSubstance = substance
        then:
        assert currentSubstance.name == expectedName
        where:
        label         | substance             | expectedName
        "No arg ctor" | new Substance()       | null
        "1 arg ctor"  | new Substance("name") | "name"
    }


}

