package pages

import geb.Page
import modules.CardsHolderModule
import modules.DocumentSectionModule
import modules.EditIconModule
import modules.EditableFormModule
import modules.ErrorInlineModule
import modules.SummaryModule
import common.Constants

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class CapScaffoldPage extends CommonFunctionalPage {
	static content = {
		summaryHeader { $("#summary-header") }
		summaryEdit {index -> module EditIconModule, viewSummary.ddValue[index] }
		editableForm { module EditableFormModule }
		viewSummary { module SummaryModule, summaryHeader }

		cardContainer(required: false) { groupName -> $("#$groupName") }
		contextTable { groupName -> cardContainer(groupName).find("table.table.table-hover")}
		cardTable{ groupName, contextTitle -> module CardsHolderModule, contextTable(groupName), contextCard:contextTitle }
		contextCards{ groupName -> module CardsHolderModule, cardContainer(groupName) }
		controlError { module ErrorInlineModule }
		documentHeaders{ docType -> module DocumentSectionModule, documentType:docType }

		//		header { sectionName -> $("#"+sectionName+"-header") }
		//		editContext {sectionName -> module EditIconModule, header(sectionName) }
		editContext {sectionName -> module EditIconModule, $("#$sectionName") }
	}
	def navigateToEditContext(def section){
		editContext(section).iconPencil.click()
	}
	def getUIContexts(def cardGroup){
		def contexts = []
		if(cardContainer(cardGroup)){
			if(contextCards(cardGroup).cardsTitle){
				contextCards(cardGroup).cardsTitle.each{ cards ->
					if(cards.find("p")[0].text()){
						contexts.add(cards.find("p")[0].text())
					}else{
						contexts.add(cards.text())
					}
				}
			}
		}
		return contexts
	}

	def getUIContextItems(def cardGroup, def card){
		def contextItems = []
		def resultMap = [:]
		if(isContextCardNotEmpty(cardGroup, card))
			if(cardTable(cardGroup, card).contextItemRows){
				cardTable(cardGroup, card).contextItemRows.each{
					resultMap = ['attributeLabel':it.find("td")[1].text(), 'valueDisplay':it.find("td")[2].text()==""?"null":it.find("td")[2].text()]
					contextItems.add(resultMap)
				}
			}
		return contextItems
	}
	boolean isContextCardNotEmpty(def cardGroup, def cardName){
		boolean found = false
		if(isContext(cardGroup, cardName)){
			if(cardTable(cardGroup, cardName).contextItemRows){
				found = true
			}
		}
		return found
	}
	boolean isContext(def cardGroup, def cardName){
		boolean found = false
		if(cardContainer(cardGroup)){
			if(contextCards(cardGroup)){
				contextCards(cardGroup).cardsTitle.each{ cards ->
					if(cards.text().contains(cardName)){
						found = true
					}
				}
			}
		}
		return found
	}
	boolean isContextItem(def cardGroup, def context, contextItem){
		boolean found = false
		if(isContext(cardGroup, context)){
			if(cardTable(cardGroup, context).attributeLabel(contextItem)){
				found = true
			}
		}
		return found
	}

	def editSummary(def indexValue, def editValue, def isCombo=false){
		def errorMessage = "Required and cannot be empty"
		assert summaryEdit(indexValue).editIconPencil
		summaryEdit(indexValue).editIconPencil.click()
		waitFor { editableForm.buttons.iconOk }
		waitFor { editableForm.buttons.iconRemove }
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
		waitFor { !editableForm.buttons.iconOk }
		waitFor { !editableForm.buttons.iconRemove }
		//		ajaxRequestCompleted()
	}

	def editDate(def indexValue, def runDate){
		def errorMessage = "Required and cannot be empty"
		assert summaryEdit(indexValue).editIconPencil
		summaryEdit(indexValue).editIconPencil.click()
		waitFor { editableForm.buttons.iconOk }
		waitFor { editableForm.buttons.iconRemove }

		assert editableForm.selectDay
		assert editableForm.selectMonth
		assert editableForm.selectYear

		editableForm.selectDay.value(runDate.day)
		editableForm.selectMonth.value(runDate.month)
		editableForm.selectYear.value(runDate.year)
		editableForm.buttons.iconOk.click()

		waitFor { !editableForm.buttons.iconOk }
		waitFor { !editableForm.buttons.iconRemove }
	}
	void selectingComboValue(def editValue){
		assert editableForm.selectInput
		editableForm.selectInput.value(editValue)
		editableForm.buttons.iconOk.click()
	}
	void fillInputField(def editValue){
		assert editableForm.inputTextArea
		assert editableForm.buttons.iconOk
		editableForm.inputTextArea.value("")
		editableForm.inputTextArea.value(editValue)
		editableForm.buttons.iconOk.click()
	}
	def getUISummaryInfo(){
		def uiSummary = [:]
		for(int i; i < viewSummary.ddValue.size(); i++){
			def label = viewSummary.dtLabel[i].text().trim().replaceAll(" ", "")
			def value = viewSummary.ddValue[i].text().trim()
			if(label.contains('(')){
				label = label.substring(0, label.indexOf('('))//.replace(":", "").replace(" ", "")

			}
			if(label.contains(':')){
				label = label.replace(":", "")
			}
			uiSummary.put(label, value=="Empty"||""?"null":value)
		}
		return uiSummary
	}

	DocumentPage navigateToCreateDocument(def document){
		assert document.addNewDocument.iconPlus
		document.addNewDocument.iconPlus.click()
		
		return new DocumentPage()
	}

	def navigateToEditDocument(def document, def docName){
		if(isDocument(document, docName)){
			assert document.documentContents(docName).iconPencil
			document.documentContents(docName).iconPencil.click()
		}
	}

	def editDocument(def document, def docName, def editValue){
		def errorMessage = "Field is required and must not be empty"
		if(isDocument(document, docName)){
			assert document.documentContents(docName).editIconPencil
			document.documentContents(docName).editIconPencil.click()
			ajaxRequestCompleted()
			if(editValue==""){
				fillFieldsInput(editValue)
				validationError(controlError.helpBlock, errorMessage)
				editableForm.buttons.iconRemove.click()
			}else{
				fillFieldsInput(editValue)
			}
			//			ajaxRequestCompleted()
		}
	}

	def fillFieldsInput(def editValue){
		assert editableForm.inputField
		assert editableForm.buttons.iconOk
		editableForm.inputField.value("")
		editableForm.inputField.value(editValue)
		editableForm.buttons.iconOk.click()
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
