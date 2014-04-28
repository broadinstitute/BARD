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

package pages

import modules.SelectChoicePopupModule
import modules.SelectToDropModule

import common.Constants



/**
 * @author Muhammad.Rafique
 * Date Created: 2013/12/02
 */
class ResultTypePage extends CapScaffoldPage{
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { title.contains("Result Types") } }
	static content = {
		doseResultTypeId { module SelectChoicePopupModule, containerId:"s2id_concentrationResultTypeId" }
		responseResultTypeId { module SelectChoicePopupModule, containerId:"s2id_responseResultTypeId" }
		resultTypeId { module SelectChoicePopupModule, containerId:"s2id_resultTypeId" }
		statsModifierId { module SelectChoicePopupModule, containerId:"s2id_statsModifierId" }
		parentResultTypeId(wait: true, required: false) { $("#parentExperimentMeasureId")}
		parentChildRelationshipId(wait: true, required: false) { $("#parentChildRelationshipId") }													//Numeric Value objects

		saveBtn { $("input",type:"submit", value:"Save") }
		cancelBtn { $("a.btn", text:"Cancel") }
		selectToDrop(wait: true, required: false) { module SelectToDropModule }
	}

	boolean alertError(def element, def condition){
		if(element){
			if(element.text()){
				assert element.text().equalsIgnoreCase(condition)
				return true
			}
		}
		return false
	}

	ViewExperimentPage addResultType(def inputData){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(resultTypeId, selectToDrop, inputData.resultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
		selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifier)
		saveBtn.click()
		
		return new ViewExperimentPage()
	}

	ViewExperimentPage addChildResultType(def inputData){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(resultTypeId, selectToDrop, inputData.resultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
		selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifier)
		parentResultTypeId.value(inputData.parentResultType)
		parentChildRelationshipId.value(inputData.relationship)
		saveBtn.click()
		
		return new ViewExperimentPage()
	}

	ViewExperimentPage addDoseResultType(def inputData){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(doseResultTypeId, selectToDrop, inputData.doseResultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	responseResultTypeId.selectChoice }
		selectAutoChoiceValue(responseResultTypeId, selectToDrop, inputData.responseResultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
		selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifier)
		saveBtn.click()
		
		return new ViewExperimentPage()
	}

	void selectAutoChoiceValue(def element1, def element2,  def inputValue){
		int index = 0
		if(!element2.dropdown){
			element1.selectChoice.click()
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){
				element2.dropdown
				element2.searchInput
			}
		}
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ element2.searchResult }
		element2.searchInput.value(inputValue)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ element2.searchResult[index].text().contains(inputValue)}
		element2.searchResult[index].click()
	}
}
