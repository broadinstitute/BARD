package bard.core.interfaces

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentRoleUnitSpec extends Specification {

    void "test valueOf #label"() {

        when:
        final ExperimentRole foundExperimentRole = ExperimentRole.valueOf(index)
        then:
        assert foundExperimentRole == experimentRole

        where:
        label                    | experimentRole                       | index
        "Other Type"             | ExperimentRole.Primary               | 0
        "Screening Type"         | ExperimentRole.Counterscreen         | 1
        "Secondary Confirmation" | ExperimentRole.SecondaryConfirmation | 2
        "Secondary  Alternative" | ExperimentRole.SecondaryAlternative  | 3
        "Secondary Orthogonal"   | ExperimentRole.SecondaryOrthogonal   | 4
        "Secondary Selectivity"  | ExperimentRole.SecondarySelectivity  | 5
    }

    void "test valueOf IllegalArgument Exception"() {

        when:
        ExperimentRole.valueOf(6)
        then:
        thrown(IllegalArgumentException)

    }


}

