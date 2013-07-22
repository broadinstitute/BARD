package pages

import geb.Page
import modules.AddContextCardModule;
import modules.ButtonsModule
import modules.CardsHolderModule
import modules.LoadingModule

import common.Constants
import common.Constants.ContextItem

class EditContextPage extends CapScaffoldPage{
	final static SLEEP_INTERVAL = 2000
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { $("h4").text().contains("Edit") }
	}
	def itemName
	static content = {
		finishEditing(wait: true) { module ButtonsModule, $("div.well.well-small"), buttonName:"Finish Editing" }
		cardGroup { $("div.roundedBorder.card-group") }
		cardTable{ contextTitle -> module CardsHolderModule, cardGroup.find("table.table.table-hover"), contextCard:contextTitle }
		addNewCardBtn { cardGroup.find("button.btn.add-card-button")}
		addContextCard { module AddContextCardModule }
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
	
	def addNewContextCard(def contextName){
		addNewCardBtn.click()
		assert addContextCard.inputCardName
		assert addContextCard.saveBtn.buttonSubmitPrimary
		addContextCard.inputCardName.value(cardName)
		addContextCard.saveBtn.buttonSubmitPrimary.click()
		ajaxRequestCompleted()
	}
	def deleteContext(def contextName){
		if(isContextCardNotEmpty(contextName))
		withConfirm {cardTable(contextName).addContextItem.iconTrash.click()}
	}
}