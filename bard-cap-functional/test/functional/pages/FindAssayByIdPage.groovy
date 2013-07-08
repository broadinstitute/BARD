package pages

import geb.Page
//import modules.SearchFieldsModule

class FindAssayByIdPage extends Page {
	def FIND_ASSAY_FIELD = "assayId"
	static url = "assayDefinition/findById/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search assay by ID"
	}

	static content = {
		assaySearchBtns { module SearchFieldsModule, searchBtns: FIND_ASSAY_FIELD }
	}

}