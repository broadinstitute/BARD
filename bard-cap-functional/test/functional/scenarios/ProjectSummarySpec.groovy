package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec
import common.Constants
import common.TestData;
import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.SearchBy
import db.Project

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Last Updated: 13/10/07
 */
class ProjectSummarySpec extends BardFunctionalSpec {
//	def testData = TestDataReader.getTestData()
	int statusIndex = 1
	int nameIndex = 2
	int descriptionIndex = 3
	
	def setup() {
		logInSomeUser()

//		when: "User is at Home page, Navigating to Search Project By Id page"
//		at HomePage
//		navigateTo(NavigateTo.PROJECT_BY_ID)
//
//		then: "User is at Search Project by Id page"
//		at CapSearchPage
//
//		when: "User is trying to search some project"
//		at CapSearchPage
//		capSearchBy(SearchBy.PROJECT_ID, testData.ProjectID)
//
//		then: "User is at View Project Definition page"
//		at ViewProjectDefinitionPage
	}


	def "Test Project Summary Status Edit"() {
		given:"Navigating to Show Project page"
		to ViewProjectDefinitionPage
		
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(TestData.projectId)
		def statusOriginal = uiSummary.Status
		def statusEdited = "Approved"

		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

		and:"Edit/Update Summary Status"
		editSummary(statusIndex, statusEdited, true)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		
		and:"Revert Edit/Update Summary Status"
		editSummary(statusIndex, statusOriginal, true)

		when:"Summary Status is reverted, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Status after revert on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusOriginal)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		
		report "ProjectSummaryStatus"
	}

	def "Test Project Summary Name Edit"() {
		given:"Navigating to Show Project page"
		to ViewProjectDefinitionPage
		
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(TestData.projectId)
		def nameOriginal = uiSummary.Name
		def nameEdited = nameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		and:"Edit/Update Summary Name"
		editSummary(nameIndex, nameEdited)
		
		when:"Summary Name is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		and:"Revert Edit/Update Summary Name"
		editSummary(nameIndex, nameOriginal)

		when:"Summary Name is reverted, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		
		report "ProjectSummaryName"
	}

	def "Test Project Summary Name Edit with empty value"() {
		given:"Navigating to Show Project page"
		to ViewProjectDefinitionPage 
		
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(TestData.projectId)
		def projectNameOriginal = uiSummary.Name

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())

		and:"Edit/Update Summary Description"
		editSummary(nameIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(projectNameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())

		report "ProjectSummaryNameEmpty"
	}

	def "Test Project Summary Description Edit"() {
		given:"Navigating to Show Project page"
		to ViewProjectDefinitionPage
		
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(TestData.projectId)
		def projectDescriptionOriginal = uiSummary.Description
		def projectDescriptionEdited = projectDescriptionOriginal+Constants.edited

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Edit/Update Summary Description"
		editSummary(descriptionIndex, projectDescriptionEdited)

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionEdited)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Revert Edit/Update Summary Description"
		editSummary(descriptionIndex, projectDescriptionOriginal)

		when:"Summary Description is reverted, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Description after revert on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionOriginal)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description)
		
		report "ProjectSummaryDescription"
	}

	def "Test Project Summary Description Edit with empty value"() {
		given:"Navigating to Show Project page"
		to ViewProjectDefinitionPage
		
		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(TestData.projectId)
		def projectDescriptionOriginal = uiSummary.Description

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Edit/Update Summary Description"
		editSummary(descriptionIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionOriginal)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())
		
		report "ProjectSummaryDescriptionEmpty"
	}
}