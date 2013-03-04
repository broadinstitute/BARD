package pages

import com.google.common.cache.LoadingCache;

import geb.Page
import pages.HomePage
import geb.Module
import geb.navigator.Navigator;

class FindProjectByNamePage extends Page {
	static url = "project/findByName/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by name"
	}

	static content = {
		projectResultAccordian { $("div#results_accordion").find("h3") }
		resultHolderTable { $("table.gridtable").find("tr") }
		
		projectResults { index -> moduleList SearchResultTable, $("table.gridtable tr").tail(), index }
		
		projectSearchBtns { name -> module SearchFieldsModule, searchBtns: name }
		projectAutocompleteItems { moduleList AutocompleteResult, $("li.ui-menu-item") }
		
		capHeaders { module BardCapHeaderModule }
		
	//	autocompleteItems { moduleList AutocompleteResult, $("li.ui-menu-item") }
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