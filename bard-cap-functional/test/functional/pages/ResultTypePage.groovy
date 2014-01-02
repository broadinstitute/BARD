package pages

import modules.ButtonsModule
import modules.CardsHolderModule
import modules.ErrorInlineModule
import modules.LoadingModule
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
