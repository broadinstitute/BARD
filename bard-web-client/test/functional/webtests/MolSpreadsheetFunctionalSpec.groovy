package webtests

import webtests.pages.HomePage
import webtests.pages.ResultsPage
import webtests.pages.MolSpreadsheetPage

/**
 * Created with IntelliJ IDEA.
 * User: jlev
 * Date: 10/19/12
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadsheetFunctionalSpec extends BardFunctionalSpec {
    void setup() { // pre-condition of each test: user is logged in
        logInSomeUser()
    }

    def "Test creating a molecular spreadsheet from a single assay definition"() {
        given: "An assay definition in the Query Cart"
        to HomePage
        searchBox << "1617"
        searchButton.click()
        at ResultsPage
        waitFor(10, 0.5) { assaysTab.text().contains("(1)") }   // Wait for assay definitions to populate the tab
        addAssayToCart("1617")

        when: "The user chooses to visualize the experimental results from that assay definition in the Molecular Spreadsheet"
        assert queryCart.contentSummary.text() == "1 assay definition"
        queryCart.visualizeButton.click()
        queryCart.molSpreadsheetLink.click()

        then: "The Molecular Spreadsheet should appear with experimental results"
        at MolSpreadsheetPage
//        molSpreadsheetRows.each { println it.cid }
        assert molSpreadsheetRows(0)
    }
}
