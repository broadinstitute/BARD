package pages

import geb.Page
//import modules.SearchFieldsModule
import modules.AutocompleteResultModule
import modules.SearchResultTableModule
import geb.Module
import geb.navigator.Navigator
import pages.ViewProjectDefinitionPage

class FindProjectByNamePage extends Page {
//	def FIND_PROJECT_FIELD = "projectName"
	static url = "project/findByName/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by name"
	}

	static content = {
		contentArea { $("div.bs-docs") }
		projectResultAccordian { $("div#results_accordion").find("h3") }
		resultHolderTable { moduleList SearchResultTableModule, $("table.gridtable tr").tail() }
		projectResults { index -> moduleList SearchResultTableModule, $("table.gridtable tr").tail(), index }
		searchInput { module SearchFieldsModule, contentArea, buttonName: "projectName" }
		searchBtns { module SearchFieldsModule, contentArea, buttonName: "search" }
		projectAutocompleteItems { moduleList AutocompleteResultModule, $("li.ui-menu-item") }
		capHeaders { module BardCapHeaderModule }
	}
	
	def searchProjectByName(def searchQuery){
		assert searchInput.fieldButton
		assert searchBtns.fieldButton 
		searchInput.fieldButton << searchQuery
		searchBtns.fieldButton.click()
//		return new ViewProjectDefinitionPage()
	}
}