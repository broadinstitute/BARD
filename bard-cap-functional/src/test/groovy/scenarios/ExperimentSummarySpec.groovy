package scenarios

import pages.CreateExperimentPage
import pages.ViewExperimentPage
import base.BardFunctionalSpec

import common.TestData

import db.Experiment



/**
 * This class includes all the possible test functions for overview section of experiment.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/25
 */
class ExperimentSummarySpec extends BardFunctionalSpec {
	int statusIndex = 1
	int nameIndex = 2
	int descriptionIndex = 3
	int runDateFromIndex = 5
	int runDateToIndex = 6

	def setup() {
		logInSomeUser()
	}

	def "Test Create New Experiment"() {
		given:"Navigating to Create New Experiment page"
		to CreateExperimentPage

		when:"User is at Create New Experiment Page"
		at CreateExperimentPage
		CreateNewExperiment(TestData.createExperiment)

		then:"Navigate to View Experiment page and fetch summary info"
		at ViewExperimentPage

		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getCreatedExperimentSummary(TestData.createExperiment.name)

		and:"Validate the created Experiment summary info with db and ui"
		assert uiSummary.EID.toString() == dbSummary.EID.toString()
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.Owner == dbSummary.owner
	}

	def "Test Edit Experiment Summary Status"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def statusOriginal = uiSummary.Status
		def statusEdited = ""
		if(statusOriginal != "Approved"){
			//			statusEdited = "Approved"
			statusEdited = "Retired"
		}else{
			statusEdited = "Retired"
		}

		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

        when:"Edit/Update Summary Status, Fetch Summary info on UI and DB for validation"
        editSummary(statusIndex, statusEdited, true)
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
	}

	def "Test Edit Experiment Summary Name"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def nameOriginal = uiSummary.Name
		def nameEdited = nameOriginal+TestData.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

        when:"Edit/Update Summary Name, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, nameEdited)
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

        when:"Revert Edit/Update Summary Name, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, nameOriginal)
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
	}

	def "Test Edit Experiment Summary Name with empty value"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def experimentNameOriginal = uiSummary.Name

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())

        when:"Edit/Update Summary Description, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, "")
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(experimentNameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
	}

	def "Test Edit Experiment Summary Description"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def experimentDescriptionOriginal  = uiSummary.Description
		def experimentDescriptionEdited  = experimentDescriptionOriginal +TestData.edited

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

        when:"Edit/Update Summary Description, Fetch Summary info on UI and DB for validation"
        editSummary(descriptionIndex, experimentDescriptionEdited )
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(experimentDescriptionEdited )
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

        when:"Revert Edit/Update Summary Description, Fetch Summary info on UI and DB for validation"
        editSummary(descriptionIndex, experimentDescriptionOriginal )
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after revert on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(experimentDescriptionOriginal )
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description)
	}

	def "Test Edit Experiment Summary Description with empty value"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def experimentDescriptionOriginal  = uiSummary.Description

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

        when:"Edit/Update Summary Description, Fetch Summary info on UI and DB for validation"
        editSummary(descriptionIndex, "")
        at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(experimentDescriptionOriginal )
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())
	}

	def "Test Edit Experiment Summary Run Date"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Run Date before edit on UI & DB"
		assert uiSummary.RunDatefrom.toString() == dbSummary.RunDateFrom.toString()
		assert uiSummary.RunDateto.toString() == dbSummary.RunDateTo.toString()

		and:"Edit/Update Summary Run Date"
		editDate(runDateFromIndex, TestData.rundate.From)
		editDate(runDateToIndex, TestData.rundate.To)

		when:"Run Date is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Run Date after edit on UI & DB"
		assert uiSummary.RunDatefrom.toString() == dbSummary.RunDateFrom.toString()
		assert uiSummary.RunDateto.toString() == dbSummary.RunDateTo.toString()
	}
}