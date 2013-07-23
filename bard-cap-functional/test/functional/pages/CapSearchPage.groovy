package pages

import geb.Page
import modules.ButtonsModule;
import modules.InputFieldsModule
import common.Constants.SearchBy

class CapSearchPage extends Page {
	static url = ""
	static at = {
		//		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by ID"
	}

	static content = {
		contentArea { $("div.bs-docs") }
		inputProjectId { module InputFieldsModule, contentArea, buttonName: "projectId" }
		inputProjectName { module InputFieldsModule, contentArea, buttonName: "projectName" }
		inputAssayId { module InputFieldsModule, contentArea, buttonName: "assayId" }
		inputAssayName { module InputFieldsModule, contentArea, buttonName: "assayName" }
		searchBtn { module ButtonsModule, contentArea, buttonName: "search" }
	}

	def capSearchBy(SearchBy by, def searchQuery){
		switch(by){
			case SearchBy.ASSAY_ID:
				assert inputAssayId.fieldButton
				assert searchBtn.inputSubmitPrimary
				inputAssayId.fieldButton << searchQuery
				searchBtn.inputSubmitPrimary.click()
				break;
			case SearchBy.ASSAY_NAME:
				assert inputAssayName.fieldButton
				assert searchBtn.inputSubmitPrimary
				inputAssayName.fieldButton << searchQuery
				searchBtn.inputSubmitPrimary.click()
				break;
			case SearchBy.PROJECT_ID:
				assert inputProjectId.fieldButton
				assert searchBtn.inputSubmitPrimary
				inputProjectId.fieldButton << searchQuery
				searchBtn.inputSubmitPrimary.click()
				break;
			case SearchBy.PROJECT_NAME:
				assert inputProjectName.fieldButton
				assert searchBtn.inputSubmitPrimary
				inputProjectName.fieldButton << searchQuery
				searchBtn.inputSubmitPrimary.click()
				break;
		}

	}
}