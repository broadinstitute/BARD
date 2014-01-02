package scenarios

import pages.CreateProjectPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Project

/**
 * This class includes all the possible test functions for overview section of Project.
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ProjectSummarySpec extends BardFunctionalSpec {
	int statusIndex = 1
	int nameIndex = 2
	int descriptionIndex = 3

	def setup() {
		logInSomeUser()
	}

	def "Test Create New Project"() {
		given:"Navigating to Create New Project page"
		to CreateProjectPage

		when:"User is at Create New Project Page"
		at CreateProjectPage
		CreateNewProject(TestData.createProject)

		then:"Navigate to View Project page and fetch summary info"
		at ViewProjectDefinitionPage

		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getCreatedProjectSummary(TestData.createProject.name)

		and:"Validate the created project summary info with db and ui"
		assert uiSummary.PID.toString() == dbSummary.PID.toString()
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.Owner == dbSummary.owner

		report ""
	}

	def "Test Edit Project Summary Status"() {
		given:"Navigating to Show Project page"
		to ViewProjectDefinitionPage

		when:"User is at View Project Page, Fetch Summary info on UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Project.getProjectSummaryById(TestData.projectId)
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
		at ViewProjectDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Project.getProjectSummaryById(TestData.projectId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

		report ""
	}

	def "Test Edit Project Summary Name"() {
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

		report ""
	}

	def "Test Edit Project Summary Name with empty value"() {
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

		report ""
	}

	def "Test Edit Project Summary Description"() {
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

		report ""
	}

	def "Test Edit Project Summary Description with empty value"() {
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

		report ""
	}

}