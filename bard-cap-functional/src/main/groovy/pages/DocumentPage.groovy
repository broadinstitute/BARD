package main.groovy.pages

import main.groovy.modules.ButtonsModule;
import main.groovy.modules.ErrorInlineModule;


/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Date Updated: 13/10/30
 */
class DocumentPage extends CommonFunctionalPage{
	static url=""
	static at = { $("input#documentType") }

	static content = {
		documentName { $("#documentName") }
		documentContent() { $("#documentContent") }
		//		documentURL { $("input#documentContent", type:"url") }
		cancelBtn { module ButtonsModule, $("div.control-group"), buttonName:"Cancel" }
		createBtn { module ButtonsModule, $("div.control-group"), buttonName:"Create" }
		updateBtn { module ButtonsModule, $("div.control-group"), buttonName:"Update" }
		validations { module ErrorInlineModule }

	}

	def createDocument(def name, def content, boolean isUpdate = false){
		def validationErrorMessage = "Name is a required field"
		assert documentName
		//		assert createBtn.inputSubmitButton
		assert cancelBtn.button
		assert documentContent
		if(name==""){
			fillDocumentForm(name, content, isUpdate)
//			assert validationError(validations.helpInline, validationErrorMessage)
			cancelBtn.button.click()
		}else{
			fillDocumentForm(name, content, isUpdate)
		}
	}

	def fillDocumentForm(def name, def content, def urlType=false, def isUpdate){
		documentName.value(name)
		documentContent.value(content)
		if(isUpdate){
			updateBtn.inputSubmitButton.click()
		}else{
			createBtn.inputSubmitButton.click()
		}
	}
}