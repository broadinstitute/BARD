package pages

import com.google.common.cache.LoadingCache;

import geb.Page
import modules.SearchFieldsModule
import modules.AutocompleteResultModule
import modules.SearchResultTableModule
import pages.HomePage
import geb.Module
import geb.navigator.Navigator;

class FindProjectByNamePage extends CommonFunctionalPage {
	def FIND_PROJECT_FIELD = "projectName"
	static url = "project/findByName/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by name"
	}

	static content = {
		projectResultAccordian { $("div#results_accordion").find("h3") }
		resultHolderTable { $("table.gridtable").find("tr") }
		
		projectResults { index -> moduleList SearchResultTableModule, $("table.gridtable tr").tail(), index }
		
		projectSearchBtns { module SearchFieldsModule, searchBtns: FIND_PROJECT_FIELD }
		projectAutocompleteItems { moduleList AutocompleteResultModule, $("li.ui-menu-item") }
		
		capHeaders { module BardCapHeaderModule }
	}
}