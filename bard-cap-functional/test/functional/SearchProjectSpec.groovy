import pages.HomePage
import pages.FindProjectByIdPage
import pages.ViewProjectDefinitionPage
import pages.ScaffoldPage
import spock.lang.Stepwise
import pages.FindProjectByNamePage

@Stepwise
class SearchProjectSpec extends BardFunctionalSpec {
	final static ISEMPTY = "Empty"
	final static PROJECTRESULTACCORDIAN = "Project"
	
	String projectName = testData.projectName
	String projectId = testData.projectId
	String projectString = testData.projectSearchString
	String projectExactName = testData.projectExactName
	
	void setupSpec() {
		// pre-condition of each test: user is logged in
		logInSomeUser()
	}

	def "Test Find Project By Project Id"() {
		when: "User is navigating to Find Project page"
		at HomePage
		
		capHeaders.projectTab.click()
		capHeaders.projectChildTabs[0].click()

		then: "User is at Find Project page"
		at FindProjectByIdPage

		when: "User is trying to search some project"
		at FindProjectByIdPage
		projectSearchBtns.inputBtns << projectId
		projectSearchBtns.searchBtn.click()
		
		then: "User is navigated to View Project Definition page"
		at ViewProjectDefinitionPage
		assert viewProjectDefinition.contains(testData.projectId)
		
		
		when:"Navigating to Home Page"
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
		
	}
	
	def "Test Find Project By Exact Name"() {
		when: "User is navigating to Find Project page"
		at HomePage
		capHeaders.projectTab.click()
		capHeaders.projectChildTabs[1].click()

		then: "User is at Find Project page"
		at FindProjectByNamePage

		when: "User is trying to search some Project with Name"
		at FindProjectByNamePage
		projectSearchBtns.inputBtns << projectExactName
		projectSearchBtns.searchBtn.click()

		then: "User is navigated to view Project definition page"
		at ViewProjectDefinitionPage
		assert projectSummary.projectName
		projectSummary.projectName.next().text() ==~ projectExactName
		
		when:"User is at View Project definition page"
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Find Project By Name"() {
		when: "User is navigating to Find Project page"
		at HomePage
		capHeaders.projectTab.click()
		capHeaders.projectChildTabs[1].click()
		
		then: "User is at Find Project page"
		at FindProjectByNamePage

		when: "User is trying to search some Project with Name"
		at FindProjectByNamePage
		projectSearchBtns.inputBtns << projectName
		projectSearchBtns.searchBtn.click()

		then: "wait for result to populate in project result accordian"
		waitFor(10, 2) { projectResultAccordian.text().contains(PROJECTRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		
		when:"Navigating to Home Page"
		at FindProjectByNamePage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}

	def "Test Find Project By Name And Open Specifi Project Detail"() {
		when: "User is navigating to Find Project page"
		at HomePage
		capHeaders.projectTab.click()
		capHeaders.projectChildTabs[1].click()

		then: "User is at Find Project page"
		at FindProjectByNamePage

		when: "User is trying to search some Project with Name"
		at FindProjectByNamePage
		projectSearchBtns.inputBtns << projectName
		projectSearchBtns.searchBtn.click()

		then: "wait for result to populate in Project result accordian"
		waitFor(10, 2) { projectResultAccordian.text().contains(PROJECTRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		
		when:"Navigating to Home Page"
		at FindProjectByNamePage
		projectResults(0).assayId.click()
		then:
		at ViewProjectDefinitionPage
		
		when:
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Find Project via Autocomplete"() {
		when: "User is navigating to Find Project page"
		at HomePage
		capHeaders.projectTab.click()
		capHeaders.projectChildTabs[1].click()

		then: "User is at Find Project page"
		at FindProjectByNamePage

		when: "User is trying to search Project"
		at FindProjectByNamePage
		projectSearchBtns.inputBtns << projectString
		
		waitFor(15, 5) { projectAutocompleteItems }
		assert isAutocompleteListOk(projectAutocompleteItems, projectString)
		
		projectSearchBtns.searchBtn.click()

		then: "wait for result to populate in Project result accordian"
		waitFor(10, 2) { projectResultAccordian.text().contains(PROJECTRESULTACCORDIAN) }
		assert resultHolderTable.size()-1 != ISEMPTY
		
		when:"Navigating to Home Page"
		at FindProjectByNamePage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}

}