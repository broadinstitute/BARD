package pages

import geb.Page
import modules.ButtonsModule
import modules.CardsHolderModule
import modules.ErrorInlineModule
import modules.LoadingModule
import modules.SelectChoicePopupModule
import modules.SelectToDropModule

import common.Constants
import common.Constants.ContextItem
import common.Constants.ExpectedValueType

class ContextItemPage extends CapScaffoldPage{
	static url=""
	//	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { $("h4").text().contains("Add a New Item") } }
	static content = {
		cardTable{ contextTitle -> module CardsHolderModule, $("div.card.roundedBorder.card-table-container").find("table.table.table-hover"), contextCard:contextTitle }
		formLoading { module LoadingModule}
		controlGroup {  $("div.control-group") }
		attributeFromDictionary { module SelectChoicePopupModule, controlGroup, containerId:"s2id_attributeElementId" }
		integratedSearch { module SelectChoicePopupModule, controlGroup, containerId:"s2id_extValueSearch" } //External Ontology objects
		externalOntologyId(wait: true, required: false) { controlGroup.find("input#extValueId") }									//External Ontology objects
		displayValue(wait: true, required: false) { controlGroup.find("input#valueDisplay") }										//External Ontology objects
		qualifier(wait: true, required: false) { controlGroup.find("#qualifier") }													//Numeric Value objects
		numericValue(wait: true, required: false) { controlGroup.find("input#valueNum") }											//Numeric Value objects
		valueUnit { module SelectChoicePopupModule, controlGroup, containerId:"s2id_valueNumUnitId" }		//Numeric Value objects
		valueFromDictionary { module SelectChoicePopupModule, controlGroup, containerId:"s2id_valueElementId" }	//Element objects
		saveBtn { module ButtonsModule, controlGroup, buttonName:"Save" }
		cancelBtn { module ButtonsModule, controlGroup, buttonName:"Cancel" }
		updateBtn { module ButtonsModule, controlGroup, buttonName:"Update" }
		addAnotherItemBtn { module ButtonsModule, controlGroup, buttonName:"Add Another Item" }
		backToContextBtn { module ButtonsModule, controlGroup, buttonName:"Back to Context" }
		selectToDrop(wait: true, required: false) { module SelectToDropModule, $("div.select2-drop.select2-drop-active") }
		//		controlError(wait: true, required: false) { $("div.control-group.error").find("div.controls").find("span.help-inline") }
		providedWithResults { controlGroup.find("input#providedWithResults") }
		valueContraintFree(wait: true, required: false) { controlGroup.find("input#valueConstraintFree") }
		valueConstraintList(wait: true, required: false) { controlGroup.find("input#valueConstraintList") }
		valueConstraintRange(wait: true, required: false) { controlGroup.find("input#valueConstraintRange") }
		valueMin(wait: true, required: false) { controlGroup.find("input#valueMin") }
		valueMax(wait: true, required: false) { controlGroup.find("input#valueMax") }
		controlError(wait: true, required: false) { module ErrorInlineModule }
	}

