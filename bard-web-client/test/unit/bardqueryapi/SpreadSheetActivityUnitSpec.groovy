package bardqueryapi

import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.Readout
import molspreadsheet.SpreadSheetActivity
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class SpreadSheetActivityUnitSpec extends Specification {
    void "test readOutsToHillCurveValues"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        when:
        spreadSheetActivity.readOutsToHillCurveValues([], [])
        then:
        assert !spreadSheetActivity.hillCurveValueList
    }

    void "test readOutToHillCurveValue with empty Read outs"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        Readout readout = new Readout()
        List<String> resultTypeNames = []
        when:
        spreadSheetActivity.readOutToHillCurveValue(resultTypeNames, readout)
        then:
        assert !resultTypeNames
    }
    void "test readOutToHillCurveValue"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        Readout readout = new Readout(name: "name")
        List<String> resultTypeNames = []
        when:
        spreadSheetActivity.readOutToHillCurveValue(resultTypeNames, readout)
        then:
        assert resultTypeNames
    }

    void "test addPotency #label"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        when:
        spreadSheetActivity.addPotency(activity)
        then:
        assert (spreadSheetActivity.potency != null) == expected
        where:
        label             | activity                   | expected
        "With potency"    | new Activity(potency: "2") | true
        "Without potency" | new Activity()             | false
    }

    void "test addOutCome #label"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        when:
        spreadSheetActivity.addOutCome(activity)
        then:
        assert spreadSheetActivity.activityOutcome == expected
        where:
        label             | activity                 | expected
        "With outcome"    | new Activity(outcome: 2) | ActivityOutcome.ACTIVE
        "Without outcome" | new Activity()           | ActivityOutcome.UNSPECIFIED
    }
}
