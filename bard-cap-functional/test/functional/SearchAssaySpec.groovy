import pages.HomePage
import pages.FindAssayByIdPage
import pages.ViewAssayDefinitionPage
import pages.ScaffoldPage
import spock.lang.Stepwise
import pages.FindAssayByNamePage

@Stepwise
class SearchAssaySpec extends BardFunctionalSpec {
	final static ISEMPTY = "Empty"
	final static ASSAYRESULTACCORDIAN = "Assays"
	
	String assayName = testData.assayName
	String assayId = testData.assayId
	String assayString = testData.assaySearchString
	String assayExactName = testData.assayExactName
	
	void setupSpec() {
		// pre-condition of each test: user is logged in
		logInSomeUser()
		
	}

	def "Test Find Assay By Assay Definition Id"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		
		capHeaders.assayTab.click()
		capHeaders.assayChildTabs[0].click()

		then: "User is at Find Assay page"
		at FindAssayByIdPage

		when: "User is trying to search some Assays"
		at FindAssayByIdPage
		assaySearchBtns.inputBtns << assayId
		assaySearchBtns.searchBtn.click()

		then: "User is navigated to View Assay Definition page"
		at ViewAssayDefinitionPage
		assert viewAssayDefinition.contains(testData.assayId)
		assert assaySummary.value[0].text() ==~ assayId
		
		when:"Navigating to Home Page"
		at ViewAssayDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
		
	}
	
	def "Test Find Assay By Exact Name"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		capHeaders.assayTab.click()
		capHeaders.assayChildTabs[1].click()

		then: "User is at Find Assay page"
		at FindAssayByNamePage

		when: "User is trying to search some Assays with Name"
		at FindAssayByNamePage
		assaySearchBtns.inputBtns << assayExactName
		assaySearchBtns.searchBtn.click()

		then: "User is navigated to view assay definition page"
		at ViewAssayDefinitionPage
		assert assaySummary.value[3].text() ==~ assayExactName
		
		when:"User is at View assay definition page"
		at ViewAssayDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Find Assay By Name"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		capHeaders.assayTab.click()
		capHeaders.assayChildTabs[1].click()

		then: "User is at Find Assay page"
		at FindAssayByNamePage

		when: "User is trying to search some Assays with Name"
		at FindAssayByNamePage
		assaySearchBtns.inputBtns << assayName
		assaySearchBtns.searchBtn.click()
		
		then: "wait for result to populate in assay result accordian"
		waitFor(10, 2) { assayResultAccordian.text().contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		
		when:"Navigating to Home Page"
		at FindAssayByNamePage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}

	def "Test Find Assay By Name And Open Specifi Assay Detail"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		capHeaders.assayTab.click()
		capHeaders.assayChildTabs[1].click()

		then: "User is at Find Assay page"
		at FindAssayByNamePage

		when: "User is trying to search some Assays with Name"
		at FindAssayByNamePage
		assaySearchBtns.inputBtns << assayName
		assaySearchBtns.searchBtn.click()

		then: "wait for result to populate in assay result accordian"
		waitFor(10, 2) { assayResultAccordian.text().contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		
		when:"Navigating to Home Page"
		at FindAssayByNamePage
		assaysResults(0).assayId.click()
		then:
		at ViewAssayDefinitionPage
		
		when:
		at ViewAssayDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Find Assay via Autocomplete"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		capHeaders.assayTab.click()
		capHeaders.assayChildTabs[1].click()

		then: "User is at Find Assay page"
		at FindAssayByNamePage

		when: "User is trying to search Assays"
		at FindAssayByNamePage
		assaySearchBtns.inputBtns << assayString
		
		waitFor(15, 5) { autocompleteItems }
		assert isAutocompleteListOk(autocompleteItems, assayString)
		
		assaySearchBtns.searchBtn.click()

		then: "wait for result to populate in assay result accordian"
		waitFor(10, 2) { assayResultAccordian.text().contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size()-1 != ISEMPTY
		
		when:"Navigating to Home Page"
		at FindAssayByNamePage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}

}