package pages

import geb.Page
import modules.ButtonsModule
import modules.CardsHolderModule
import modules.LoadingModule

import common.Constants
import common.Constants.ContextItem

class EditContextPage extends ContextScaffoldPage{
	final static SLEEP_INTERVAL = 2000
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { $("h4").text().contains("Edit Project") }
	}
	def itemName
	static content = {
		finishEditing(wait: true) { module ButtonsModule, $("div.well.well-small"), buttonName:"Finish Editing" }
		cardTable{ contextTitle -> module CardsHolderModule, $("div#Unclassified").find("table.table.table-hover"), contextCard:contextTitle }
		formLoading { module LoadingModule}
	}

	def navigateToAddEditDeleteContextItemPage(ContextItem ci, def contextName, def ... contextItemName){
		assert cardTable(contextName).contextTitle
		assert cardTable(contextName).addContextItem.iconPlus
		switch(ci){
			case ContextItem.ADD:
				cardTable(contextName).addContextItem.iconPlus.click()
				break;
			case ContextItem.UPDATE:
				assert cardTable(contextName).editContextItem(contextItemName).iconPencil
				cardTable(contextName).editContextItem(contextItemName).iconPencil.click()
				break;
			case ContextItem.DELETE:
				assert cardTable(contextName).deleteContextItem(contextItemName).iconTrash
				withConfirm { cardTable(contextName).deleteContextItem(contextItemName).iconTrash.click() }
				break;
		}
	}
	
}