package pages

import geb.Page
import modules.CardsHolderModule
import modules.DocumentSectionModule
import modules.EditIconModule
import modules.EditableFormModule
import modules.ErrorInlineModule
import modules.SummaryModule

class CapScaffoldPage extends CommonFunctionalPage {
	static content = {
		summaryHeader { $("#summary-header") }
		summaryEdit {index -> module EditIconModule, viewSummary.ddValue[index] }
		editableForm { module EditableFormModule }
		viewSummary { module SummaryModule, summaryHeader }
		cardContainer { $("div.card.roundedBorder.card-table-container").find("table.table.table-hover") }
		cardTable{ contextTitle -> module CardsHolderModule, cardContainer, contextCard:contextTitle }
		controlError { module ErrorInlineModule }
		documentHeaders{ docType -> module DocumentSectionModule, documentType:docType }
	}

	
	def getUIContextItems(def card){
		def contextItems = []
		def resultMap = [:]
		if(isContextCardNotEmpty(card))
			if(cardTable(card).contextItemRows){
				cardTable(card).contextItemRows.each{
					resultMap = ['attributeLabel':it.find("td")[1].text(), 'valueDisplay':it.find("td")[2].text()==""?"null":it.find("td")[2].text()]
					contextItems.add(resultMap)
				}
			}
		return contextItems
	}
	boolean isContextCardNotEmpty(def cardName){
		boolean found = false
		if(isContext(cardName)){
			if(cardTable(cardName).contextItemRows){
				found = true
			}
		}
		return found
	}
	boolean isContext(def cardName){
		boolean found = false
		if(cardTable){
			cardTable.cardsTitle.each{ cards ->
				if(cards.text().contains(cardName)){
					found = true
				}
			}
		}
		return found
	}
	boolean isContextItem(def context, contextItem){
		boolean found = false
		if(isContext(context)){
			if(cardTable(context).attributeLabel(contextItem)){
				found = true
			}
		}
		return found
	}

	def editSummary(def indexValue, def editValue, def isCombo=false){
		def errorMessage = "Required and cannot be empty"
		assert summaryEdit(indexValue).editIconPencil
		summaryEdit(indexValue).editIconPencil.click()
		ajaxRequestCompleted()
		assert editableForm.buttons.iconOk
		assert editableForm.buttons.iconRemove
		if(isCombo){
			selectingComboValue(editValue)
		}else{
			if(editValue==""){
				fillInputField(editValue)
				validationError(controlError.helpBlock, errorMessage)
				editableForm.buttons.iconRemove.click()
			}else{
				fillInputField(editValue)
			}
		}
		ajaxRequestCompleted()
	}
	def selectingComboValue(def editValue){
		assert editableForm.selectInput
		editableForm.selectInput.value(editValue)
		editableForm.buttons.iconOk.click()
	}
	def fillInputField(def editValue){
		assert editableForm.inputField
		assert editableForm.buttons.iconOk
		editableForm.inputField.value("")
		editableForm.inputField.value(editValue)
		editableForm.buttons.iconOk.click()
	}
	def getUISummaryInfo(){
		def uiSummary = [:]
		for(int i; i < viewSummary.ddValue.size(); i++){
			uiSummary.put(viewSummary.dtLabel[i].text().trim().replace(" ", "").replace(":", ""), viewSummary.ddValue[i].text().trim()=="Empty"||""?"null":viewSummary.ddValue[i].text().trim())
		}
		return uiSummary
	}
	
	def navigateToCreateDocument(def document){
		assert document.addNewDocument.iconPlus
		document.addNewDocument.iconPlus.click()
	}

	def editDocument(def document, def docName, def editValue){
		def errorMessage = "Field is required and must not be empty"
		if(isDocument(document, docName)){
			assert document.documentContents(docName).editIconPencil
			document.documentContents(docName).editIconPencil.click()
			ajaxRequestCompleted()
			if(editValue==""){
				fillInputField(editValue)
				validationError(controlError.helpBlock, errorMessage)
				editableForm.buttons.iconRemove.click()
			}else{
				fillInputField(editValue)
			}
			ajaxRequestCompleted()
		}
	}

	def deleteDocument(def document, def docName){
		if(isDocument(document, docName)){
			assert document.documentContents(docName).iconTrash
			withConfirm { document.documentContents(docName).iconTrash.click() }
			ajaxRequestCompleted()
		}
	}

	boolean isDocument(def document, def docName){
		if(document.documentContents(docName)){
			return true
		}
		return false
	}

	def getUIDucuments(def document){
		def uiDocuments = []
		if(document.documentList){
			document.documentList.each { docList ->
				if(docList.find("span"))
					uiDocuments.add(docList.find("span").text().trim())
			}
		}
		return uiDocuments
	}
}