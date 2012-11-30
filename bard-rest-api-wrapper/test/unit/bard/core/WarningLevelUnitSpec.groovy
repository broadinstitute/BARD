package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.WarningLevel

@Unroll
class WarningLevelUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Get Description() #label"() {
        when: "We call the getDescription() method on the given #warningLevel"
        final String description = warningLevel.description
        then: "The expected to get back the description at that warning level"
        assert description
        description == expectedDescription
        where:
        label                   | expectedDescription                     | warningLevel
        "WarningLevel.none"     | "none (between 0.0 and 4000.0)"         | WarningLevel.none
        "WarningLevel.moderate" | "moderate (between 4000.0 and 10000.0)" | WarningLevel.moderate
        "WarningLevel.severe"   | "severe (> 10000.0)"                    | WarningLevel.severe
    }

    void "test Get WarningLevel #label"() {
        when: "We call the getWarningLevel() method on the given scaffold"
        final WarningLevel warningLevel = WarningLevel.getWarningLevel(pScore)
        then: "The expected to get back the expected warning level"
        assert warningLevel
        warningLevel == expectedWarningLevel
        where:
        label                 | pScore              | expectedWarningLevel
        "With pScore=0"       | new Double(0)       | WarningLevel.none
        "With pScore=3999.99" | new Double(399.99)  | WarningLevel.none
        "With pScore=4000"    | new Double(4000)    | WarningLevel.moderate
        "With pScore=9999.99" | new Double(9999.99) | WarningLevel.moderate
        "With pScore=10000"   | new Double(10000)   | WarningLevel.severe
        "With pScore=20000"   | new Double(20000)   | WarningLevel.severe
    }

}

