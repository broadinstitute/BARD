package bard.core.interfaces

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayTypeUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test valueOf #label"() {

        when:
        final AssayType foundAssayType = AssayType.valueOf(index)
        then:
        assert foundAssayType == assayType

        where:
        label               | assayType              | index
        "Other Type"        | AssayType.Other        | 0
        "Screening Type"    | AssayType.Screening    | 1
        "Confirmatory Type" | AssayType.Confirmatory | 2
        "Summary Type"      | AssayType.Summary      | 3
    }

    void "test valueOf IllegalArgument Exception"() {

        when:
        AssayType.valueOf(4)
        then:
        thrown(IllegalArgumentException)

    }


}

