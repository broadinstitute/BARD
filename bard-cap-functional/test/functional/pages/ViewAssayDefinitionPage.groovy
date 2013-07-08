package pages

import java.util.Map;

import geb.Module;
import geb.Page
import modules.SummaryModule
import modules.DialogButtonsSetModule
import modules.EditSummaryModule

class ViewAssayDefinitionPage extends Page{
	static url=""
	static at = {  waitFor(10, 0.5){$("h4").text().contains("View Assay Definition")}}

	static content = {
		viewAssayDefinition {$("div", class:"pull-left").find("h4").text()}
		adId {$("div#target-summary-info").find("dd").text()}
		editAssayCotextBtn { $("a.btn", text:"Edit") }  // Edit asssay context btn
		editAssayMeasureBtn { $("a.btn", text:"Edit Measures") } // Edit Measure btn
		cardHolder {$("table.table.table-hover")}
		editSummaryBtn { $("button#editSummaryButton") }
		cardHolders { module CardsHolderModule, $("div.roundedBorder.card-group.assay-protocol--assay-component-") }
		capHeaders { module BardCapHeaderModule }
		assaySummary { module SummaryModule, $("div#summaryDetailSection") }
		editAssaySummary { module EditSummaryModule, $("form#editSummaryForm") }
		dialogButtonset { module DialogButtonsSetModule, $("div.ui-dialog-buttonset") }
		
		addDocumentBtn { $("div#target-documents-info").find("a", text: "Add new document")}
	}

	def editSummay(def name, def assayStatus, def designedBy){
		editSummaryBtn.click()
		waitFor { editAssaySummary.ddValue(1) }
		editAssaySummary.ddValue(1).value("")
		editAssaySummary.ddValue(1).value(name)
		editAssaySummary.ddSelect(3).value(assayStatus)
		editAssaySummary.ddValue(5).value("")
		editAssaySummary.ddValue(5).value(designedBy)
		assert dialogButtonset.button[0]
		assert dialogButtonset.button[1]
		dialogButtonset.button[0].click()
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