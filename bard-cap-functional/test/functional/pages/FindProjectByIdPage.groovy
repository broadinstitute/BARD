package pages

import geb.Page
import modules.SearchFieldsModule

class FindProjectByIdPage extends Page {
	def FIND_FIELD_ID = "projectId"
	static url = "project/findById/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by ID"
	}

	static content = {
		projectSearchBtns { module SearchFieldsModule, searchBtns: FIND_FIELD_ID }
	}
}