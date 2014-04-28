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

import modules.CardsHolderModule
import modules.DocumentSectionModule
import modules.EditIconModule
import modules.EditableFormModule
import modules.ErrorInlineModule
import modules.SummaryModule



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

		editContext {sectionName -> module EditIconModule, $("#$sectionName") }
	}
	EditContextPage navigateToEditContext(def section){
		editContext(section).iconPencil.click()
		return new EditContextPage()
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
		if(isContextCardNotEmpty(cardGroup, card)){
			if(cardTable(cardGroup, card).contextItemRows){
				cardTable(cardGroup, card).contextItemRows.each{
					resultMap = ['attributeLabel':it.find("td")[1].text(), 'valueDisplay':it.find("td")[2].text()==""?"null":it.find("td")[2].text()]
					contextItems.add(resultMap)
				}
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
		waitFor { !editableForm.buttons }
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

	DocumentPage navigateToEditDocument(def document, def docName){
		if(isDocument(document, docName)){
			assert document.documentContents(docName).iconPencil
			document.documentContents(docName).iconPencil.click()
		}
		return new DocumentPage()
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
			//			ajaxRequestCompleted()
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
