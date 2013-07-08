package pages

import geb.Module
import geb.Page
import modules.BardCapHeaderModule
//import constutil.GlobalObjects.NavigateTo
import common.Constants.NavigateTo
import common.Constants

class HomePage extends Page {
	static url = ""
	static at = {
		$("h3").text() ==~ "CAP - Catalog of Assay Protocols"
	}

	static content = {
		logOut { $("form#logoutForm").find("button") }
		bardBrandLogo { $("a.brand") }
		navigationMenu {index -> moduleList BardCapHeaderModule, $("ul.nav").children(), index }
	}

	def navigateTo(NavigateTo to){
		switch(to){
			case NavigateTo.ASSAY_BY_ID:
				navigation(Constants.index_2, Constants.index_0)
				break;
			case NavigateTo.ASSAY_BY_NAME:
				navigation(Constants.index_2, Constants.index_1)
				break;
			case NavigateTo.PROJECT_BY_ID:
				navigation(Constants.index_3, Constants.index_0)
				break;
			case NavigateTo.PROJECT_BY_NAME:
				navigation(Constants.index_3, Constants.index_1)
				break;
		}
	}
	def navigation(def tabIndex, def menuIndex){
		assert navigationMenu(tabIndex).menuTab
		navigationMenu(tabIndex).menuTab.click()
		assert navigationMenu(tabIndex).dropdownMenu(menuIndex)
		navigationMenu(tabIndex).dropdownMenu(menuIndex).click()
	}
}