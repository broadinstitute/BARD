package pages

import geb.Page
import pages.HomePage
import geb.Module

class FindAssayByNamePage extends Page {
	def FIND_ASSAY_FIELD = "assayName"
	static url = "assayDefinition/findByName/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search assay by name"
	}


	static content = {
		assayResultAccordian { $("div#results_accordion").find("h3") }
		resultHolderTable { $("table.gridtable").find("tr") }
		
		assaysResults { index -> moduleList SearchResultTable, $("table.gridtable tr").tail(), index }
		
		assaySearchBtns { module SearchFieldsModule, searchBtns: FIND_ASSAY_FIELD }
		capHeaders { module BardCapHeaderModule }
		
		autocompleteItems { moduleList AutocompleteResult,  $("li.ui-menu-item")}
	}
	
	boolean isAutocompleteListOk(element, condition){
		if(element){
			element.each { elementValue ->
				if(elementValue.text()){
					assert elementValue.text().toUpperCase().contains(condition.toUpperCase())
				}
			}
		}
	}
}