package bard.core.interfaces

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentTypeUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test valueOf #label"() {

        when:
        final ExperimentType foundExperimentType = ExperimentType.valueOf(index)
        then:
        assert foundExperimentType == experimentType

        where:
        label               | experimentType              | index
        "Other Type"        | ExperimentType.Other        | 0
        "Screening Type"    | ExperimentType.Screening    | 1
        "Confirmatory Type" | ExperimentType.Confirmatory | 2
        "Summary Type"      | ExperimentType.Summary      | 3
    }

    void "test valueOf IllegalArgument Exception"() {

        when:
        ExperimentType.valueOf(4)
        then:
        thrown(IllegalArgumentException)

    }


}

