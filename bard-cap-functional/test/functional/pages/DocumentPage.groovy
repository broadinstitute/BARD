package pages

import geb.Page
import modules.ButtonsModule

import common.Constants.DocumentAs

class DocumentPage extends Page{
	static url=""
	static at = { }

	static content = {
		documentName { $("input#documentName") }
		documentContent { $("div.nicEdit-main") }
		documentURL { $("input#documentContent") }
		cancelBtn { module ButtonsModule, $("div.control-group"), buttonName:"Cancel" }
		createBtn { module ButtonsModule, $("div.control-group"), buttonName:"Create" }

	}

	def createDocument(DocumentAs doc, def name, def content){
		assert documentName
		assert createBtn.inputSubmitButton
		switch(doc){
			case DocumentAs.CONTENTS:
				assert documentContent
				documentName.value(name)
				documentContent.value(content)
				createBtn.inputSubmitButton.click()
				break;
			case DocumentAs.URL:
				assert documentURL
				documentName.value(name)
				documentURL.value(content)
				createBtn.inputSubmitButton.click()
				break;
		}
	}
}