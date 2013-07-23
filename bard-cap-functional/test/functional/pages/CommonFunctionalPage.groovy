package pages

import geb.Page
import geb.navigator.Navigator
import modules.BardCapHeaderModule;
import modules.LoadingModule

import common.Constants
import common.Constants.NavigateTo;

public class CommonFunctionalPage extends Page {
	def seachADID = "Search by Assay Definition ID"
	def seachADN = "Search by Assay Definition Name"
	def seachPDN = "Search by Project Name"
	def seachPDID = "Search by Project ID"
	def createPD = "Create a New Project"
	def capMenu = "CAP"
	def ADMenu = "Assay Definitions"
	def projectMenu = "Projects"
	def webClientMenu = "Bard Web Client"
	
	static url = ""
	static at = {}
	static content = {
		formLoading { module LoadingModule }
		navigationMenu { module BardCapHeaderModule, $("ul.nav") }
	}

	boolean isAutocompleteListOk(element, condition){
		if(element){
			element.each { elementValue ->
				if(elementValue.text()){
					assert elementValue.text().toUpperCase().contains(condition.toUpperCase())
				}
			}
		}
	}

	boolean validationError(def element, def condition){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ element }
		if(element){
			if(element.text()){
				element.text().contains(condition)
				return true
			}
		}
		return false
	}

	boolean ajaxRequestCompleted(){
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ !formLoading.loading.displayed }
	}
	
	def navigateTo(NavigateTo to){
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
	}

	def navigation(def tabName, def menuName){
		assert navigationMenu.menuTab(tabName)
		navigationMenu.menuTab(tabName).click()
		assert navigationMenu.dropdownMenu(menuName)
		navigationMenu.dropdownMenu(menuName).click()
	}

}