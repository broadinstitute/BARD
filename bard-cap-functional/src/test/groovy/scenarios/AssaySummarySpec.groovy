package test.groovy.scenarios

import main.groovy.common.Constants
import main.groovy.common.TestData
import main.groovy.db.Assay
import main.groovy.pages.CreateAssayPage
import main.groovy.pages.ViewAssayDefinitionPage
import test.groovy.base.BardFunctionalSpec


/**
 * This class holds all the test functions of Assay Summary section
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssaySummarySpec extends BardFunctionalSpec {
	int statusIndex = 1
	int nameIndex = 2
	int designedByIndex = 4
	int definitionTypeIndex = 5

	def setup() {
		logInSomeUser()
	}

	def "Test Create New Assay"() {
		given:"Navigating to Create New Assay page"
		to CreateAssayPage

		when:"User is at Create New Assay Page"
		at CreateAssayPage
		CreateNewAssay(TestData.createAssay)

		then:"Navigate to View Assay page and fetch summary info"
		at ViewAssayDefinitionPage

		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getCreatedAssaySummary(TestData.createAssay.name)

		and:"Validate the created assay summary info with db and ui"
		assert uiSummary.ADID.toString() == dbSummary.ADID.toString()
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.Owner == dbSummary.owner
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

        when:"Edit/Update Summary Status, Fetch Summary info on UI and DB for validation"
        editSummary(statusIndex, statusEdited, true)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
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

        when:"Edit/Update Summary Name, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, nameEdited)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

        when:"Revert Edit/Update Summary Name, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, nameOriginal)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
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

        when:"Edit/Update Summary Description, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, "")
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
	}

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

        when:"Edit/Update Summary Definition Type, Fetch Summary info on UI and DB for validation"
        editSummary(definitionTypeIndex, definitionTypeEdited, true)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Definition Type after edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeEdited)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)

        when:"Revert Edit/Update Summary Definition Type, Fetch Summary info on UI and DB for validation"
        editSummary(definitionTypeIndex, definitionTypeOriginal, true)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Definition Type after revert on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeOriginal)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)
	}

}