package scenarios
import base.BardFunctionalSpec;
import pages.HomePage
import pages.FindAssayByIdPage
import pages.ViewAssayDefinitionPage
import pages.ScaffoldPage
import spock.lang.Stepwise
import pages.FindAssayByNamePage
import common.Constants.NavigateTo
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
	final static WAIT_INTERVAL = 15
	final static R_INTERVAL = 3
	
	def setupSpec() {
		logInSomeUser()
	}

	def "Test Find Assay By Assay Definition Id"() {
		when: "User is navigating to Find Assay page"
		at HomePage
		navigateTo(NavigateTo.ASSAY_BY_ID)

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
		assert assaySummary.ddValue[index++].text() ==~ assayId
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayVersion)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.shortName)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayName)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.designedBy)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayStatus)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayType)
		report "FindAssayById"
		
		when:"Seach is complete, Navigating to Home Page"
		at ViewAssayDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
		
	}
	/*
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
		def summaryData = assays.getAssaySummaryByName(assayExactName)
		def index = 1
		
		then: "User is navigated to view assay definition page"
		at ViewAssayDefinitionPage
		assert assaySummary.ddValue[3].text() ==~ assayExactName
		
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayVersion)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.shortName)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayName)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.designedBy)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayStatus)
		assert assaySummary.ddValue[index++].text().equalsIgnoreCase(summaryData.assayType)
		report "FindAssayByExactName"
		
		when:"Seach is complete, Navigating to Home Page"
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
		waitFor(WAIT_INTERVAL, R_INTERVAL) { accordianText.contains(ASSAYRESULTACCORDIAN) }
		def resultCount = accordianText.subSequence(accordianText.indexOf('(')+1, accordianText.indexOf(')'))
		assert resultHolderTable.size().toString() == resultCount
		assert resultHolderTable.size() != ISEMPTY
		assert resultHolderTable.size() == searchCount
		report "FindAssayByName"
		
		when:"Seach is complete, Navigating to Home Page"
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
		waitFor(WAIT_INTERVAL, R_INTERVAL) { accordianText.contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		def resultCount = accordianText.subSequence(accordianText.indexOf('(')+1, accordianText.indexOf(')'))
		assert resultHolderTable.size().toString() == resultCount
		assert resultHolderTable.size() == searchCount
		report "FindAssayBySearchString"
		
		when:"Open specific assay details"
		at FindAssayByNamePage
		assaysResults(0).assayId.click()
		
		then: "User is at assay details page"
		at ViewAssayDefinitionPage
		report "OpenAssayFromSearchResult"
		
		when: "Seach is complete, Navigate to Home page"
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
		waitFor(WAIT_INTERVAL, R_INTERVAL) { autocompleteItems.size() > 1 }
		assert isAutocompleteListOk(autocompleteItems, assayString)
		assaySearchBtns.searchBtn.click()
		report "AutoSuggestResult"
		
		then: "Wait for result to populate in assay result accordian"
		def accordianText = assayResultAccordian.text()
		waitFor(WAIT_INTERVAL, R_INTERVAL) { accordianText.contains(ASSAYRESULTACCORDIAN) }
		assert resultHolderTable.size() != ISEMPTY
		def resultCount = accordianText.subSequence(accordianText.indexOf('(')+1, accordianText.indexOf(')'))
		assert resultHolderTable.size().toString() == resultCount
		assert resultHolderTable.size() == searchCount
		report "FindAssayByAutoSuggest"
		
		when:"Seach is complete, Navigating to Home Page"
		at FindAssayByNamePage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
*/
}