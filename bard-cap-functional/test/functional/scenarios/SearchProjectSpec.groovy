package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec

import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Project

class SearchProjectSpec extends BardFunctionalSpec{
	final static String PROJECT_RESULT_ACCORDIAN = "Project"
	final static index_1 = 1
	final static index_0 = 0
	String projectName = testData.projectName
	String projectId = testData.projectId
	String projectString = testData.projectSearchString
	String projectExactName = testData.projectExactName
	def projects = new Project()
	def setup() {
		logInSomeUser()
	}

	def "Test Find Project By Project Id"() {
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
		def dbSummaryInfo = projects.getProjectSummaryById(projectId)
		
		then: "Validate project summary info with database "
		assert uiSummaryInfo.PID.equalsIgnoreCase(dbSummaryInfo.PID.toString())
		assert uiSummaryInfo.Status.equalsIgnoreCase(dbSummaryInfo.Status)
		assert uiSummaryInfo.Name.equalsIgnoreCase(dbSummaryInfo.Name)
		assert uiSummaryInfo.Description.equalsIgnoreCase(dbSummaryInfo.Description.toString())
		assert uiSummaryInfo.DateCreated.equalsIgnoreCase(dbSummaryInfo.DateCreated)
		assert uiSummaryInfo.LastUpdated.equalsIgnoreCase(dbSummaryInfo.LastUpdated)

		report "FindProjectById"

	}

	/*	def "Test Find Project By Exact Name"() {
	 when: "User is navigating to Find Project page"
	 at HomePage
	 navigateToFindProjectByName()
	 then: "User is at Find Project page"
	 at FindProjectByNamePage
	 when: "User is trying to search some Project with Name"
	 at FindProjectByNamePage
	 searchProjectByName(projectExactName)
	 then: "User is navigated to view Project definition page"
	 at ViewProjectDefinitionPage
	 when: "When user View Project, Fetch summary info to validate"
	 at ViewProjectDefinitionPage
	 def uiSummaryInfo = getUISummaryInfo()
	 def dbSummaryInfo = projects.getProjectSummaryById(projectExactName)
	 then: "Validate project summary info with database "
	 assert uiSummaryInfo.join(', ').equalsIgnoreCase(dbSummaryInfo.join(', '))
	 report "FindProjectByExactName"
	 }
	 def "Test Find Project By Name"() {
	 when: "User is navigating to Find Project page"
	 at HomePage
	 navigateToFindProjectByName()
	 then: "User is at Find Project page"
	 at FindProjectByNamePage
	 when: "User is trying to search some Project with Name"
	 at FindProjectByNamePage
	 searchProjectByName(projectName)
	 def searchCount = projects.getProjectSearchCount(projectName)
	 then: "wait for result to populate in project result accordian"
	 def accordianText = projectResultAccordian.text()
	 waitFor(WAIT_INTERVAL, R_INTERVAL) { accordianText.contains(PROJECT_RESULT_ACCORDIAN) }
	 def resultCount = accordianText.subSequence(accordianText.indexOf('(')+index_1, accordianText.indexOf(')'))
	 assert resultHolderTable.size().toString() == resultCount
	 assert resultHolderTable.size() != IS_EMPTY
	 assert resultHolderTable.size() == searchCount
	 report "FindProjectByName"
	 }
	 def "Test Find Project By Name And Open Specifi Project Detail"() {
	 when: "User is navigating to Find Project page"
	 at HomePage
	 navigateToFindProjectByName()
	 then: "User is at Find Project page"
	 at FindProjectByNamePage
	 when: "User is trying to search some Project with Name"
	 at FindProjectByNamePage
	 searchProjectByName(projectName)
	 def searchCount = projects.getProjectSearchCount(projectName)
	 then: "wait for result to populate in Project result accordian"
	 def accordianText = projectResultAccordian.text()
	 waitFor(WAIT_INTERVAL, R_INTERVAL) { accordianText.contains(PROJECT_RESULT_ACCORDIAN) }
	 def resultCount = accordianText.subSequence(accordianText.indexOf('(')+index_1, accordianText.indexOf(')'))
	 assert resultHolderTable.size().toString() == resultCount
	 assert resultHolderTable.size() != IS_EMPTY
	 assert resultHolderTable.size() == searchCount
	 report "FindProjecByName"
	 when: "Open specific project details"
	 at FindProjectByNamePage
	 projectResults(index_0).idFiled.click()
	 then: "User is at project details page"
	 at ViewProjectDefinitionPage
	 report "OpenProjectFromSearchResult"
	 }
	 def "Test Find Project via Autocomplete"() {
	 when: "User is navigating to Find Project page"
	 at HomePage
	 navigateToFindProjectByName()
	 then: "User is at Find Project page"
	 at FindProjectByNamePage
	 when: "User is trying to search Project"
	 at FindProjectByNamePage
	 searchInput.fieldButton << projectString
	 def searchCount = projects.getProjectSearchCount(projectString)
	 waitFor(WAIT_INTERVAL, R_INTERVAL) { projectAutocompleteItems.size() > index_1 }
	 CapFunctionalPage autoList = new CapFunctionalPage()
	 assert autoList.isAutocompleteListOk(projectAutocompleteItems, projectString)
	 searchBtns.fieldButton.click()
	 report "AutoSuggestResult"
	 then: "Wait for result to populate in Project result accordian"
	 def accordianText = projectResultAccordian.text()
	 waitFor(WAIT_INTERVAL, R_INTERVAL) { accordianText.contains(PROJECT_RESULT_ACCORDIAN) }
	 assert resultHolderTable.size() != IS_EMPTY
	 def resultCount = accordianText.subSequence(accordianText.indexOf('(')+index_1, accordianText.indexOf(')'))
	 assert resultHolderTable.size().toString() == resultCount
	 assert resultHolderTable.size() == searchCount
	 report "FindProjectByAutoSuggest"
	 }
	 */
	def cleanup() {
		if(isLoggedIn()) {
			logout()
		}
		assert !isLoggedIn()
	}
}