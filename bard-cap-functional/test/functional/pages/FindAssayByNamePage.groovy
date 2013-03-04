package pages

import geb.Page
import pages.HomePage
import geb.Module

class FindAssayByNamePage extends Page {
	static url = "assayDefinition/findByName/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search assay by name"
	}


	static content = {
		assayResultAccordian { $("div#results_accordion").find("h3") }
		resultHolderTable { $("table.gridtable").find("tr") }
		
		assaysResults { index -> moduleList SearchResultTable, $("table.gridtable tr").tail(), index }
		
		assaySearchBtns {name -> module SearchFieldsModule, searchBtns: name }
		capHeaders { module BardCapHeaderModule }
		
		autocompleteItems { moduleList AutocompleteResult, $("li.ui-menu-item") }
	}

	boolean isAssayAutocomplete(element, condition){
		if(element){
			element.each { elementValue ->
				if(elementValue.text()){
					assert elementValue.text().toUpperCase().contains(condition.toUpperCase())
				}
			}
		}
	}

}


/*
class AutocompleteResult extends Module {
	static content = {
		items { $("a", it) }
		itemsList { items(0) }
		itemsListSize { itemsList.size() }
		
		loading { $("div#spinner") }
		itemsLoaded { !Loading.displayed }
	}
}

class AssayResultTable extends Module {
	static content = {
		cell { $("td", it) }
		assayId { cell(0).find("a") }
		shortName { cell(1).text() }
		assayName { cell(2).text() }
		designedBy { cell(3).text() }
	}
}
*/