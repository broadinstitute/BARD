package modules

import geb.Module
import modules.EditIconModule
import geb.navigator.Navigator

class DocumentSectionModule extends Module {
	def documentType
	static content = {
		documentHeader { $("#documents-"+documentType+"-header") }
		addNewDocument { module EditIconModule, documentHeader(documentType) }
		documentList(wait: true, required: false) { documentHeader(documentType).find("div.borderlist") }
		documentBorderList(wait: true, required: false) { documentName -> documentList.find("span", text:contains(documentName)).parent() }
		documentContents(wait: true, required: false) { documentName -> module EditIconModule, documentBorderList(documentName) }
	}
}