package molspreadsheet

import bardqueryapi.ActivityOutcome
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 1/16/13
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetCellActivityOutcomeUnitSpec  extends Specification{
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test convert #label"() {


        when: "Call all the conversion routines for MolSpreadSheetCellActivityOutcome #label"
        final  MolSpreadSheetCellActivityOutcome molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.newMolSpreadSheetCellActivityOutcome(activityOutcome)

        then: "The resulting experimental value type should much the expected one"
        assert molSpreadSheetCellActivityOutcome == expectedMolSpreadSheetCellActivityOutcome

        where:
        label                   | activityOutcome                | expectedMolSpreadSheetCellActivityOutcome
        "Active"                | ActivityOutcome.ACTIVE         | MolSpreadSheetCellActivityOutcome.Active
        "Inactive"              | ActivityOutcome.INACTIVE       | MolSpreadSheetCellActivityOutcome.Inactive
        "Inconclusive"          | ActivityOutcome.INCONCLUSIVE   | MolSpreadSheetCellActivityOutcome.Inconclusive
        "Probe"                 | ActivityOutcome.PROBE          | MolSpreadSheetCellActivityOutcome.Probe
        "Unspecified"           | ActivityOutcome.UNSPECIFIED    | MolSpreadSheetCellActivityOutcome.Unspecified
        "uninitialized"         | null                           | MolSpreadSheetCellActivityOutcome.Unknown

    }

    void "MolSpreadSheetCellActivityOutcome smoke test"() {


        when: "We call the convert method"
        MolSpreadSheetCellActivityOutcome molSpreadSheetCellActivityOutcome = MolSpreadSheetCellActivityOutcome.Active

        then: "The resulting experimental value is completely unexpected, so throw an exception"
        assertNotNull molSpreadSheetCellActivityOutcome.color
        molSpreadSheetCellActivityOutcome.toString() == "Active"
    }
}
