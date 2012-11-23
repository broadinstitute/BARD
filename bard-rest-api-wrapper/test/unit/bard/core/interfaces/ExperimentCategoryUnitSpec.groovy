package bard.core.interfaces

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentCategoryUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test valueOf #label"() {

        when:
        final ExperimentCategory foundExperimentCategory = ExperimentCategory.valueOf(index)
        then:
        assert foundExperimentCategory == experimentCategory
        where:
        label                     | experimentCategory          | index
        "Unknown"                 | ExperimentCategory.Unknown  | 0
        "MLSCN"                   | ExperimentCategory.MLSCN    | 1
        "MLPCN"                   | ExperimentCategory.MLPCN    | 2
        "MLSCN_AP"                | ExperimentCategory.MLSCN_AP | 3
        "MLPCN AP"                | ExperimentCategory.MLPCN_AP | 4
        "Some non-existing Index" | ExperimentCategory.Unknown  | 5
    }
}

