package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import spock.lang.Ignore
import base.BardFunctionalSpec

import common.Constants.NavigateTo
import common.Constants.SearchBy
import common.TestDataReader;

import db.Project

class ProjectSearchSpec extends BardFunctionalSpec{
	def testData = TestDataReader.getTestData()
	String projectId = testData.ProjectID
	String projectSearchName = testData.projectSearchName
	
	def setup() {
		logInSomeUser()
	}

	def "Test Search Project By Project Id"() {
		when: "User is navigating to Find Project page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_ID)

		then: "User is at Find Project page"
		at CapSearchPage

		when: "User is trying to search some project"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_ID, projectId)

		then: "User is navigated to View Project Definition page"
		at ViewProjectDefinitionPage

		when: "When user is at View Project, Fetch summary info to validate"
		at ViewProjectDefinitionPage
		def uiSummaryInfo = getUISummaryInfo()
		def dbSummaryInfo = Project.getProjectSummaryById(projectId)

		then: "Validate project summary info with database "
		assert uiSummaryInfo.PID.equalsIgnoreCase(dbSummaryInfo.PID.toString())
		assert uiSummaryInfo.Status.equalsIgnoreCase(dbSummaryInfo.Status)
		assert uiSummaryInfo.Name.equalsIgnoreCase(dbSummaryInfo.Name)
		assert uiSummaryInfo.Description.equalsIgnoreCase(dbSummaryInfo.Description.toString())
		assert uiSummaryInfo.DateCreated.equalsIgnoreCase(dbSummaryInfo.DateCreated)
		assert uiSummaryInfo.LastUpdated.equalsIgnoreCase(dbSummaryInfo.LastUpdated)

		report "SearchProjectById"
	}

	def "Test Search Project By Name"() {
		when: "User is navigating to Find Project page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_NAME)

		then: "User is at Find Project page"
		at CapSearchPage

		when: "User is trying to search some Project with Name"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_NAME, projectSearchName)
		def dbSearchCount = Project.getProjectSearchCount(projectSearchName)
		def uiSearchCount = searchResultCount()
		def dbSearchResult = Project.getProjectSearchResults(projectSearchName)
		def uiSearchResult = seachResults()
		
		then: "Validate the search result with db"
		assert uiSearchCount == dbSearchCount
		assert uiSearchResult.sort() == dbSearchResult.sort()

		report "SearchProjectByName"
	}
}