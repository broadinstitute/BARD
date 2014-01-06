package scenarios

import pages.CreatePanelPage
import pages.HomePage
import pages.MyPanelPage
import pages.PanelAddAssayPage
import pages.ViewPanelPage
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Panel

/**
 * This class includes all the possible test functions for Panel module.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class PanelSpec extends BardFunctionalSpec {
	int nameIndex = 1
	int descriptionIndex = 2
	def setup() {
		logInSomeUser()
	}

	def "Test Create and Delete New Panel"() {
		given:"Navigating to Create New Panel page"
		to CreatePanelPage

		when:"User is at Create New Panel Page"
		at CreatePanelPage
		CreateNewPanel(TestData.addPanel)

		then:"Verify Summary Status before edit on UI & DB"
		at ViewPanelPage

		def uiSummary = getUISummaryInfo()
		def dbSummary = Panel.getPanelOverviewByName(TestData.addPanel.name)

		and:"Varify the added panel from db"
		assert uiSummary.PanelID.toString() == dbSummary.panelId.toString()
		assert uiSummary.Panel.equalsIgnoreCase(dbSummary.name)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.description)
		assert uiSummary.Owner.equalsIgnoreCase(dbSummary.owner)

		and:"Delete the added panel"
		deletePanel()

		and:"User is navigated to My Panel page"
		at MyPanelPage

		report ""
	}

	def "Test Add Assay to Panel"() {
		given:"Navigating to Create New Panel page"
		to ViewPanelPage

		when:"User is at View Panel page, clean up assays from panel"
		at ViewPanelPage
		deletePanelAssay()

		then:"Navigate to Add Assay to Panel page"
		navigateToAddAssayToPanel()

		and:"Add Assay Definition to Panel"
		at PanelAddAssayPage
		AddAssaysToPanel(TestData.assaysToPanel)

		and:"Delete the added panel"
		at ViewPanelPage
		def uiPanelAdids = getUiPanelAssays()
		def dbPanelAdids = Panel.getPanelAssays(TestData.panelId)

		and:"Validate UI Panel Assays with DB Panel Assays"
		assert uiPanelAdids.sort() == dbPanelAdids.sort()

		and:"Delete Panel Assays"
		deletePanelAssay()
		def uiPanelAdidsList = getUiPanelAssays()
		def dbPanelAdidsList = Panel.getPanelAssays(TestData.panelId)

		and:"Validate UI Panel Assays with DB Panel Assays"
		assert uiPanelAdidsList.sort() == dbPanelAdidsList.sort()

		report ""
	}

	def "Test Edit Panel Summary Name"() {
		given:"Navigating to Show Panel page"
		to ViewPanelPage

		when:"User is at View Panel Page, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Panel.getPanelOverviewById(TestData.panelId)
		def nameOriginal = uiSummary.Panel
		def nameEdited = nameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Panel.equalsIgnoreCase(dbSummary.Name)

		and:"Edit/Update Summary Name"
		editSummary(nameIndex, nameEdited)

		when:"Summary Name is Updated, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		uiSummary = getUISummaryInfo()
		dbSummary = Panel.getPanelOverviewById(TestData.panelId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Panel.equalsIgnoreCase(nameEdited)
		assert uiSummary.Panel.equalsIgnoreCase(dbSummary.Name)

		and:"Revert Edit/Update Summary Name"
		editSummary(nameIndex, nameOriginal)

		when:"Summary Name is reverted, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		uiSummary = getUISummaryInfo()
		dbSummary = Panel.getPanelOverviewById(TestData.panelId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Panel.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Panel.equalsIgnoreCase(dbSummary.Name)

		report ""
	}

	def "Test Edit Panel Summary Name with empty value"() {
		given:"Navigating to Show Panel page"
		to ViewPanelPage

		when:"User is at View Panel Page, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Panel.getPanelOverviewById(TestData.panelId)
		def projectNameOriginal = uiSummary.Panel

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Panel.equalsIgnoreCase(dbSummary.Name.toString())

		and:"Edit/Update Summary Description"
		editSummary(nameIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		uiSummary = getUISummaryInfo()
		dbSummary = Panel.getPanelOverviewById(TestData.panelId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Panel.equalsIgnoreCase(projectNameOriginal)
		assert uiSummary.Panel.equalsIgnoreCase(dbSummary.Name.toString())

		report ""
	}

	def "Test Edit Panel Summary Description"() {
		given:"Navigating to Show Panel page"
		to ViewPanelPage

		when:"User is at View Panel Page, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Panel.getPanelOverviewById(TestData.panelId)
		def projectDescriptionOriginal = uiSummary.Description
		def projectDescriptionEdited = projectDescriptionOriginal+Constants.edited

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Edit/Update Summary Description"
		editSummary(descriptionIndex, projectDescriptionEdited)

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		uiSummary = getUISummaryInfo()
		dbSummary = Panel.getPanelOverviewById(TestData.panelId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionEdited)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Revert Edit/Update Summary Description"
		editSummary(descriptionIndex, projectDescriptionOriginal)

		when:"Summary Description is reverted, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		uiSummary = getUISummaryInfo()
		dbSummary = Panel.getPanelOverviewById(TestData.panelId)

		then:"Verify Summary Description after revert on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionOriginal)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description)

		report ""
	}

	def "Test Edit Panel Summary Description with empty value"() {
		given:"Navigating to Show Panel page"
		to ViewPanelPage

		when:"User is at View Panel Page, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Panel.getPanelOverviewById(TestData.panelId)
		def projectDescriptionOriginal = uiSummary.Description

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Edit/Update Summary Description"
		editSummary(descriptionIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewPanelPage
		uiSummary = getUISummaryInfo()
		dbSummary = Panel.getPanelOverviewById(TestData.panelId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(projectDescriptionOriginal)
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		report ""
	}

}