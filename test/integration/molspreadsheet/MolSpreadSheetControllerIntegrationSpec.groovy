package molspreadsheet

import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before

import spock.lang.Unroll

@Unroll
class MolSpreadSheetControllerIntegrationSpec extends IntegrationSpec {

    MolSpreadSheetController molSpreadSheetController = new MolSpreadSheetController()
    MolecularSpreadSheetService molecularSpreadSheetService

    @Before
    void setup() {
    }

    @After
    void tearDown() {
    }

    void "test showExperimentDetails() #label"() {

        when: "we generate a molecular spreadsheet for one probe and then another probe"
        molSpreadSheetController.showExperimentDetails(firstPID, firstCID)
        Boolean firstDataReturnedSomething = molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()
        molSpreadSheetController.showExperimentDetails(secondPID, secondCID)
        Boolean secondDataReturnedSomething = molecularSpreadSheetService.weHaveEnoughDataToMakeASpreadsheet()

        then: "we should be able to get EnoughDataToMakeASpreadsheet"
        assert firstDataReturnedSomething == firstExpectedDataNotEmpty
        assert secondDataReturnedSomething == secondExpectedDataNotEmpty

        where:
        label                                | firstPID | firstCID | firstExpectedDataNotEmpty | secondPID | secondCID | secondExpectedDataNotEmpty
        'using two different probes'         | 17       | 9795907  | true                      | 15        | 1512045   | true
        'using the same probe consecutively' | 17       | 9795907  | true                      | 17        | 9795907   | true
    }
}
