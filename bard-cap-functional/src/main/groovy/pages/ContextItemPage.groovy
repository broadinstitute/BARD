package main.groovy.pages

import main.groovy.common.Constants
import main.groovy.modules.ButtonsModule
import main.groovy.modules.CardsHolderModule
import main.groovy.modules.ErrorInlineModule
import main.groovy.modules.LoadingModule
import main.groovy.modules.SelectChoicePopupModule
import main.groovy.modules.SelectToDropModule


/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ContextItemPage extends CapScaffoldPage{
	static url=""
	static at = { waitFor { $("h2").text().contains("Item") } }
	static content = {
		cardTable{ contextTitle -> module CardsHolderModule, $("div.card.roundedBorder.card-table-container").find("table.table.table-hover"), contextCard:contextTitle }
		formLoading { module LoadingModule}
		controlGroup {  $("div.control-group") }
		containerControl(wait: true, required: false){ controlGroup.find(".select2-container.span11.select2-container-active")}
		containerControlOpen(wait: true, required: false){ controlGroup.find(".select2-container.span11.select2-container-active.select2-dropdown-open")}
		attributeFromDictionary { module SelectChoicePopupModule, controlGroup, containerId:"s2id_attributeElementId" }
		integratedSearch { module SelectChoicePopupModule, controlGroup, containerId:"s2id_extValueSearch" } //External Ontology objects
		externalOntologyId(wait: true, required: false) { controlGroup.find("input#extValueId") }			//External Ontology objects
		freeTextValueContainer(wait: true, required: false){$("#freeTextValueContainer")}
		displayValue(wait: true, required: false) { $("#valueDisplay") }
		qualifier(wait: true, required: false) { controlGroup.find("#qualifier") }								//Numeric Value objects
		numericValue(wait: true, required: false) { $("#valueNum") }					//Numeric Value objects
		valueUnit { module SelectChoicePopupModule, controlGroup, containerId:"s2id_valueNumUnitId" }		//Numeric Value objects
		valueFromDictionary { module SelectChoicePopupModule, controlGroup, containerId:"s2id_valueElementId" }	//Element objects
		saveBtn { module ButtonsModule, controlGroup, buttonName:"Save" }
		cancelBtn { module ButtonsModule, controlGroup, buttonName:"Cancel" }
		updateBtn { module ButtonsModule, controlGroup, buttonName:"Update" }
		addAnotherItemBtn { module ButtonsModule, controlGroup, buttonName:"Add Another Item" }
		backToContextBtn { module ButtonsModule, controlGroup, buttonName:"Back to Context" }
		selectToDrop(wait: true, required: false) { module SelectToDropModule }
		providedWithResults { controlGroup.find("input#providedWithResults") }
		valueContraintFree(wait: true, required: false) { controlGroup.find("input#valueConstraintFree") }
		valueConstraintList(wait: true, required: false) { controlGroup.find("input#valueConstraintList") }
		valueConstraintRange(wait: true, required: false) { controlGroup.find("input#valueConstraintRange") }
		valueMin(wait: true, required: false) { controlGroup.find("input#valueMin") }
		valueMax(wait: true, required: false) { controlGroup.find("input#valueMax") }
		controlError(wait: true, required: false) { module ErrorInlineModule }
	}

	def isElementType(def inputData, def isCreate){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){
			valueFromDictionary.selectChoice
		}
		if(isCreate){
			selectAutoChoiceValue(valueFromDictionary, selectToDrop, inputData.dictionaryValue)
			addOrUpdate(isCreate)
			navigateBackToContext()
		}else{
			selectAutoChoiceValue(valueFromDictionary, selectToDrop, inputData.dictionaryValueEdit)
			addOrUpdate(isCreate)
			navigateBackToContext()
		}
	}

	def isFreeTextTYpe(def inputData, def isCreate){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ displayValue }
		if(isCreate){
			displayValue.value("")
			displayValue.value(inputData.valueDisplay)
			addOrUpdate(isCreate)
			navigateBackToContext()
		}else{
			displayValue.value("")
			displayValue.value(inputData.valueDisplayEdit)
			addOrUpdate(isCreate)
			navigateBackToContext()
		}
	}

	def isNumericType(def inputData, def isCreate){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ numericValue }
		if(isCreate){
			qualifier.value(inputData.qualifier)
			numericValue.value("")
			numericValue.value(inputData.numericValue)
			addOrUpdate(isCreate)
			navigateBackToContext()
		}else{
			waitFor{ numericValue }
			qualifier.value(inputData.qualifierEdit)
			numericValue.value("")
			numericValue.value(inputData.numericValueEdit)
			addOrUpdate(isCreate)
			navigateBackToContext()
		}

	}
	def isExternalOntologyType(def inputData, def isCreate, def integration){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ integratedSearch }
		if(isCreate){
			if(integration){
				Thread.sleep(6000)
				selectAutoChoiceValue(integratedSearch, selectToDrop, inputData.integratedSearch)
				addOrUpdate(isCreate)
				navigateBackToContext()
			}else{
				externalOntologyId.value("")
				displayValue.value("")
				externalOntologyId.value(inputData.ontologyId)
				displayValue.value(inputData.valueDisplay)
				addOrUpdate(isCreate)
				navigateBackToContext()
			}
		}else{
			if(integration){
				Thread.sleep(6000)
				selectAutoChoiceValue(integratedSearch, selectToDrop, inputData.integratedSearchEdit)
				addOrUpdate(isCreate)
				navigateBackToContext()
			}else{
				externalOntologyId.value("")
				displayValue.value("")
				externalOntologyId.value(inputData.ontologyIdEdit)
				displayValue.value(inputData.valueDisplayEdit)
				addOrUpdate(isCreate)
				navigateBackToContext()
			}
		}
	}

	def isFreeConstraint(){
		valueContraintFree.click()
		addOrUpdate(ci)
		navigateBackToContext()
	}

	def addOrUpdate(boolean isCreate){
		if(isCreate){
			saveBtn.buttonSubmitPrimary.click()
		}else{
			updateBtn.buttonSubmitPrimary.click()
		}
	}

	def navigateBackToContext(){
		backToContextBtn.button.click()
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

	def addExternalOntologyItem(def inputData, boolean isCreate=true, def resultUpload=false, def isIntegration = true){
		if(isCreate){
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	attributeFromDictionary.selectClose	}
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	integratedSearch.selectChoice }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	externalOntologyId }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	displayValue }
			//			attributeFromDictionary.selectClose.click()
			//			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}
		isExternalOntologyType(inputData, isCreate, isIntegration)
	}

	def addNumericValueItem(def inputData, boolean isCreate=true, def resultUpload=false){
		if(isCreate){
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	attributeFromDictionary.selectClose }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ numericValue.displayed }
			attributeFromDictionary.selectClose.click()
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !numericValue.displayed }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}
		isNumericType(inputData, isCreate)
	}

	def addFreeTextItem(def inputData, boolean isCreate=true, def resultUpload=false){
		if(isCreate){
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ attributeFromDictionary.selectClose }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ displayValue.displayed }
			attributeFromDictionary.selectClose.click()
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !displayValue.displayed }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}

		isFreeTextTYpe(inputData, isCreate)
	}
	def addElementContextItem(def inputData, boolean isCreate=true, def resultUpload=false){
		if(isCreate){
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ valueFromDictionary.selectChoice }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	attributeFromDictionary.selectClose }
			attributeFromDictionary.selectClose.click()
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}
		if(resultUpload){
			providedWithResults.click()
			if(valuConstraint=="Free"){
				isFreeConstraint()
			}else if(valuConstraint=="List"){
				valueConstraintList.click()
				isElementType(inputData, isCreate)
			}

		}else{
			isElementType(inputData, isCreate)
		}
	}

	def addContextItemValidation(def inputData, boolean isCreate=true, def resultUpload=false){
		waitFor{ !selectToDrop.searchNoResult }
		if(inputData == ""){
			addOrUpdate(isCreate)
			def alertMessage = "Attribute cannot be null, please select an attribute."
			waitFor{ !selectToDrop.searchNoResult }
			alertError(controlError.helpInline, alertMessage)
			cancelBtn.button.click()
		}
	}
	def selectAutoChoiceValue(def element1, def element2,  def inputValue){
		int index = 0
		if(!element2.dropdown){
			element1.selectChoice.click()
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ element2.dropdown }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	element2.searchInput }
		}
		//		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ element2.searchResult }
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	element2.searchInput }
		element2.searchInput.value(inputValue)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ element2.searchResult[index].text().contains(inputValue)}
		element2.searchResult[index].click()
	}
}