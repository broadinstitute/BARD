package pages

import geb.Page
import geb.navigator.Navigator
import modules.ButtonsModule
import modules.ErrorInlineModule

import common.Constants.DocumentAs

class DocumentPage extends CommonFunctionalPage{
	static url=""
	static at = { }

	static content = {
		documentName { $("input#documentName") }
		documentContent { $("div.nicEdit-main") }
		documentURL { $("input#documentContent", type:"url") }
		cancelBtn { module ButtonsModule, $("div.control-group"), buttonName:"Cancel" }
		createBtn { module ButtonsModule, $("div.control-group"), buttonName:"Create" }
		validations { module ErrorInlineModule }

	}

	def createDocument(DocumentAs doc, def name, def content){
		def validationErrorMessage = "Name is a required field"
		assert documentName
		assert createBtn.inputSubmitButton
		assert cancelBtn.button
		switch(doc){
			case DocumentAs.CONTENTS:
				assert documentContent
				if(name==""){
					fillDocumentForm(name, content)
					assert validationError(validations.helpInline, validationErrorMessage)
					cancelBtn.button.click()
				}else{
					fillDocumentForm(name, content)
				}
				break;
			case DocumentAs.URL:
				assert documentURL
				if(name==""){
					fillDocumentForm(name, content, true)
					assert validationError(validations.helpInline, validationErrorMessage)
					cancelBtn.button.click()
				}else{
					fillDocumentForm(name, content, true)
				}
				break;
		}
	}

	def fillDocumentForm(def name, def content, def urlType=false){
		documentName.value(name)
		if(urlType){
			documentURL.value(content)
		}else{
			documentContent.value(content)
		}
		createBtn.inputSubmitButton.click()
	}
}