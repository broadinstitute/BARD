/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package webtests

import spock.lang.Shared
import webtests.pages.HomePage
import webtests.pages.MolSpreadsheetPage
import webtests.pages.ResultsPage

/**
 * Created with IntelliJ IDEA.
 * User: jlev
 * Date: 10/19/12
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadsheetFunctionalSpec extends BardFunctionalSpec {
    @Shared String assayId = "44"
    @Shared String capassayId = "5499"
    @Shared String compoundId = "53377439"

    void setup() { // pre-condition of each test: user is logged in
        logInSomeUser()
    }

    def "Test creating a molecular spreadsheet from a single assay definition"() {
        given: "An assay definition in the Query Cart"
        to HomePage
        searchBox << "ADID:$capassayId"
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
        waitFor(20, 0.5){ $("div#molecularSpreadSheet") }
        waitFor(20, 0.5){ $("div.alert") }
        assert checkFor($("div.alert"), "Cannot display molecular spreadsheet without at least one compound")
    }

    def "Test creating a molecular spreadsheet from a single assay definition and a single compound"() {
        given: "An assay definition and a compound in the Query Cart"
        to HomePage
        // Add the assay definition
        searchBox = "ADID:$capassayId"
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
        waitFor(20, 0.5){ $("div#molecularSpreadSheet") }
        assert molSpreadsheetRows(0)
//        assert molSpreadsheetRows(0).cid == compoundId
    }

}
