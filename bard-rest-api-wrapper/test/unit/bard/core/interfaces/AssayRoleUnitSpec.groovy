package bard.core.interfaces

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayRoleUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test valueOf #label"() {

        when:
        final AssayRole foundAssayRole = AssayRole.valueOf(index)
        then:
        assert foundAssayRole == assayRole

        where:
        label                    | assayRole                       | index
        "Other Type"             | AssayRole.Primary               | 0
        "Screening Type"         | AssayRole.Counterscreen         | 1
        "Secondary Confirmation" | AssayRole.SecondaryConfirmation | 2
        "Secondary  Alternative" | AssayRole.SecondaryAlternative  | 3
        "Secondary Orthogonal"   | AssayRole.SecondaryOrthogonal   | 4
        "Secondary Selectivity"  | AssayRole.SecondarySelectivity  | 5
    }

    void "test valueOf IllegalArgument Exception"() {

        when:
        AssayRole.valueOf(6)
        then:
        thrown(IllegalArgumentException)

    }


}

