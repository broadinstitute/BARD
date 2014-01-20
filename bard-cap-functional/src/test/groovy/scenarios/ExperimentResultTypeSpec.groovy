package test.groovy.scenarios

import main.groovy.common.TestData
import main.groovy.db.Experiment
import main.groovy.pages.ResultTypePage
import main.groovy.pages.ViewExperimentPage

import spock.lang.Unroll
import test.groovy.base.BardFunctionalSpec

/**
 * This class includes all the possible test functions for result types of experiment.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/28
 */
@Unroll
class ExperimentResultTypeSpec extends BardFunctionalSpec {

	def setup() {
		logInSomeUser()
	}

	def "Test Add #TestName and then delete from Experiment"() {
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
		while(isResultType(resultType)){
			deleteSpecificResultType(resultType)
		}
		and:"Navigate to Add Result Type Page"
		if(TestName=="ResultType"){
			navigateToResultTypePage()
		}else if(TestName=="DoseResultType"){
			navigateToDoseResultTypePage()
		}

		when:"At Add Result Type page, Add new result type"
		at ResultTypePage
		if(TestName=="ResultType"){
			addResultType(inputData)
		}else if(TestName=="DoseResultType"){
			addDoseResultType(inputData)
		}

		then:"At view experiment page, Verify result type is added"
		at ViewExperimentPage
		assert isResultType(resultType)

		def uiResultTypeAdded = getUIRsultTypes()
		def dbResultTypeAdded = Experiment.getExperimentResultType(TestData.experimentId)

		then:"Validate UI result types with database result types"
		assert uiResultTypeAdded.size() == dbResultTypeAdded.size()
		assert uiResultTypeAdded.sort() == dbResultTypeAdded.sort()

		and:"Cleanup the added result types"
		while(isResultType(resultType)){
			deleteSpecificResultType(resultType)
		}
		and:"User is at show experiment page"
		at ViewExperimentPage

		report ""

		where:
		TestName				| inputData						| resultType
		"ResultType"			| TestData.resultType.parent	| TestData.resultType.parent.resultType
		"DoseResultType"		| TestData.resultType.dose		| TestData.resultType.dose.doseResultType

	}

	def "Test Edit #TestName in an Experiment"() {
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
		while(isResultType(resultType)){
			deleteSpecificResultType(resultType)
		}
		and:"Add new result type before editing"
		if(TestName=="ResultType"){
			navigateToResultTypePage()
		}else if(TestName=="DoseResultType"){
			navigateToDoseResultTypePage()
		}

		and:"Add new result type"
		at ResultTypePage
		if(TestName=="ResultType"){
			addResultType(inputData)
		}else if(TestName=="DoseResultType"){
			addDoseResultType(inputData)
		}

		and:"Verify result type is added"
		at ViewExperimentPage
		assert isResultType(resultType)

		when:"At view experiment page, edit result type"
		at ViewExperimentPage
		editResultTypePage(resultType)

		then:"Add new result type"
		at ResultTypePage
		addResultType(editData)

		and:"Verify result type is edited"
		at ViewExperimentPage
		assert isResultType(editData.resultType)
		def uiResultTypeEdited = getUIRsultTypes()
		def dbResultTypeEdited = Experiment.getExperimentResultType(TestData.experimentId)

		and:"Validate UI result types with database result types"
		assert uiResultTypeEdited.size() == dbResultTypeEdited.size()
		assert uiResultTypeEdited.sort() == dbResultTypeEdited.sort()

		and:"Cleanup the added result types"
		while(isResultType(editData.resultType)){
			deleteSpecificResultType(editData.resultType)
		}

		and:"User is at show experiment page"
		at ViewExperimentPage

		report ""

		where:
		TestName			| inputData						| editData 						 | resultType
		"ResultType"		| TestData.resultType.parent	| TestData.resultType.editParent | TestData.resultType.parent.resultType
		"DoseResultType"	| TestData.resultType.dose		| TestData.resultType.editParent | TestData.resultType.dose.doseResultType
	}

	def "Test Add Child Result Type in an Experiment"() {
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
		while(isResultType(TestData.resultType.parent.resultType)){
			deleteSpecificResultType(TestData.resultType.parent.resultType)
		}
		and:"Add new result type before editing"
		navigateToResultTypePage()

		and:"Add new result type"
		at ResultTypePage
		addResultType(TestData.resultType.parent)

		and:"Verify result type is added"
		at ViewExperimentPage
		assert isResultType(TestData.resultType.parent.resultType)

		when:"At view experiment page, add child result type"
		at ViewExperimentPage
		navigateToResultTypePage()

		then:"Add new result type"
		at ResultTypePage
		addChildResultType(TestData.resultType.child)

		and:"Verify result type is edited"
		at ViewExperimentPage
		assert isResultType(TestData.resultType.child.resultType)
		def uiResultTypeEdited = getUIRsultTypes()
		def dbResultTypeEdited = Experiment.getExperimentResultType(TestData.experimentId)

		and:"Validate UI result types with database result types"
		assert uiResultTypeEdited.size() == dbResultTypeEdited.size()
		assert uiResultTypeEdited.sort() == dbResultTypeEdited.sort()

		and:"Cleanup the added result types"
		while(isResultType(TestData.resultType.child.resultType)){
			deleteSpecificResultType(TestData.resultType.child.resultType)
		}

		and:"User is at show experiment page"
		at ViewExperimentPage

		report ""
	}
}
