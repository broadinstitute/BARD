import pages.HomePage
import pages.FindAssayByIdPage
import pages.ViewAssayDefinitionPage
import pages.ScaffoldPage
import spock.lang.Stepwise
import pages.FindAssayByNamePage
import db.Assay

@Stepwise
class SearchAssaySpec extends BardFunctionalSpec {
	final static ISEMPTY = "Empty"
	final static ASSAYRESULTACCORDIAN = "Assays"
	
	String assayName = testData.assayName
	String assayId = testData.assayId
	String assayString = testData.assaySearchString
	String assayExactName = testData.assayExactName
	def assays = new Assay()
	def summaryData = [:]
	
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
		summaryData = assays.getAssaySummaryById(assayId)
		
		then: "User is navigated to View Assay Definition page"
		at ViewAssayDefinitionPage
		assert viewAssayDefinition.contains(testData.assayId)
		assert assaySummary.ddValue[0].text() ==~ assayId
		assert assaySummary.ddValue[1].text().equalsIgnoreCase(summaryData.assayVersion)
		assert assaySummary.ddValue[2].text().equalsIgnoreCase(summaryData.shortName)
		assert assaySummary.ddValue[3].text().equalsIgnoreCase(summaryData.assayName)
		assert assaySummary.ddValue[4].text().equalsIgnoreCase(summaryData.designedBy)
		assert assaySummary.ddValue[5].text().equalsIgnoreCase(summaryData.assayStatus)
		assert assaySummary.ddValue[6].text().equalsIgnoreCase(summaryData.assayType)
		report "FindAssayById"
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
		summaryData = assays.getAssaySummaryByName(assayExactName)
		
		then: "User is navigated to view assay definition page"
		at ViewAssayDefinitionPage
		assert assaySummary.ddValue[3].text() ==~ assayExactName
		
		assert assaySummary.ddValue[1].text().equalsIgnoreCase(summaryData.assayVersion)
		assert assaySummary.ddValue[2].text().equalsIgnoreCase(summaryData.shortName)
		assert assaySummary.ddValue[3].text().equalsIgnoreCase(summaryData.assayName)
		assert assaySummary.ddValue[4].text().equalsIgnoreCase(summaryData.designedBy)
		assert assaySummary.ddValue[5].text().equalsIgnoreCase(summaryData.assayStatus)
		assert assaySummary.ddValue[6].text().equalsIgnoreCase(summaryData.assayType)
		report "FindAssayByExactName"
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
		def searchCount = assays.getAssaySearchCount(assayName)
		
		then: "wait for result to populate in assay result accordian"
		def accordianText = assayResultAccordian.text()
		waitFor(10, 2) { accordianText.contains(ASSAYRESULTACCORDIAN) }
		def resultCount = accordianText.subSequence(accordianText.indexOf('(')+1, accordianText.indexOf(')'))
		assert resultHolderTable.size().toString() == resultCount
		assert resultHolderTable.size() != ISEMPTY
		assert resultHolderTable.size() == searchCount
		report "FindAssayByName"
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
		def searchCount = assays.getAssaySearchCount(assayName)
		
		then: "wait for result to populate in assay result accordian"
		def accordianText = assayResultAccordian.text()
		waitFor(10, 2) { accordianText.contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		def resultCount = accordianText.subSequence(accordianText.indexOf('(')+1, accordianText.indexOf(')'))
		assert resultHolderTable.size().toString() == resultCount
		assert resultHolderTable.size() == searchCount
		report "FindAssayBySearchString"
		when:"Navigating to Home Page"
		at FindAssayByNamePage
		assaysResults(0).assayId.click()
		then:
		at ViewAssayDefinitionPage
		report "OpenAssayFromSearchResult"
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
		def searchCount = assays.getAssaySearchCount(assayString)
		waitFor(15, 5) { autocompleteItems.size() > 1 }
		assert isAutocompleteListOk(autocompleteItems, assayString)
		assaySearchBtns.searchBtn.click()
		report "AutoSuggestResult"
		then: "wait for result to populate in assay result accordian"
		def accordianText = assayResultAccordian.text()
		waitFor(10, 2) { accordianText.contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		def resultCount = accordianText.subSequence(accordianText.indexOf('(')+1, accordianText.indexOf(')'))
		assert resultHolderTable.size().toString() == resultCount
		assert resultHolderTable.size() == searchCount
		report "FindAssayByAutoSuggest"
		when:"Navigating to Home Page"
		at FindAssayByNamePage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}

}