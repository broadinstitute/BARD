package pages

import geb.Page
import geb.navigator.Navigator
import modules.LoadingModule

import common.Constants

public class CommonFunctionalPage extends Page {
	static url = ""
	static at = {}
	static content = {
		formLoading { module LoadingModule }
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

}