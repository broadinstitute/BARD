package pages

import geb.Page
import modules.SelectToContainerModule
import modules.SelectInputModule
import modules.SelectResultPopListModule

import geb.Module
import geb.navigator.Navigator
import geb.report.Reporter

class EditAssayContextPage extends Page{
	static url=""
	static at = {waitFor(10, 0.5){$("h4").text().contains("Edit Assay Context")}
	}

	static content = {
		editAssayDefinition {$("div", class:"pull-left").find("h4").text()}
		finishEditingBtn(wait: true) { $("a.btn.btn-small.btn-primary") }

		addEditAssayCards { module AddEditAssayCardModule }
		editAssayCards { module EditAssayCardModule }
		deleteAssayCards { module DeleteAssayCardModule }
		cardHolders { module CardsHolderModule, $("div.roundedBorder.card-group.assay-protocol--assay-component-") }
		moveAssayCardItem { module MoveCardItemsModule }
		itemWizard { module AddItemWizardModule, $("form#AddItemWizard") }
		selectToSearch { module SelectInputModule}
		selectResult { module SelectResultPopListModule }
	}

	def addNewContextCard(def cardName){
		if(isCardPresent(cardName)){
			deletAssayCard(cardName)
			Thread.sleep(3000)
		}
		
		addEditAssayCards.addNewCardBtn.click()
		waitFor{ addEditAssayCards.titleBar }
		assert addEditAssayCards.titleBar.text() ==~ "Create new card"
		assert addEditAssayCards.enterCardName
		assert addEditAssayCards.saveBtn
		assert addEditAssayCards.cancelBtn
		addEditAssayCards.enterCardName << "$cardName"
		addEditAssayCards.saveBtn.click()
		waitFor(20, 7){ cardHolders.cardName("$cardName") }
	}

	def verifyEmptyCardsMenu(def cardName){
		assert cardHolders.cardName("$cardName")
		cardHolders.cardMenu("$cardName").click()
		assert cardHolders.cardDDMenu[1] // edit card name button
		assert cardHolders.cardDDMenu[2] // add item wizard button
		assert cardHolders.cardDDMenu[3] // delete card button
		cardHolders.cardDDMenu[0].click()
	}

	def deletAssayCard(def cardName){
		assert isCardPresent(cardName)
		cardHolders.cardMenu(cardName).click()
		cardHolders.cardDDMenu[3].click()
		waitFor { deleteAssayCards.deleteBtn }
		deleteAssayCards.deleteBtn.click()
	}

	boolean isCardPresent(def cardName){
		boolean found = false
		if(cardHolders.cardTable){
			cardHolders.cardTable.find("caption").each{ cards ->
				if(cards.text().contains(cardName)){
					found = true
				}
			}
		}
		return found
	}

	def defineAttributes(searchAttribValue){
		Thread.sleep(3000)
		waitFor(15, 3){ itemWizard.selectAttrib.selectLink }
		assert itemWizard.selectAttrib.selectLink
		assert itemWizard.nextBtn
		assert itemWizard.cancelBtn
		itemWizard.selectAttrib.selectLink.click()
		selectToSearch.enterResult.value("$searchAttribValue")
		Thread.sleep(2000)
		waitFor(15, 3){ selectResult.resultPopup }
		selectResult.resultPopup.click()
		itemWizard.nextBtn.click()
	}

	def defineValueType(val){
		Thread.sleep(2000)
		waitFor(15, 3){ itemWizard.valueType("$val") }
		assert itemWizard.valueType("$val")
		assert itemWizard.nextBtn
		assert itemWizard.previousBtn
		itemWizard.valueType("$val").click()
		itemWizard.nextBtn.click()
	}

	def defineValue(searchAttribValue, qualifier, unitVal, selectUnit){
		Thread.sleep(2000)
		waitFor(15, 3){ itemWizard.selectValue.selectLink }
		assert itemWizard.selectValue.selectLink
		assert itemWizard.nextBtn
		assert itemWizard.cancelBtn
		assert itemWizard.previousBtn
		itemWizard.selectValue.selectLink.click()
		selectToSearch.enterResult.value("$searchAttribValue")
		Thread.sleep(2000)
		waitFor(15, 5){ selectResult.resultPopup }
		selectResult.resultPopup.click()
		itemWizard.valueQualifier.value("$qualifier")
		itemWizard.numericVal.value("$unitVal")
		itemWizard.selectValueUnit.selectLink.click()
		selectToSearch.enterResult.value("")
		Thread.sleep(2000)
		waitFor(15, 3){ selectResult.resultPopup }
		selectResult.resultPopup.click()
		itemWizard.nextBtn.click()
	}

