package scenarios

import pages.HomePage
import pages.ResultTypePage;
import pages.ViewExperimentPage
import base.BardFunctionalSpec
import common.Constants
import common.TestData
import db.Experiment

/**
 * This class includes all the possible test functions for result types of experiment.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/28
 */
class ExperimentResultTypeSpec extends BardFunctionalSpec {

	def setup() {
		logInSomeUser()
	}

	def "Test Add Result Type and then delete from Experiment"() {
		given:"Navigating to Show Experiment page"
		to ViewExperimentPage

		when:"User is at View Experiment Page, Fetch Summary info on UI and DB for validation"
		at ViewExperimentPage
		def uiResultTypes = getUIRsultTypes()
		def dbResultTypes = Experiment.getExperimentResultType(TestData.experimentId)
		
		then:"Validate UI result types with database result types"
		assert uiResultTypes.size() == dbResultTypes.size()
		assert uiResultTypes.sort() == dbResultTypes.sort()
		
		and:"Cleanup result types before adding new one"
		while(isAnyResultType()){
			deleteResultTypes()
		}
		and:"Navigate to Add Result Type Page"
		navigateToResultTypePage()
		
		when:"At Add Result Type page, Add new result type"
		at ResultTypePage
		addResultType(TestData.resultType)
		
		then:"At view experiment page, Verify result type is added"
		at ViewExperimentPage
		assert isResultType(TestData.resultType)
		
		def uiResultTypeAdded = getUIRsultTypes()
		def dbResultTypeAdded = Experiment.getExperimentResultType(TestData.experimentId)
		
		then:"Validate UI result types with database result types"
		assert uiResultTypeAdded.size() == dbResultTypeAdded.size()
		assert uiResultTypeAdded.sort() == dbResultTypeAdded.sort()
		
		and:"Cleanup the added result types"
		while(isAnyResultType()){
			deleteResultTypes()
		}

		report ""
	}

}