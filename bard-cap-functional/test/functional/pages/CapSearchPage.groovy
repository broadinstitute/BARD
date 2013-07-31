package pages

import geb.Page

import common.Constants.SearchBy

class CapSearchPage extends Page {
	static url = ""
	static at = {
		//		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by ID"
	}

	static content = {
		contentArea { $("div.bs-docs") }
		inputProjectId(wait: true, required: false) { contentArea.find("input", name:"projectId") }
		inputProjectName(wait: true, required: false) { contentArea.find("input", name:"projectName") }
		inputAssayId(wait: true, required: false) { contentArea.find("input", name:"assayId") }
		inputAssayName(wait: true, required: false) { contentArea.find("input", name:"assayName") }
		searchBtn(wait: true, required: false) { contentArea.find("input", name:"search") }
		
		resultAccordian { $("div#results_accordion") }
		accordianHeader { resultAccordian.find("h3") }
		gridTable { resultAccordian.find("table.gridtable") }
		resultTable  { gridTable.find("tbody").find("tr") }
	}

	def capSearchBy(SearchBy by, def searchQuery){
		switch(by){
			case SearchBy.ASSAY_ID:
				assert inputAssayId
				assert searchBtn
				inputAssayId << searchQuery
				searchBtn.click()
				break;
			case SearchBy.ASSAY_NAME:
				assert inputAssayName
				assert searchBtn
				inputAssayName << searchQuery
				searchBtn.click()
				break;
			case SearchBy.PROJECT_ID:
				assert inputProjectId
				assert searchBtn
				inputProjectId << searchQuery
				searchBtn.click()
				break;
			case SearchBy.PROJECT_NAME:
				assert inputProjectName
				assert searchBtn
				inputProjectName << searchQuery
				searchBtn.click()
				break;
		}

	}
	
	def searchResultCount(){
		return resultTable.size()
	}
	
	def seachResults(){
		def searchResult = []
		resultTable.each {columns ->
			def ids = columns.find("td")[0].text()
			searchResult.add(ids.toString())
		}
		return searchResult
	}
}