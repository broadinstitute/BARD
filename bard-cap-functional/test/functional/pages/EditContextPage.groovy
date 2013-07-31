package pages

import modules.AddContextCardModule
import modules.ButtonsModule
import modules.CardsHolderModule
import modules.LoadingModule

import common.Constants

class EditContextPage extends CapScaffoldPage{
	final static SLEEP_INTERVAL = 2000
	static url=""
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { $("h4").text().contains("Edit") }
	}
	def itemName
	static content = {
		finishEditing(wait: true) { module ButtonsModule, $("div.well.well-small"), buttonName:"Finish Editing" }
		addNewCardBtn { groupId -> cardContainer(groupId).find("button.btn.add-card-button") }
		addContextCard { module AddContextCardModule }
		formLoading { module LoadingModule}

		cardContainer { groupName -> $("div.roundedBorder.card-group."+groupName) }
		contextTable { groupName -> cardContainer(groupName).find("table.table.table-hover")}
		cardTable{ groupName, contextTitle -> module CardsHolderModule, contextTable(groupName), contextCard:contextTitle }
	}

	def navigateToAddContextItem(def cardGroup, def contextName){
		assert cardTable(cardGroup, contextName).contextTitle
		assert cardTable(cardGroup, contextName).contextBtnGroup.iconPlus
		cardTable(cardGroup, contextName).contextBtnGroup.iconPlus.click()
	}

	def navigateToUpdateContextItem(def cardGroup, def contextName, def contextItemName){
		assert cardTable(cardGroup, contextName).contextTitle
		assert cardTable(cardGroup, contextName).editContextItem(contextItemName).iconPencil
		cardTable(cardGroup, contextName).editContextItem(contextItemName).iconPencil.click()

	}
	def deleteContextItem(def cardGroup, def contextName, def contextItemName){
		assert cardTable(cardGroup, contextName).contextTitle
		assert cardTable(cardGroup, contextName).deleteContextItem(contextItemName).iconTrash
		withConfirm { cardTable(cardGroup, contextName).deleteContextItem(contextItemName).iconTrash.click() }
	}
	def cleanUpContext(def cardGroup, def contextName){
		assert cardTable(cardGroup, contextName).contextTitle
		assert cardTable(cardGroup, contextName).deleteAllItems.iconTrash
		withConfirm { cardTable(cardGroup, contextName).deleteAllItems.iconTrash.click() }
	}

	def addNewContextCard(def groupId, def contextName){
		addNewCardBtn(groupId).click()
		assert addContextCard.inputCardName
		assert addContextCard.saveBtn.buttonSubmitPrimary
		addContextCard.inputCardName.value(contextName)
		addContextCard.saveBtn.buttonSubmitPrimary.click()
		ajaxRequestCompleted()
	}

	def deleteContext(def cardGroup, def contextName){
		if(isContextCardNotEmpty(cardGroup, contextName)){
			while(isContextCardNotEmpty(cardGroup, contextName)){
				cleanUpContext(cardGroup,contextName)
				ajaxRequestCompleted()
			}
			withConfirm {cardTable(cardGroup, contextName).contextBtnGroup.iconTrash.click()}
		}else{
			withConfirm {cardTable(cardGroup, contextName).contextBtnGroup.iconTrash.click()}
		}
	}

	def editContext(def cardGroup, def contextName, def editedValue){
		if(isContext(cardGroup, contextName)){
			if(!isContextCardNotEmpty(cardGroup, contextName)){
				assert cardTable(cardGroup, contextName).editContext
				cardTable(cardGroup, contextName).editContext.editIconPencil.click()
				assert editableForm.buttons.iconOk
				assert editableForm.buttons.iconRemove
				fillInputField(editedValue)
				ajaxRequestCompleted()
			}
		}
	}
}