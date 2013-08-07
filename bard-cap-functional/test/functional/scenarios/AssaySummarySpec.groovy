package scenarios
//Updated by Syed.
import org.openqa.selenium.remote.UnreachableBrowserException;

import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Assay

class AssaySummarySpec extends BardFunctionalSpec {
	def testData = TestDataReader.getTestData()
	int statusIndex = 1
	int nameIndex = 2
	int designedByIndex = 4
	int definitionTypeIndex = 7

	def setup() {
		logInSomeUser()

		when: "User is at Home page, Navigating to Search Assay By Id page"
		at HomePage
		navigateTo(NavigateTo.ASSAY_BY_ID)

		then: "User is at Search Assay by Id page"
		at CapSearchPage

		when: "User is trying to search some Assay"
		at CapSearchPage
		capSearchBy(SearchBy.ASSAY_ID, testData.AssayId)

		then: "User is at View Assay Definition page"
		at ViewAssayDefinitionPage
	}


	def "Test Assay Summary Status Edit"() {
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(testData.AssayId)
		def statusOriginal = uiSummary.Status
		def statusEdited = "Approved"

		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Status"
		editSummary(statusIndex, statusEdited, true)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Revert Edit/Update Summary Status"
		editSummary(statusIndex, statusOriginal, true)

		when:"Summary Status is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Status after revert on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusOriginal)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
		
		report "AssaySummaryStatus"
	}

	def "Test Assay Summary Name Edit"() {
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(testData.AssayId)
		def nameOriginal = uiSummary.Name
		def nameEdited = nameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Name"
		editSummary(nameIndex, nameEdited)

		when:"Summary Name is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Revert Edit/Update Summary Name"
		editSummary(nameIndex, nameOriginal)

		when:"Summary Name is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
		
		report "AssaySummaryName"
	}

	def "Test Assay Summary Name Edit with empty value"() {
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(testData.AssayId)
		def nameOriginal = uiSummary.Name

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Description"
		editSummary(nameIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
		
		report "AssaySummaryNameEmpty"
	}

	def "Test Assay Summary Designed By Edit"() {
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(testData.AssayId)
		def designedByOriginal = uiSummary.DesignedBy
		def designedByEdited = designedByOriginal+Constants.edited

		then:"Verify Summary Designed By before edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Designed By"
		editSummary(designedByIndex, designedByEdited)

		when:"Summary Designed By is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Designed By after edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(designedByEdited)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Revert Edit/Update Summary Designed By"
		editSummary(designedByIndex, designedByOriginal)

		when:"Summary Designed By is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Designed By after revert on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(designedByOriginal)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
		
		report "AssaySummaryDesignedBy"
	}

	def "Test Assay Summary Designed By Edit with empty value"() {
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(testData.AssayId)
		def designedByOriginal = uiSummary.DesignedBy

		then:"Verify Summary Designed By before edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Designed By"
		editSummary(designedByIndex, "")

		when:"Summary Designed By is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Designed By after edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(designedByOriginal)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
		
		report "SummaryDesignedByEmpty"
	}

	def "Test Assay Definition Type Edit"() {
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(testData.AssayId)
		def definitionTypeOriginal = uiSummary.DefinitionType
		def definitionTypeEdited = "Template"

		then:"Verify Summary Definition Type before edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Definition Type"
		editSummary(definitionTypeIndex, definitionTypeEdited, true)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Definition Type after edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeEdited)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Revert Edit/Update Summary Definition Type"
		editSummary(definitionTypeIndex, definitionTypeOriginal, true)

		when:"Summary Definition Type is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(testData.AssayId)

		then:"Verify Summary Definition Type after revert on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeOriginal)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)
//		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
//		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		report "AssaySummaryDefinitionType"
	}

}