	def reviewAndSave(){
		Thread.sleep(2000)
		waitFor(15, 3){ itemWizard.reviewContents.text() ==~ "Please review the information for this item above." }
		assert itemWizard.saveBtn
		assert itemWizard.cancelBtn
		assert itemWizard.previousBtn
		itemWizard.saveBtn.click()
		Thread.sleep(2000)
		waitFor(15, 3){ itemWizard.successAlert }
		assert itemWizard.closeBtn
		assert itemWizard.addAnotherBtn
		itemWizard.closeBtn.click()
		waitFor { finishEditingBtn}
	}
}

class CardsHolderModule extends Module {
	static content = {
		cardTable { $("table.table.table-hover") }
		cardName { captionText -> cardTable.find("caption", text:"$captionText") }
		cardMenu { captionText -> cardTable.find("caption", text:"$captionText").find("a")[0] }
		dropupBtns { $("div.btn-group.dropup.open") }
		cardDDMenu(wait: true) { dropupBtns.find("a") }
		cardItemMenu {cardValue -> cardTable.find("caption", text:"$cardValue").next().find("a")[0]}
		cardItemMenuDD(wait: true) { dropupBtns.find("a")}
		cardItems { captionText -> cardTable.find("caption", text:"$captionText").next().find("tr.context_item_row.ui-droppable") }
	}
}

class AddEditAssayCardModule extends Module {
	static content = {
		addNewCardBtn {$("button#addNewBtn-assay-protocol--assay-component-")}
		titleBar(wait: true) {$("span#ui-dialog-title-dialog_new_card")}
		enterCardName { $("input#new_card_name") }
		saveBtn { $("button.btn.btn-primary", text:"Save") }
		cancelBtn { $("button.btn.btn-primary", text:"Save").next() }
	}
}

class EditAssayCardModule extends Module {
	static content = {
		titleBar(wait: true) {$("span#ui-dialog-title-dialog_edit_card") }
		enterCardName { $("input#edit_card_name") }
		saveBtn { $("button.btn.btn-primary", text:"Save") }
		cancelBtn { $("button.btn.btn-primary", text:"Save").next() }
	}
}

class DeleteAssayCardModule extends Module {
	static content = {
		cardsContainer { $("div.roundedBorder.card-group.assay-protocol--assay-component-").find("div.row-fluid") }

		titleBar(wait: true) {$("span#ui-dialog-title-dialog_confirm_delete_card")}
		deleteBtn(wait: true) { $("button.btn.btn-danger") }
		cancelBtn(wait: true) { $("button.btn.btn-danger").next() }
	}
}

class MoveCardItemsModule extends Module {
	static content = {
		titleBar { $("span#ui-dialog-title-dialog_move_item") }
		selectCardId(wait: true) {$("#cardId")}
		newCardHolder {cardName -> $("#cardId").value("$cardName")}
		moveBtn (wait: true) { $("button.btn.btn-primary", text:"Move") }
		cancelBtn (wait: true) { $("button.btn.btn-primary").next() }
	}
}

class AddItemWizardModule extends Module {
	static content = {
		selectAttrib { module SelectToContainerModule, $("div#s2id_attributeId") }
		selectValue { module SelectToContainerModule, $("div#s2id_valueId") }
		selectValueUnit { module SelectToContainerModule, $("div#s2id_valueUnitId") }
		naviBtns { $("div.navigation")}
		nextBtn { naviBtns.find("input", name:"next") }
		cancelBtn { naviBtns.find("input", name:"cancel") }
		previousBtn { naviBtns.find("input", name:"previous") }
		saveBtn { naviBtns.find("input", name:"save") }
		closeBtn { naviBtns.find("input", name:"close") }
		addAnotherBtn { naviBtns.find("input", name:"addAnotherItem") }
		valueType {value -> $("input", name:"valueTypeOption", value:"$value")}
		valueQualifier { $("#valueQualifier") }
		numericVal { $("input#numericValue") }
		//seleValueId { module SelectToValueIdModule, $("div#s2id_valueId") }
		reviewContents { $("div.content").find("h1") }
		successAlert { $("div.alert.alert-success") }
	}
}