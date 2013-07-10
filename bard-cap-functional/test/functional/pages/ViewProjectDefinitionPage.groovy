package pages

import geb.Module
import geb.error.RequiredPageContentNotPresent
import geb.waiting.WaitTimeoutException
import modules.BardCapHeaderModule
import modules.ButtonsModule
import modules.DocumentSectionModule
import modules.EditIconModule
import modules.EditableFormModule
import modules.ErrorInlineModule;
import modules.LoadingModule
import modules.SummaryModule

import common.Constants
import common.Constants.ProjectSummaryEdit

class ViewProjectDefinitionPage extends ContextScaffoldPage{
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	$("h4").text().contains("View Project")} }

	static content = {
		summaryHeader { $("#summary-header") }
		contextHeader { $("#contexts-header") }
		projectContextEdit { module ButtonsModule, contextHeader, buttonName:"Edit Contexts" }
		capHeaders { module BardCapHeaderModule }
		experimentBtns { module ExperimentBtnsModule }
		viewprojectSummary { module SummaryModule, summaryHeader }
		projectSummaryEdit {index -> module EditIconModule, viewprojectSummary.ddValue[index] }
		editableForm { module EditableFormModule }
		controlError { module ErrorInlineModule }
		documentHeaders{ docType -> module DocumentSectionModule, documentType:docType }
	}

	def editSummary(def indexValue, def editValue, def editCombo=false){
		def errorMessage = "Required and cannot be empty"
		assert projectSummaryEdit(indexValue).editIconPencil
		projectSummaryEdit(indexValue).editIconPencil.click()
		ajaxRequestCompleted()
		assert editableForm.submitButton
		assert editableForm.cancelButton
		if(editCombo){
			selectingComboValue(editValue)
		}else{
			if(editValue==""){
				fillInputField(editValue)
				validationError(controlError.helpBlock, errorMessage)
				editableForm.cancelButton.click()
			}else{
				fillInputField(editValue)
			}
		}
		ajaxRequestCompleted()
		!editableForm.selectInput
		!editableForm.submitButton
		!editableForm.cancelButton
		!editableForm.selectInput
	}

	def fillInputField(def editValue){
		assert editableForm.inputField
		assert editableForm.submitButton
		editableForm.inputField.value("")
		editableForm.inputField.value(editValue)
		editableForm.submitButton.click()
	}
	def selectingComboValue(def editValue){
		assert editableForm.selectInput
		editableForm.selectInput.value(editValue)
		editableForm.submitButton.click()
	}

	Map<String, String> getUISummaryInfo(){
		def uiProjectSummary = [:]
		for(int i; i < viewprojectSummary.ddValue.size(); i++){
			uiProjectSummary.put(viewprojectSummary.dtLabel[i].text().trim().replace(" ", "").replace(":", ""), viewprojectSummary.ddValue[i].text().trim()=="Empty"||""?"null":viewprojectSummary.ddValue[i].text().trim())
		}
		return uiProjectSummary
	}

	def navigateToEditContextPage(){
		assert projectContextEdit.button
		projectContextEdit.button.click()
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
				editableForm.cancelButton.click()
			}else{
				fillInputField(editValue)
			}
			ajaxRequestCompleted()
			!editableForm.inputField
			!editableForm.submitButton
			!editableForm.cancelButton
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

	List<String> getUIDucuments(def document){
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

class ExperimentBtnsModule extends Module {
	static content = {
		addExperimentBtn { $("button#addExperimentToProject") }
		linkExperimentBtn { $("button#linkExperiment") }
		redrawExperimentBtn { $("button#redraw") }
	}
}