package modules

import geb.Module
import geb.navigator.Navigator

class CardsHolderModule extends Module {
	def contextCard
	static content = {
		cardsTitle(required: false) { $("div.cardTitle") }
		contextTitle(required: false) { $("div.cardTitle", text:contextCard) }
		addContextItem(required: false) { module EditIconModule, contextTitle.parent().find("div.btn-group") }
		itemRows(required: false) { contextTitle.parent().next("tbody") }
		contextItemRows(required: false) { itemRows.find("tr") }
		attributeLabel(required: false) { contextItem -> itemRows.find("td.attributeLabel", text:contextItem) }
		valueDisplay(required: false) { contextItem -> itemRows.find("td.valuedLabel", text:contextItem) }
		buttonGroup(required: false) { contextItem -> attributeLabel(contextItem).parent().find("div.btn-group") }
		deleteContextItem(required: false) { contextItem -> module EditIconModule, buttonGroup(contextItem) }
		editContextItem(required: false) { contextItem -> module EditIconModule, buttonGroup(contextItem) }
		
	}
}