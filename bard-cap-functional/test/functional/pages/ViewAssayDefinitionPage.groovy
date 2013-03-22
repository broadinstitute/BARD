package pages

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
		editAssayCotextBtn { $("a.btn.btn-primary", text:"Edit") }  // Edit asssay context btn
		editAssayMeasureBtn { $("a.btn.btn-small.btn-info", text:"Edit Measures") } // Edit Measure btn
		cardHolder {$("table.table.table-hover")}
		editSummaryBtn { $("button#editSummaryButton") } 
		cardsHold { module AssayCardsHolding }
		capHeaders { module BardCapHeaderModule }
		assaySummary { module SummaryModule, $("div#summaryDetailSection") }
		editAssaySummary { module EditSummaryModule, $("form#editSummaryForm") }
		dialogButtonset { module DialogButtonsSetModule, $("div.ui-dialog-buttonset") }
	}
	
	def editSummay(def name, def assayStatus, def designedBy){
		editSummaryBtn.click()
		waitFor { editAssaySummary.ddValue(1) }
		editAssaySummary.ddValue(1).value("")
		editAssaySummary.ddValue(1).value(name)
		editAssaySummary.ddSelect(3).value(assayStatus)
		editAssaySummary.ddValue(4).value("")
		editAssaySummary.ddValue(4).value(designedBy)
		assert dialogButtonset.button[0]
		assert dialogButtonset.button[1]
		dialogButtonset.button[0].click()
	}
}

class AssayCardsHolding extends Module {
	static content = {
		cardcap{cardValue -> $("div.roundedBorder.card-group.assay-protocol--assay-component-").find("table.table.table-hover").find("caption").find("p", text:"$cardValue") }
	}
}