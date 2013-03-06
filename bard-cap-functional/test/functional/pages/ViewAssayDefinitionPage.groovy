package pages

import geb.Module;
import geb.Page

class ViewAssayDefinitionPage extends Page{
	static url=""
	static at = {  waitFor(10, 0.5){$("h4").text().contains("View Assay Definition")}}

	static content = {
		viewAssayDefinition {$("div", class:"pull-left").find("h4").text()}
		adId {$("div#target-summary-info").find("dd").text()}
		editAssayCotextBtn { $("a.btn.btn-primary", text:"Edit") }  // Edit asssay context btn
		editAssayMeasureBtn { $("a.btn.btn-small.btn-info", text:"Edit Measures") } // Edit Measure btn
		cardHolder {$("table.table.table-hover")}

		cardsHold { module AssayCardsHolding }
		capHeaders { module BardCapHeaderModule }
		
		assaySummay { module AssaySummaryModule }
	}
}

class AssayCardsHolding extends Module {
	static content = {
		cardcap{cardValue -> $("div.roundedBorder.card-group.assay-protocol--assay-component-").find("table.table.table-hover").find("caption").find("p", text:"$cardValue") }
	}
}

class AssaySummaryModule extends Module {
	static content = {
		summaryDetailsSection { $("div#summaryDetailSection") }
		assayName { summaryDetailsSection.find("dt", text:"Assay Name:") }
		assayDefinitionId { summaryDetailsSection.find("dt", text:"ID:") }
	}
}