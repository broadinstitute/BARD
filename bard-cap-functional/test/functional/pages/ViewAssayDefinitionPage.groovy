package pages

import geb.Page

import common.Constants

class ViewAssayDefinitionPage extends CapScaffoldPage{
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){$("h4").text().contains("View Assay Definition")}}

	static content = {
		//		editAssayCotextBtn { $("a.btn", text:"Edit") }  // Edit asssay context btn
		//		editAssayMeasureBtn { $("a.btn", text:"Edit Measures") } // Edit Measure btn
		//		cardHolder {$("table.table.table-hover")}
		//		cardHolders { module CardsHolderModule, $("div.roundedBorder.card-group.assay-protocol--assay-component-") }
		//		dialogButtonset { module DialogButtonsSetModule, $("div.ui-dialog-buttonset") }
	}

	def getUIContexts(){
		def uiContexts = []
		if(cardHolders.cardTable.find("caption")){
			cardHolders.cardTable.find("caption").each{ contextName ->
				def contexts = contextName.find("div.cardTitle").find("p")[0].text()
				uiContexts.add(contexts)
			}
		}
		return uiContexts
	}

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