package scenarios
import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import base.BardFunctionalSpec

import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Assay

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