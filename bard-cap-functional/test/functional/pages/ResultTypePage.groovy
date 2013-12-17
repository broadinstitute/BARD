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
<<<<<<< HEAD
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

	def addResultType(def inputData){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(resultTypeId, selectToDrop, inputData.resultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
		selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifier)
		saveBtn.click()
	}

	def addChildResultType(def inputData){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(resultTypeId, selectToDrop, inputData.resultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
		selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifier)
		parentResultTypeId.value(inputData.parentResultType)
		parentChildRelationshipId.value(inputData.relationship)
		saveBtn.click()
	}

	def addDoseResultType(def inputData){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(doseResultTypeId, selectToDrop, inputData.doseResultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	responseResultTypeId.selectChoice }
		selectAutoChoiceValue(responseResultTypeId, selectToDrop, inputData.responseResultType)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
		selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifier)
		saveBtn.click()
=======
		resultTypeId { module SelectChoicePopupModule, containerId:"s2id_resultTypeId" }
		statsModifierId { module SelectChoicePopupModule, containerId:"s2id_statsModifierId" }
		parentResultTypeId(wait: true, required: false) { $("#parentExperimentMeasureId")}
		parentChildRelationshipId(wait: true, required: false) { $("#parentChildRelationshipId") }													//Numeric Value objects
		
		saveBtn { $("input",type:"submit", value:"Save") }
		cancelBtn { $("a.btn", text:"Cancel") }
		updateBtn { $("a.btn", text:"Cancel") }
		selectToDrop(wait: true, required: false) { module SelectToDropModule }
	}

	def addOrUpdate(boolean isCreate){
		if(isCreate){
			saveBtn.click()
		}else{
			updateBtn.click()
		}
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

	def addResultType(def inputData, boolean isCreate=true){
		if(isCreate){
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
			selectAutoChoiceValue(resultTypeId, selectToDrop, inputData.resultTypeId)
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	statsModifierId.selectChoice }
			selectAutoChoiceValue(statsModifierId, selectToDrop, inputData.statsModifierId)
			addOrUpdate(isCreate)
		}else{
		}
>>>>>>> branch 'functionaltests' of https://github.com/broadinstitute/BARD.git
	}

	def selectAutoChoiceValue(def element1, def element2,  def inputValue){
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