	def addEditContextItem(ContextItem ci, ExpectedValueType valueType, def inputData, def add=true, def integration=false, def resultUpload=false, def ... valuConstraint){
		String qualifierReset = "Qualifier"
		if(inputData.AttributeFromDictionary != ""){
			if(add){
				selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.AttributeFromDictionary)
			}
			switch(valueType){
				case ExpectedValueType.ELEMENT:
					if(resultUpload){
						providedWithResults.click()
						if(valuConstraint=="Free"){
							isFreeConstraint()
						}else if(valuConstraint=="List"){
							valueConstraintList.click()
							isElementType(inputData, ci)
						}

					}else{
						isElementType(inputData, ci)
					}
					break;
				case ExpectedValueType.FREE:
					if(resultUpload){
						providedWithResults.click()
						if(valuConstraint=="Free"){
							isFreeConstraint()
						}else if(valuConstraint=="List"){
							valueConstraintList.click()
							isFreeTextTYpe(inputData, ci)
						}

					}else{
						isFreeTextTYpe(inputData, ci)
					}
					break;
				case ExpectedValueType.NUMERIC:
					if(resultUpload){
						providedWithResults.click()
						if(valuConstraint=="Free"){
							isFreeConstraint()
						}else if(valuConstraint=="List"){
							valueConstraintList.click()
							isNumericType(inputData, ci)
						}else if(valuConstraint=="Range"){
							valueConstraintRange.click()
							valueMin.value(inputData.valueMin)
							valueMax.value(inputData.valueMax)
							addOrUpdate(ci)
							navigateBackToContext()
						}

					}else{
						isNumericType(inputData, ci)
					}
					break;
				case ExpectedValueType.ONTOLOGY:
					if(resultUpload){
						providedWithResults.click()
						if(valuConstraint=="Free"){
							isFreeConstraint()
						}else if(valuConstraint=="List"){
							valueConstraintList.click()
							isExternalOntologyType(inputData, ci, integration)
						}

					}else{
						isExternalOntologyType(inputData, ci, integration)
					}
					break;
			}
		}else{
			attributeFromDictionary.label.click()
			addOrUpdate(ci)
			def alertMessage = "Attribute cannot be null, please select an attribute."
			alertError(controlError.helpInline, alertMessage)
			attributeFromDictionary.label.click()
			cancelBtn.button.click()
		}
	}
	def isElementType(def inputData, def ci){
		if(inputData.ValueFromDictionary != ""){
			selectAutoChoiceValue(valueFromDictionary, selectToDrop, inputData.ValueFromDictionary)
			addOrUpdate(ci)
			navigateBackToContext()
		}else{
			valueFromDictionary.label.click()
			addOrUpdate(ci)
			def errorMessageTop = "A value from the dictionary is required."
			def errorMessageIn = "When the attribute of an item expects a BARD dictionary element, a dictionary value is required."
			alertError(controlError.alertError.find("p")[0], errorMessageTop)
			alertError(controlError.alertError.find("p")[1], errorMessageIn)
			valueFromDictionary.label.click()
			cancelBtn.button.click()
		}
	}
	def isFreeTextTYpe(def inputData, def ci){
		if(inputData.DiplayValue != ""){
			displayValue.value("")
			displayValue.value(inputData.DiplayValue)
			addOrUpdate(ci)
			navigateBackToContext()
		}else{
			addOrUpdate(ci)
			def displayValueAlert = "The valueDisplay can not be blank, this field is usually populated after using an auto-suggest drop-down."
			alertError(controlError.helpInline, displayValueAlert)
			cancelBtn.button.click()
		}
	}
	def isNumericType(def inputData, def ci){
		if(inputData.NumericValue != ""){
			qualifier.value(inputData.Qalifier)
			numericValue.value("")
			numericValue.value(inputData.NumericValue)
			addOrUpdate(ci)
			navigateBackToContext()
		}else{
			addOrUpdate(ci)
			def displayValueAlert = "Please supply all required fields for a numeric value."
			alertError(controlError.alertError.find("p")[0], displayValueAlert)
			cancelBtn.button.click()
		}
	}
	def isExternalOntologyType(def inputData, def ci, def integration){
		if(integration){
			if(inputData.IntegratedSearch != ""){
				integratedSearch.label.click()
				externalOntologyId.value("")
				displayValue.value("")
				selectAutoChoiceValue(integratedSearch, selectToDrop, inputData.IntegratedSearch)
				addOrUpdate(ci)
				navigateBackToContext()
			}else{
				integratedSearch.label.click()
				addOrUpdate(ci)
				def ontologyAlert = "When the attribute of an item expects an External Ontology reference, the fields External Ontology Id and Display Value must not be blank."
				alertError(controlError.alertError.find("p")[0], ontologyAlert)
				cancelBtn.button.click()
			}

		}else{
			if(inputData.OntologyId != "" || inputData.DiplayValue != ""){
				integratedSearch.label.click()
				externalOntologyId.value("")
				displayValue.value("")
				externalOntologyId.value(inputData.OntologyId)
				displayValue.value(inputData.DiplayValue)
				addOrUpdate(ci)
				navigateBackToContext()
			}else{
				integratedSearch.label.click()
				addOrUpdate(ci)
				def ontologyAlert = "When the attribute of an item expects an External Ontology reference, the fields External Ontology Id and Display Value must not be blank."
				alertError(controlError.alertError.find("p")[0], ontologyAlert)
				cancelBtn.button.click()
			}
		}
	}
	def isFreeConstraint(){
		valueContraintFree.click()
		addOrUpdate(ci)
		navigateBackToContext()
	}
	def addOrUpdate(ContextItem ci){
		switch(ci){
			case ContextItem.ADD:
				saveBtn.buttonSubmitPrimary.click()
				break;
			case ContextItem.UPDATE:
				updateBtn.buttonSubmitPrimary.click()
				break;
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
	def selectAutoChoiceValue(def element1, def element2,  def inputValue){
		element1.label.click()
		if(element1.selectClose.displayed){
			element1.selectClose.click()
		}
		int index = 0
		element1.searchInput.value(inputValue)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { element2.searchResult[index].text() != "Searching..." && element2.searchResult[index].text() != "Please enter 1 more characters"}
		element2.searchResult[index].click()
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { !formLoading.loading.displayed }
	}
}