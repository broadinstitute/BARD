package pages

import geb.Page
import modules.SearchResultTableModule
import modules.SearchFieldsModule
import modules.AutocompleteResultModule
import pages.HomePage
import geb.Module

class FindAssayByNamePage extends CapFunctionalPage {
	def FIND_ASSAY_FIELD = "assayName"
	static url = "assayDefinition/findByName/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search assay by name"
	}


	static content = {
		assayResultAccordian { $("div#results_accordion").find("h3") }
		resultHolderTable { moduleList SearchResultTableModule, $("table.gridtable tr").tail() }
		assaysResults { index -> moduleList SearchResultTableModule, $("table.gridtable tr").tail(), index }
		assaySearchBtns { module SearchFieldsModule, searchBtns: FIND_ASSAY_FIELD }
		capHeaders { module BardCapHeaderModule }
		autocompleteItems { moduleList AutocompleteResultModule,  $("li.ui-menu-item")}
	}
	

}