package webtests

import webtests.pages.HomePage
import webtests.pages.ResultsPage
import webtests.pages.MolSpreadsheetPage
import spock.lang.Shared

/**
 * Created with IntelliJ IDEA.
 * User: jlev
 * Date: 10/19/12
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadsheetFunctionalSpec extends BardFunctionalSpec {
    @Shared String assayId = "1617"
    @Shared String compoundId = "5389617"

    void setup() { // pre-condition of each test: user is logged in
        logInSomeUser()
    }

    def "Test creating a molecular spreadsheet from a single assay definition"() {
        given: "An assay definition in the Query Cart"
        to HomePage
        searchBox << "ADID:$assayId"
        searchButton.click()
        at ResultsPage
        waitFor(20, 0.5) { assaysTab.text().contains("1") }   // Wait for assay definitions to populate the tab
        addAssayToCart(assayId)

        when: "The user chooses to visualize the experimental results from that assay definition in the Molecular Spreadsheet"
        assert queryCart.assayDefContentSummary.text() == "1 assay definition"
        queryCart.visualizeButton.click()
        queryCart.molSpreadsheetLink.click()

        then: "The Molecular Spreadsheet should not appear -- instead it should request that the user enter a compound"
        at MolSpreadsheetPage
        waitFor(10, 0.5){ $("div#molecularSpreadSheet") }
        waitFor(3, 0.5){ $("div.alert") }
        assert checkFor($("div.alert"), "Cannot display molecular spreadsheet without at least one compound")
    }

    def "Test creating a molecular spreadsheet from a single assay definition and a single compound"() {
        given: "An assay definition and a compound in the Query Cart"
        to HomePage
        // Add the assay definition
        searchBox = "ADID:$assayId"
        searchButton.click()
        at ResultsPage
        waitFor(20, 0.5) { assaysTab.text().contains("1") }   // Wait for assay definitions to populate the tab
        addAssayToCart(assayId)

        // Add the compound
        searchBox = "CID:$compoundId"
        searchButton.click()
        at ResultsPage
        waitFor(20, 0.5) { compoundsTab.text().contains("1")}  // Wait for compounds to populate the tab
        addCompoundToCart(compoundId)

        when: "The user chooses to visualize the experimental results from that assay definition in the Molecular Spreadsheet"
        assert queryCart.assayDefContentSummary.text().contains("1 assay definition")
        assert queryCart.compoundContentSummary.text().contains("1 compound")
        queryCart.visualizeButton.click()
        queryCart.molSpreadsheetLink.click()

        then: "The Molecular Spreadsheet should appear with experimental results"
        at MolSpreadsheetPage
        waitFor(10, 0.5){ $("div#molecularSpreadSheet") }
        assert molSpreadsheetRows(0)
//        assert molSpreadsheetRows(0).cid == compoundId
    }

}
