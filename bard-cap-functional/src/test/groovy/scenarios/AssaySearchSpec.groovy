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

package scenarios
import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import spock.lang.Ignore
import base.BardFunctionalSpec

import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Assay
/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 */
@Ignore
class AssaySearchSpec extends BardFunctionalSpec {
	def testData = TestDataReader.getTestData()
	String AssayId = testData.AssayId
	String searchQuery = testData.assaySearchName
//	def assays = new Assay()
		
	def setup() {
		logInSomeUser()
	}

def "Test Search Assay By Assay Id"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		navigateTo(NavigateTo.ASSAY_BY_ID)

		then: "User is at Find Assay page"
		at CapSearchPage

		when: "User is trying to search some Assay"
		at CapSearchPage
		capSearchBy(SearchBy.ASSAY_ID, AssayId)

		then: "User is navigated to View Assay Definition page"
		at ViewAssayDefinitionPage

		when: "When user is at View Assay, Fetch summary info to validate"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(AssayId)

		then: "Validate Assay summary info with database "
		assert uiSummary.ADID.equalsIgnoreCase(dbSummary.ADID.toString())
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.ShortName.equalsIgnoreCase(dbSummary.ShortName)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy)
		assert uiSummary.DateCreated.equalsIgnoreCase(dbSummary.DateCreated)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		report "SearchAssayById"
	}

	def "Test Search Assay By Name"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		navigateTo(NavigateTo.ASSAY_BY_NAME)

		then: "User is at Find Assay page"
		at CapSearchPage

		when: "User is trying to search some Assay with Name"
		at CapSearchPage
		capSearchBy(SearchBy.ASSAY_NAME, searchQuery)
		def dbSearchCount = Assay.getAssaySearchCount(searchQuery)
		def uiSearchCount = searchResultCount()
		def dbSearchResult = Assay.getAssaySearchResults(searchQuery)
		def uiSearchResult = seachResults()
		
		then: "Validate the search result with db"
		assert uiSearchCount == dbSearchCount
		assert uiSearchResult.sort() == dbSearchResult.sort()

		report "SearchAssayByName"
	}
}
