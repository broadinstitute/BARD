package pages

import geb.Page
import modules.EditIconModule

import common.Constants

class ViewAssayDefinitionPage extends CapScaffoldPage{
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){$("h4").text().contains("View Assay Definition")}}

	static content = {
		//		editAssayMeasureBtn { $("a.btn", text:"Edit Measures") } // Edit Measure btn
		header { sectionName -> $("#"+sectionName+"-header") }
		editContext {sectionName -> module EditIconModule, header(sectionName) }
	}

	def navigateToEditContext(def section){
		editContext(section).iconPencil.click()
	}
	
	/*def getUIContexts(){
		def uiContexts = []
		if(cardHolders.cardTable.find("caption")){
			cardHolders.cardTable.find("caption").each{ contextName ->
				def contexts = contextName.find("div.cardTitle").find("p")[0].text()
				uiContexts.add(contexts)
			}
		}
		return uiContexts
	}*/

	Map<String, String> getUIAssociatedMeasure(def cardName){
		def contextMeasure = [:]
		if(cardHolders.cardTable){
			cardHolders.cardTable.each{ cards ->
				def cardCaption = cards.find("div.cardTitle").find("p")
				if(cardCaption[0].text().equalsIgnoreCase(cardName) && cardCaption[1]){
					def uiMeasure = cardCaption[1].find("a").text()
					def measure = uiMeasure.takeWhile { it != '('}.trim()
					contextMeasure = ['measure':measure, 'context':cardCaption[0].text()]
				}
			}
		}
		return contextMeasure
	}
}