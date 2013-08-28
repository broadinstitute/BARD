package bardqueryapi

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
class ActivityOutcomeUnitSpec extends Specification {
    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test getLabel"() {
        when:
        final String returnedLabel = activityOutcome.label
        then:
        assert returnedLabel == expectedLabel
        where:
        label          | activityOutcome              | expectedLabel
        "Active"       | ActivityOutcome.ACTIVE       | "Active"
        "Inactive"     | ActivityOutcome.INACTIVE     | "Inactive"
        "Inconclusive" | ActivityOutcome.INCONCLUSIVE | "Inconclusive"
        "Probe"        | ActivityOutcome.PROBE        | "Probe"
        "Unspecified"  | ActivityOutcome.UNSPECIFIED  | "Unspecified"
    }

    /**
     * {@link ActivityOutcome#findActivityOutcome(int)}
     */
    void "test findActivityOutcome"() {
        when:
        final ActivityOutcome returnedActivityOutcome =ActivityOutcome.findActivityOutcome(pubChemValue)
        then:
        assert returnedActivityOutcome == activityOutcome
        where:
        label          | activityOutcome              | pubChemValue
        "Active"       | ActivityOutcome.ACTIVE       | 2
        "Inactive"     | ActivityOutcome.INACTIVE     | 1
        "Inconclusive" | ActivityOutcome.INCONCLUSIVE | 3
        "Probe"        | ActivityOutcome.PROBE        | 5
        "Unspecified"  | ActivityOutcome.UNSPECIFIED  | 4
    }
    /**
     * {@link ActivityOutcome#getPubChemValue()}
     */
    void "test getPubChemValue"() {
        given:
        when:
        final int pubChemValue = activityOutcome.pubChemValue
        then:
        assert pubChemValue == expectedPubChemValue
        where:
        label          | activityOutcome              | expectedPubChemValue
        "Active"       | ActivityOutcome.ACTIVE       | 2
        "Inactive"     | ActivityOutcome.INACTIVE     | 1
        "Inconclusive" | ActivityOutcome.INCONCLUSIVE | 3
        "Probe"        | ActivityOutcome.PROBE        | 5
        "Unspecified"  | ActivityOutcome.UNSPECIFIED  | 4
    }
}
