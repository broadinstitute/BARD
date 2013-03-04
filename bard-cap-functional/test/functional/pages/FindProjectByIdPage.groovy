package pages

import geb.Page
import pages.CAPUtilPage

class FindProjectByIdPage extends Page {
	static url = "project/findById/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by ID"
	}

	static content = {
		projectSearchBtns {name -> module SearchFieldsModule, searchBtns: name }
	}
}