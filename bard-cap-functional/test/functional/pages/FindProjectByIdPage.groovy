package pages

import geb.Page
//import modules.SearchFieldsModule

class FindProjectByIdPage extends Page {
	static url = "project/findById/"
	static at = {
		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by ID"
	}

	static content = {
		contentArea { $("div.bs-docs") }
		searchInput { module SearchFieldsModule, contentArea, buttonName: "projectId" }
		searchBtn { module SearchFieldsModule, contentArea, buttonName: "search" }
	}
	
	def searchProject(def searchQuery){
		assert searchInput.fieldButton
		assert searchBtn.fieldButton
		searchInput.fieldButton << searchQuery
		searchBtn.fieldButton.click()
	}
}