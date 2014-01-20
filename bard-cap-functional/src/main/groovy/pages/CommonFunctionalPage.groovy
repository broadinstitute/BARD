package main.groovy.pages

import geb.Page
import main.groovy.common.Constants.NavigateTo
import main.groovy.modules.BardCapHeaderModule
import main.groovy.modules.LoadingModule

public class CommonFunctionalPage extends Page {
//	def seachADID = "Search by Assay Definition ID"
//	def seachADN = "Search by Assay Definition Name"
//	def seachPDN = "Search by Project Name"
//	def seachPDID = "Search by Project ID"
//	def createPD = "Create a New Project"
//	def capMenu = "CAP"
//	def ADMenu = "Assay Definitions"
//	def projectMenu = "Projects"
//	def webClientMenu = "Bard Web Client"
	
	static url = ""
	static at = {}
	static content = {
		formLoading { module LoadingModule }
		navigationMenu { module BardCapHeaderModule, $("ul.nav") }
	}

	boolean validationError(def element, def condition){
		waitFor{ element }
		if(element){
			if(element.text()){
				element.text().contains(condition)
				return true
			}
		}
		return false
	}

	boolean ajaxRequestCompleted(){
		waitFor { !formLoading.loading.displayed }
	}
	
	/*def navigateTo(NavigateTo to){
		switch(to){
			case NavigateTo.ASSAY_BY_ID:
				navigation(ADMenu, seachADID)
				break;
			case NavigateTo.ASSAY_BY_NAME:
				navigation(ADMenu, seachADN)
				break;
			case NavigateTo.PROJECT_BY_ID:
				navigation(projectMenu, seachPDID)
				break;
			case NavigateTo.PROJECT_BY_NAME:
				navigation(projectMenu, seachPDN)
				break;

		}
	}*/

	def navigation(def tabName, def menuName){
		assert navigationMenu.menuTab(tabName)
		navigationMenu.menuTab(tabName).click()
		assert navigationMenu.dropdownMenu(menuName)
		navigationMenu.dropdownMenu(menuName).click()
	}
	void waitForPageToLoad() {
		waitFor(15, 0.5) { title.contains("BARD: Catalog of Assay Protocols") }
	}
}
