package pages

import geb.Page
import geb.Module
import modules.CardsHolderModule
import geb.navigator.Navigator

public class CapFunctionalPage extends Page {
	static url = ""
	static at = {}
	static content = {
	
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
}