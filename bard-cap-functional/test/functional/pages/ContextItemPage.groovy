package pages

import modules.ButtonsModule
import modules.CardsHolderModule
import modules.ErrorInlineModule
import modules.LoadingModule
import modules.SelectChoicePopupModule
import modules.SelectToDropModule

import common.Constants
import common.Constants.ContextItem
import common.Constants.ExpectedValueType

/**
 * @author Muhammad.Rafique
  * Date Created: 13/02/07
 * Date Updated: 13/10/07
 */
class ContextItemPage extends CapScaffoldPage{
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { $("h2").text().contains("Item") } }
	static content = {
		cardTable{ contextTitle -> module CardsHolderModule, $("div.card.roundedBorder.card-table-container").find("table.table.table-hover"), contextCard:contextTitle }
		formLoading { module LoadingModule}
		controlGroup {  $("div.control-group") }
		containerControl(wait: true, required: false){ controlGroup.find(".select2-container.span11.select2-container-active")}
		containerControlOpen(wait: true, required: false){ controlGroup.find(".select2-container.span11.select2-container-active.select2-dropdown-open")}
		attributeFromDictionary { module SelectChoicePopupModule, controlGroup, containerId:"s2id_attributeElementId" }
		integratedSearch { module SelectChoicePopupModule, controlGroup, containerId:"s2id_extValueSearch" } //External Ontology objects
		externalOntologyId(wait: true, required: false) { controlGroup.find("input#extValueId") }									//External Ontology objects
		displayValue(wait: true, required: false) { $("#freeTextValueContainer").find("input#valueDisplay") }										//External Ontology objects
		qualifier(wait: true, required: false) { controlGroup.find("#qualifier") }													//Numeric Value objects
		numericValue(wait: true, required: false) { controlGroup.find("input#valueNum") }											//Numeric Value objects
		valueUnit { module SelectChoicePopupModule, controlGroup, containerId:"s2id_valueNumUnitId" }		//Numeric Value objects
		valueFromDictionary { module SelectChoicePopupModule, controlGroup, containerId:"s2id_valueElementId" }	//Element objects
		saveBtn { module ButtonsModule, controlGroup, buttonName:"Save" }
		cancelBtn { module ButtonsModule, controlGroup, buttonName:"Cancel" }
		updateBtn { module ButtonsModule, controlGroup, buttonName:"Update" }
		addAnotherItemBtn { module ButtonsModule, controlGroup, buttonName:"Add Another Item" }
		backToContextBtn { module ButtonsModule, controlGroup, buttonName:"Back to Context" }
		selectToDrop(wait: true, required: false) { module SelectToDropModule }
//		selectToDrop(wait: true, required: false) { module SelectToDropModule, $("#select2-drop") }
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
				waitFor{ !selectToDrop.searchNoResult }
//				if(!selectToDrop.selectDropMask){
//					attributeFromDictionary.selectChoice.click()
//				}
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
	def isElementType(def inputData, def isCreate){
//		if(inputData.ValueFromDictionary != ""){
//			waitFor{ valueFromDictionary }
//			selectAutoChoiceValue(valueFromDictionary, selectToDrop, inputData.dictionaryValue)
//			addOrUpdate(isCreate)
//			navigateBackToContext()
//		}else{
//			waitFor{ valueFromDictionary }
//			valueFromDictionary.click()
//			addOrUpdate(isCreate)
//			def errorMessageTop = "A value from the dictionary is required."
//			def errorMessageIn = "When the attribute of an item expects a BARD dictionary element, a dictionary value is required."
//			alertError(controlError.alertError.find("p")[0], errorMessageTop)
//			alertError(controlError.alertError.find("p")[1], errorMessageIn)
//			valueFromDictionary.label.click()
//			cancelBtn.button.click()
//		}
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
//		if(inputData.DiplayValue != ""){
//			waitFor{ displayValue }
//			displayValue.value("")
//			displayValue.value(inputData.DiplayValue)
//			addOrUpdate(isCreate)
//			navigateBackToContext()
//		}else{
//			waitFor{ displayValue }
//			addOrUpdate(isCreate)
//			def displayValueAlert = "The valueDisplay can not be blank, this field is usually populated after using an auto-suggest drop-down."
//			alertError(controlError.helpInline, displayValueAlert)
//			cancelBtn.button.click()
//		}
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
//		if(inputData.NumericValue != ""){
//			waitFor{ numericValue }
//			qualifier.value(inputData.Qalifier)
//			numericValue.value("")
//			numericValue.value(inputData.NumericValue)
//			addOrUpdate(ci)
//			navigateBackToContext()
//		}else{
//		waitFor{ numericValue }
//			addOrUpdate(ci)
//			def displayValueAlert = "Please supply all required fields for a numeric value."
//			alertError(controlError.alertError.find("p")[0], displayValueAlert)
//			cancelBtn.button.click()
//		}
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
//		if(integration){
//			if(inputData.IntegratedSearch != ""){
//				waitFor{ integratedSearch }
//				selectAutoChoiceValue(integratedSearch, selectToDrop, inputData.IntegratedSearch)
//				addOrUpdate(isCreate)
//				navigateBackToContext()
//			}else{
//			waitFor{ integratedSearch }
////				integratedSearch.label.click()
//				addOrUpdate(isCreate)
//				def ontologyAlert = "When the attribute of an item expects an External Ontology reference, the fields External Ontology Id and Display Value must not be blank."
//				alertError(controlError.alertError.find("p")[0], ontologyAlert)
//				cancelBtn.button.click()
//			}
//
//		}else{
//			if(inputData.OntologyId != "" || inputData.DiplayValue != ""){
//				waitFor{ externalOntologyId }
////				integratedSearch.label.click()
//				externalOntologyId.value("")
//				displayValue.value("")
//				externalOntologyId.value(inputData.OntologyId)
//				displayValue.value(inputData.DiplayValue)
//				addOrUpdate(isCreate)
//				navigateBackToContext()
//			}else{
//			waitFor{ integratedSearch }
////				integratedSearch.label.click()
//				addOrUpdate(isCreate)
//				def ontologyAlert = "When the attribute of an item expects an External Ontology reference, the fields External Ontology Id and Display Value must not be blank."
//				alertError(controlError.alertError.find("p")[0], ontologyAlert)
//				cancelBtn.button.click()
//			}
//		}
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ integratedSearch }
		if(isCreate){
			if(integration){
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
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ integratedSearch
			attributeFromDictionary.selectClose }
			attributeFromDictionary.selectClose.click()
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}
		if(resultUpload){
			providedWithResults.click()
			if(valuConstraint=="Free"){
				isFreeConstraint()
				}else if(valuConstraint=="List"){
					valueConstraintList.click()
					isExternalOntologyType(inputData, isCreate, isIntegration)
					}
		}else{
			isExternalOntologyType(inputData, isCreate, isIntegration)
		}
	}
	
	def addNumericValueItem(def inputData, boolean isCreate=true, def resultUpload=false){
		if(isCreate){
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ numericValue
			attributeFromDictionary.selectClose }
			attributeFromDictionary.selectClose.click()
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}
		if(resultUpload){
			providedWithResults.click()
			if(valuConstraint=="Free"){
				isFreeConstraint()
			}else if(valuConstraint=="List"){
				valueConstraintList.click()
				isNumericType(inputData, isCreate)
			}else if(valuConstraint=="Range"){
				valueConstraintRange.click()
				valueMin.value(inputData.valueMin)
				valueMax.value(inputData.valueMax)
				addOrUpdate(ci)
				navigateBackToContext()
			}

		}else{
			isNumericType(inputData, isCreate)
		}
	}
	
	def addFreeTextItem(def inputData, boolean isCreate=true, def resultUpload=false){
		if(isCreate){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ displayValue
			attributeFromDictionary.selectClose }
			attributeFromDictionary.selectClose.click()
			selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}
		if(resultUpload){
			providedWithResults.click()
			if(valuConstraint=="Free"){
				isFreeConstraint()
			}else if(valuConstraint=="List"){
				valueConstraintList.click()
				isFreeTextTYpe(inputData, isCreate)
			}

		}else{
			isFreeTextTYpe(inputData, isCreate)
		}
	}
	def addElementContextItem(def inputData, boolean isCreate=true, def resultUpload=false){
		if(isCreate){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !selectToDrop.searchNoResult }
		selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.attribute)
		}else{
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ valueFromDictionary.selectChoice
			attributeFromDictionary.selectClose }
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
//		selectAutoChoiceValue(attributeFromDictionary, selectToDrop, inputData.AttributeFromDictionary)
//		if(resultUpload){
//			providedWithResults.click()
//			if(valuConstraint=="Free"){
//				isFreeConstraint()
//			}else if(valuConstraint=="List"){
//				valueConstraintList.click()
//				isElementType(inputData, isCreate)
//			}
//
//		}else{
//			isElementType(inputData, isCreate)
//		}
		if(inputData == ""){
			addOrUpdate(isCreate)
			def alertMessage = "Attribute cannot be null, please select an attribute."
			waitFor{ !selectToDrop.searchNoResult }
			alertError(controlError.helpInline, alertMessage)
//			attributeFromDictionary.label.click()
			cancelBtn.button.click()
		}
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