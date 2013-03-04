package pages

import geb.Page

class FindAssayByIdPage extends Page {
	static url = "assayDefinition/findById/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search assay by ID"
	}

	static content = {
		assaySearchBtns {name -> module SearchFieldsModule, searchBtns: name }
	}

}