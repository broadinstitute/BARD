/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scenarios

import pages.ResultTypePage
import pages.ViewExperimentPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.TestData

import db.Experiment

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
