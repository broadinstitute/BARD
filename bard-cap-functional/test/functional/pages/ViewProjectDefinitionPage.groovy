package pages

import geb.Module
import geb.error.RequiredPageContentNotPresent
import geb.waiting.WaitTimeoutException
import modules.BardCapHeaderModule
import modules.ButtonsModule
import modules.DocumentSectionModule
import modules.EditIconModule
import modules.EditableFormModule
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
		projectSummaryStatusEdit {module EditIconModule, viewprojectSummary.ddValue[Constants.index_1] }
		projectSummaryNameEdit {module EditIconModule, viewprojectSummary.ddValue[Constants.index_2] }
		projectSummaryDescriptionEdit {module EditIconModule, viewprojectSummary.ddValue[Constants.index_3] }
		formLoading { module LoadingModule}
		editableForm { module EditableFormModule }

		documentHeaders{ docType -> module DocumentSectionModule, documentType:docType }

	}

	def editSummay(ProjectSummaryEdit edit, def editValue){
		def errorMessage = "Required and cannot be empty"
		switch(edit){
			case ProjectSummaryEdit.STATUS:
				try{
					assert projectSummaryStatusEdit.editIconPencil
					projectSummaryStatusEdit.editIconPencil.click()
					waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.editableFormLoading.displayed }
					assert editableForm.selectInput
					assert editableForm.submitButton
					assert editableForm.cancelButton
					editableForm.selectInput.value(editValue)
					editableForm.submitButton.click()
					waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
					!editableForm.selectInput
					!editableForm.submitButton
					!editableForm.cancelButton

				}catch(RequiredPageContentNotPresent c){
				}catch(IllegalArgumentException e){
				}catch(WaitTimeoutException w){
				}
				break;
			case ProjectSummaryEdit.NAME:
				try{
					assert projectSummaryNameEdit.editIconPencil
					projectSummaryNameEdit.editIconPencil.click()
					waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.editableFormLoading.displayed }
					assert editableForm.inputField
					assert editableForm.submitButton
					assert editableForm.cancelButton
					if(editValue==""){
						editableForm.inputField.value("")
						editableForm.inputField.value(editValue)
						editableForm.submitButton.click()
						waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ editableForm.errorMessage }
						assert editableForm.errorMessage.text() == errorMessage
						editableForm.cancelButton.click()
					}else{
						editableForm.inputField.value("")
						editableForm.inputField.value(editValue)
						editableForm.submitButton.click()
					}
					waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
					!editableForm.inputField
					!editableForm.submitButton
					!editableForm.cancelButton

				}catch(RequiredPageContentNotPresent c){
				}catch(IllegalArgumentException e){
				}catch(WaitTimeoutException w){
				}
				break;
			case ProjectSummaryEdit.DESCRIPTION:
				try{
					assert projectSummaryDescriptionEdit.editIconPencil
					projectSummaryDescriptionEdit.editIconPencil.click()
					waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.editableFormLoading.displayed }
					assert editableForm.inputField
					assert editableForm.submitButton
					assert editableForm.cancelButton
					if(editValue==""){
						editableForm.inputField.value("")
						editableForm.inputField.value(editValue)
						editableForm.submitButton.click()
						waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ editableForm.errorMessage }
						assert editableForm.errorMessage.text() == errorMessage
						editableForm.cancelButton.click()
					}else{
						editableForm.inputField.value("")
						editableForm.inputField.value(editValue)
						editableForm.submitButton.click()
					}
					waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
					!editableForm.inputField
					!editableForm.submitButton
					!editableForm.cancelButton

				}catch(RequiredPageContentNotPresent c){
				}catch(IllegalArgumentException e){
				}catch(WaitTimeoutException w){
				}
				break;
		}
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

	def editDocument(def document, def editForm, def docName, def editValue){
		def errorMessage = "Field is required and must not be empty"
		if(isDocument(document, docName)){
			assert document.documentContents(docName).editIconPencil
			document.documentContents(docName).editIconPencil.click()
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
			assert editForm.inputField
			assert editForm.submitButton
			assert editForm.cancelButton
			if(editValue==""){
				editForm.inputField.value("")
				editForm.inputField.value(editValue)
				editForm.submitButton.click()
				waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ editForm.errorMessage }
				assert editForm.errorMessage.text() == errorMessage
				editForm.cancelButton.click()
			}else{
				editForm.inputField.value("")
				editForm.inputField.value(editValue)
				editForm.submitButton.click()
			}
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
			!editForm.inputField
			!editForm.submitButton
			!editForm.cancelButton
		}
	}

	def deleteDocument(def document, def docName){
		if(isDocument(document, docName)){
			assert document.documentContents(docName).iconTrash
			withConfirm { document.documentContents(docName).iconTrash.click() }
			waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
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

	String documentSection(def docType){
		return docType+":"
	}
}

class ExperimentBtnsModule extends Module {
	static content = {
		addExperimentBtn { $("button#addExperimentToProject") }
		linkExperimentBtn { $("button#linkExperiment") }
		redrawExperimentBtn { $("button#redraw") }
	}
}