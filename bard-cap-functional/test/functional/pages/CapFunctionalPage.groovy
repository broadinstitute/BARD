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
	
/*	def UIContexts(def cardHolders){
		def uiContexts = []
		if(cardHolders.cardTable.find("caption")){
			cardHolders.cardTable.find("caption").each{ contextName ->
				uiContexts.add(contextName.text())
			}
		}
		return uiContexts
	}*/
}