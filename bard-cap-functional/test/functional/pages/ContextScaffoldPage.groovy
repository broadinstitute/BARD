package pages

import common.Constants;

import geb.Page
import modules.CardsHolderModule

class ContextScaffoldPage extends CommonFunctionalPage {
    static content = {
		cardTable{ contextTitle -> module CardsHolderModule, $("div.card.roundedBorder.card-table-container").find("table.table.table-hover"), contextCard:contextTitle }
    }

    
	def getUIContextItems(def card){
		def contextItems = []
		def resultMap = [:]
		if(isContextCardNotEmpty(card))
			if(cardTable(card).contextItemRows){
				cardTable(card).contextItemRows.each{
					resultMap = ['attributeLabel':it.find("td")[1].text(), 'valueDisplay':it.find("td")[2].text()]
					contextItems.add(resultMap)
				}
			}
		return contextItems
	}
	boolean isContextCardNotEmpty(def cardName){
		boolean found = false
		if(isContext(cardName)){
			if(cardTable(cardName).contextItemRows){
				found = true
			}
		}
		return found
	}
	boolean isContext(def cardName){
		boolean found = false
		if(cardTable){
			cardTable.cardsTitle.each{ cards ->
				if(cards.text().contains(cardName)){
					found = true
				}
			}
		}
		return found
	}
	boolean isContextItem(def context, contextItem){
		boolean found = false
		if(isContext(context)){
			if(cardTable(context).attributeLabel(contextItem)){
				found = true
			}
		}
		return found
	}
	/**
	 * Method is used to validate the inline error messages when fields are left blank/empty then it ensure that validation error message is appear or not
	 * @param editForm
	 * @param condition
	 * @return either true or false based on the scenario
	 */	
}