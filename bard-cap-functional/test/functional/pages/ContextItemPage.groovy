package pages

import java.awt.Label;

import geb.Page
import modules.ButtonsModule
import modules.CardsHolderModule
import modules.LoadingModule
import modules.SelectChoicePopupModule
import modules.SelectToDropModule

import common.Constants
import common.Constants.ContextItem
import common.Constants.ExpectedValueType

class ContextItemPage extends ContextScaffoldPage{
	static url=""
	//	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { $("h4").text().contains("Add a New Item") } }
	static content = {
		cardTable{ contextTitle -> module CardsHolderModule, $("div.card.roundedBorder.card-table-container").find("table.table.table-hover"), contextCard:contextTitle }
		formLoading { module LoadingModule}
		//		alertError { $("div.alert.alert-error") }
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
		selectToDrop { module SelectToDropModule, $("div.select2-drop.select2-drop-active") }
		controlError(wait: true, required: false) { $("div.control-group.error").find("div.controls").find("span.help-inline") }
	}

	def addEditContextItem(ContextItem ci, ExpectedValueType valueType, def inputData, def add=true, def integration=false){
		String qualifierReset = "Qualifier"
		if(inputData.AttributeFromDictionary != ""){
			if(add){
				attributeFromDictionary.label.click()
				attributeFromDictionary.selectChoice.click()
				fillContextItemForm(attributeFromDictionary, selectToDrop, inputData.AttributeFromDictionary)
			}
			switch(valueType){
				case ExpectedValueType.ELEMENT:
					if(inputData.ValueFromDictionary != ""){
						valueFromDictionary.label.click()
						if(valueFromDictionary.selectClose){
							valueFromDictionary.selectClose.click()
						}
						valueFromDictionary.selectChoice.click()
						fillContextItemForm(valueFromDictionary, selectToDrop, inputData.ValueFromDictionary)
					}else{
						valueFromDictionary.label.click()
					}
					break;
				case ExpectedValueType.FREE:
					if(inputData.DiplayValue != ""){
						displayValue.value("")
						displayValue.value(inputData.DiplayValue)
					}
					break;
				case ExpectedValueType.NUMERIC:
					if(inputData.NumericValue != ""){
						qualifier.value(inputData.Qalifier)
						numericValue.value("")
						numericValue.value(inputData.NumericValue)
					}
					break;
				case ExpectedValueType.ONTOLOGY:
					if(integration){
						if(inputData.IntegratedSearch != ""){
							integratedSearch.label.click()
							externalOntologyId.value("")
							displayValue.value("")
							if(integratedSearch.selectClose){
								integratedSearch.selectClose.click()
							}
							integratedSearch.selectChoice.click()
							fillContextItemForm(integratedSearch, selectToDrop, inputData.IntegratedSearch)
						}else{
							integratedSearch.label.click()
						}
					}else{
						if(inputData.OntologyId != "" || inputData.DiplayValue != ""){
							integratedSearch.label.click()
							externalOntologyId.value("")
							displayValue.value("")
							externalOntologyId.value(inputData.OntologyId)
							displayValue.value(inputData.DiplayValue)
						}else{
							integratedSearch.label.click()
						}
					}
					break;
			}
		}else{
			attributeFromDictionary.label.click()
		}
		switch(ci){
			case ContextItem.ADD:
				saveBtn.buttonSubmitPrimary.click()
				break;
			case ContextItem.UPDATE:
				updateBtn.buttonSubmitPrimary.click()
				break;
		}
		if(inputData.AttributeFromDictionary == ""){
			def alertMessage = "Attribute cannot be null, please select an attribute."
			alertError(alertMessage)
			attributeFromDictionary.label.click()
			cancelBtn.button.click()
		}else{
			switch(valueType){
				case ExpectedValueType.ELEMENT:
					if(inputData.ValueFromDictionary == ""){
						$("div.alert.alert-error").find("p")[0].text().contains("A value from the dictionary is required.")
						$("div.alert.alert-error").find("p")[1].text().contains("When the attribute of an item expects a BARD dictionary element, a dictionary value is required.")
						valueFromDictionary.label.click()
						cancelBtn.button.click()
					}else{
						backToContextBtn.button.click()
					}
					break;
				case ExpectedValueType.FREE:
					if(inputData.DiplayValue == ""){
						def displayValueAlert = "The valueDisplay can not be blank, this field is usually populated after using an auto-suggest drop-down."
						alertError(displayValueAlert)
						cancelBtn.button.click()
					}else{
						backToContextBtn.button.click()
					}
					break;
				case ExpectedValueType.NUMERIC:
					if(inputData.NumericValue == ""){
						def displayValueAlert = "Please supply all required fields for a numeric value."
						$("div.alert.alert-error").find("p")[0].text().contains(displayValueAlert)
						cancelBtn.button.click()
					}else{
						backToContextBtn.button.click()
					}
					break;
				case ExpectedValueType.ONTOLOGY:
					if(inputData.OntologyId == "" || inputData.DiplayValue == ""){
						def ontologyAlert = "When the attribute of an item expects an External Ontology reference, the fields External Ontology Id and Display Value must not be blank."
						$("div.alert.alert-error").find("p")[0].text().contains(ontologyAlert)
						cancelBtn.button.click()
					}else{
						backToContextBtn.button.click()
					}
					break;
			}
		}
	}
	boolean alertError(condition){
		if(controlError){
			if(controlError.text()){
				assert controlError.text().equalsIgnoreCase(condition)
				return true
			}
		}
		return false
	}
	def fillContextItemForm(def element1, def element2,  def inputValue){
		element1.selectChoice.click()
		int index = 0
		element1.searchInput.value(inputValue)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { element2.searchResult[index].text() != "Searching..." && element2.searchResult[index].text() != "Please enter 1 more characters"}
		element2.searchResult[index].click()
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { !formLoading.loading.displayed }
	}
}