package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.ProjectSummaryEdit
import common.Constants.SearchBy

import db.Project

//@Stepwise
class ProjectSummarySpec extends BardFunctionalSpec {
	def testData = TestDataReader.getTestData()

	def setup() {
		logInSomeUser()

		when: "User is at Home page, Navigating to Search Project By Id page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_ID)

		then: "User is at Search Project by Id page"
		at CapSearchPage

		when: "User is trying to search some project"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_ID, testData.ProjectID)

		then: "User is at View Project Definition page"
		at ViewProjectDefinitionPage
	}


	def "Test Project Summary Status Edit"() {
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Status"
		editSummay(ProjectSummaryEdit.STATUS, testData.ProjectStatus)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(testData.ProjectStatus)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
		report "SummaryStatus"
	}

	def "Test Project Summary Name Edit"() {
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(testData.ProjectID)
		def projectNameOriginal = uiSummary.Name
		def projectNameEdited = projectNameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Name"
		editSummay(ProjectSummaryEdit.NAME, projectNameEdited)

		when:"Summary Name is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(projectNameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Revert Edit/Update Summary Name"
		editSummay(ProjectSummaryEdit.NAME, projectNameOriginal)

		when:"Summary Name is reverted, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(projectNameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
	}

	def "Test Project Summary Name Edit with empty value"() {
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(testData.ProjectID)
		def projectNameOriginal = uiSummary.Name

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Description"
		editSummay(ProjectSummaryEdit.NAME, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(projectNameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

	}

	def "Test Project Summary Description Edit"() {
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(testData.ProjectID)
		def projectDescriptionOriginal = uiSummary.Description
		def projectDescriptionEdited = projectDescriptionOriginal+Constants.edited

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Description"
		editSummay(ProjectSummaryEdit.DESCRIPTION, projectDescriptionEdited)

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionEdited)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Revert Edit/Update Summary Description"
		editSummay(ProjectSummaryEdit.DESCRIPTION, projectDescriptionOriginal)

		when:"Summary Description is reverted, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Description after revert on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionOriginal)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description)
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)
	}

	def "Test Project Summary Description Edit with empty value"() {
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(testData.ProjectID)
		def projectDescriptionOriginal = uiSummary.Description

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

		and:"Edit/Update Summary Description"
		editSummay(ProjectSummaryEdit.DESCRIPTION, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(testData.ProjectID)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionOriginal)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())
		assert uiSummary.LastUpdated.equalsIgnoreCase(dbSummary.LastUpdated)
		assert uiSummary.ModifiedBy.equalsIgnoreCase(dbSummary.ModifiedBy)

	}
}