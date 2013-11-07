package scenarios
//Updated by Syed.
import org.openqa.selenium.remote.UnreachableBrowserException;

import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import spock.lang.Ignore;
import base.BardFunctionalSpec
import common.Constants
import common.TestData;
import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.SearchBy
import db.Assay

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssaySummarySpec extends BardFunctionalSpec {
//	def testData = TestDataReader.getTestData()
	int statusIndex = 1
	int nameIndex = 2
	int designedByIndex = 4
	int definitionTypeIndex = 5

	def setup() {
		logInSomeUser()
	}


	def "Test Edit Assay Summary Status"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def statusOriginal = uiSummary.Status
		def statusEdited = ""
		if(statusOriginal != "Approved"){
			statusEdited = "Approved"
		}else{
			statusEdited = "Retired"
		}
		
		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

		and:"Edit/Update Summary Status"
		editSummary(statusIndex, statusEdited, true)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		
		report ""
	}

	def "Test Edit Assay Summary Name"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def nameOriginal = uiSummary.Name
		def nameEdited = nameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		and:"Edit/Update Summary Name"
		editSummary(nameIndex, nameEdited)

		when:"Summary Name is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		and:"Revert Edit/Update Summary Name"
		editSummary(nameIndex, nameOriginal)

		when:"Summary Name is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		
		report ""
	}

	def "Test Edit Assay Summary Name with empty value"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def nameOriginal = uiSummary.Name

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())

		and:"Edit/Update Summary Description"
		editSummary(nameIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
		
		report ""
	}
/*
	def "Test Assay Summary Designed By Edit"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def designedByOriginal = uiSummary.DesignedBy
		def designedByEdited = designedByOriginal+Constants.edited

		then:"Verify Summary Designed By before edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())

		and:"Edit/Update Summary Designed By"
		editSummary(designedByIndex, designedByEdited)

		when:"Summary Designed By is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Designed By after edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(designedByEdited)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())

		and:"Revert Edit/Update Summary Designed By"
		editSummary(designedByIndex, designedByOriginal)

		when:"Summary Designed By is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Designed By after revert on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(designedByOriginal)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy)
		
		report "AssaySummaryDesignedBy"
	}

	def "Test Assay Summary Designed By Edit with empty value"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def designedByOriginal = uiSummary.DesignedBy

		then:"Verify Summary Designed By before edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())

		and:"Edit/Update Summary Designed By"
		editSummary(designedByIndex, "")

		when:"Summary Designed By is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Designed By after edit on UI & DB"
		assert uiSummary.DesignedBy.equalsIgnoreCase(designedByOriginal)
		assert uiSummary.DesignedBy.equalsIgnoreCase(dbSummary.DesignedBy.toString())
		
		report "SummaryDesignedByEmpty"
	}
*/
	def "Test Edit Assay Definition Type"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def definitionTypeOriginal = uiSummary.DefinitionType
		def definitionTypeEdited = ""
		if(definitionTypeOriginal != "Template"){
			definitionTypeEdited = "Template"
		}else{
			definitionTypeEdited = "Regular"
		}
		then:"Verify Summary Definition Type before edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)

		and:"Edit/Update Summary Definition Type"
		editSummary(definitionTypeIndex, definitionTypeEdited, true)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Definition Type after edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeEdited)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)

		and:"Revert Edit/Update Summary Definition Type"
		editSummary(definitionTypeIndex, definitionTypeOriginal, true)

		when:"Summary Definition Type is reverted, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Definition Type after revert on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeOriginal)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)

		report ""
	}

}