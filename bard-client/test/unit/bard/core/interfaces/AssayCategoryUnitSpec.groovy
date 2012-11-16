package bard.core.interfaces

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayCategoryUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test valueOf #label"() {

        when:
        final AssayCategory foundAssayCategory = AssayCategory.valueOf(index)
        then:
        assert foundAssayCategory == assayCategory
        where:
        label                     | assayCategory          | index
        "Unknown"                 | AssayCategory.Unknown  | 0
        "MLSCN"                   | AssayCategory.MLSCN    | 1
        "MLPCN"                   | AssayCategory.MLPCN    | 2
        "MLSCN_AP"                | AssayCategory.MLSCN_AP | 3
        "MLPCN AP"                | AssayCategory.MLPCN_AP | 4
        "Some non-existing Index" | AssayCategory.Unknown  | 5
    }
}

