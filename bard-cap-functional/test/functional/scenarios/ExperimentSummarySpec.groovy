package scenarios

import pages.HomePage
import pages.ViewExperimentPage
import base.BardFunctionalSpec

import common.Constants
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
			statusEdited = "Approved"
		}else{
			statusEdited = "Retired"
		}

		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

		and:"Edit/Update Summary Status"
		editSummary(statusIndex, statusEdited, true)

		when:"Status is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

		report ""
	}

	def "Test Edit Experiment Summary Name"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def nameOriginal = uiSummary.Name
		def nameEdited = nameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		and:"Edit/Update Summary Name"
		editSummary(nameIndex, nameEdited)

		when:"Summary Name is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		and:"Revert Edit/Update Summary Name"
		editSummary(nameIndex, nameOriginal)

		when:"Summary Name is reverted, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

		report ""
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

		and:"Edit/Update Summary Description"
		editSummary(nameIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(experimentNameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())

		report ""
	}

	def "Test Edit Experiment Summary Description"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)
		def experimentDescriptionOriginal  = uiSummary.Description
		def experimentDescriptionEdited  = experimentDescriptionOriginal +Constants.edited

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Edit/Update Summary Description"
		editSummary(descriptionIndex, experimentDescriptionEdited )

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(experimentDescriptionEdited )
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		and:"Revert Edit/Update Summary Description"
		editSummary(descriptionIndex, experimentDescriptionOriginal )

		when:"Summary Description is reverted, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after revert on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(experimentDescriptionOriginal )
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description)

		report ""
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

		and:"Edit/Update Summary Description"
		editSummary(descriptionIndex, "")

		when:"Summary Description is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Description.equalsIgnoreCase(experimentDescriptionOriginal )
		assert uiSummary.Description.equalsIgnoreCase(dbSummary.Description.toString())

		report ""
	}

	def "Test Edit Experiment Summary Run Date"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Run Date before edit on UI & DB"
		assert uiSummary.RunDatefrom.equals(dbSummary.RunDateFrom)
		assert uiSummary.RunDateto.equals(dbSummary.RunDateTo)

		and:"Edit/Update Summary Run Date"
		editDate(runDateFromIndex, TestData.rundate.From)
		editDate(runDateToIndex, TestData.rundate.To)

		when:"Run Date is Updated, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		uiSummary = getUISummaryInfo()
		dbSummary = Experiment.getExperimentSummaryById(TestData.experimentId)

		then:"Verify Summary Run Date after edit on UI & DB"
		assert uiSummary.RunDatefrom.equals(dbSummary.RunDateFrom)
		assert uiSummary.RunDateto.equals(dbSummary.RunDateTo)

		report ""
	}
}