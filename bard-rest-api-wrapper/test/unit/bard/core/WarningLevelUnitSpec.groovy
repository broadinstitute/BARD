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
        label                   | expectedDescription                  | warningLevel
        "WarningLevel.none"     | "none (between 0.0 and 100.0)"       | WarningLevel.none
        "WarningLevel.moderate" | "moderate (between 100.0 and 300.0)" | WarningLevel.moderate
        "WarningLevel.severe"   | "severe (> 300.0)"                   | WarningLevel.severe
    }

    void "test Get WarningLevel #label"() {
        when: "We call the getWarningLevel() method on the given scaffold"
        final WarningLevel warningLevel = WarningLevel.getWarningLevel(pScore)
        then: "The expected to get back the expected warning level"
        assert warningLevel
        warningLevel == expectedWarningLevel
        where:
        label                | pScore             | expectedWarningLevel
        "With pScore=0"      | new Double(0)      | WarningLevel.none
        "With pScore=99.99"  | new Double(99.99)  | WarningLevel.none
        "With pScore=100"    | new Double(100)    | WarningLevel.moderate
        "With pScore=299.99" | new Double(299.99) | WarningLevel.moderate
        "With pScore=300"    | new Double(300)    | WarningLevel.severe
        "With pScore=20000"  | new Double(20000)  | WarningLevel.severe
    }

}

