package pages

import geb.Page
import modules.SelectToDropModule
import modules.SelectChoicePopupModule
import modules.SelectResultPopListModule
import modules.CardsHolderModule
import geb.Module
import geb.navigator.Navigator
import geb.report.Reporter

class EditAssayContextPage extends Page{
	final static WAIT_INTERVAL = 20
	final static R_INTERVAL = 3
	final static SLEEP_INTERVAL = 2000
	static url=""
	static at = {waitFor(WAIT_INTERVAL, R_INTERVAL){$("h4").text().contains("Edit Assay Context")}
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
		selectToSearch { module SelectChoicePopupModule}
		selectResult { module SelectResultPopListModule }
	}

	def addNewContextCard(def cardName){
		if(isCardPresent(cardName)){
			if(isContextItem(cardName)){
				deleContextItem(cardName)
				waitFor(WAIT_INTERVAL, R_INTERVAL) { !isContextItem(cardName) }
			}
			deletAssayCard(cardName)
			waitFor(WAIT_INTERVAL, R_INTERVAL) { !isCardPresent(cardName) }
		}
		addCard(cardName)
	}

	boolean isContextItem(def cardName){
		boolean found = false
		if(isCardPresent(cardName)){
			if(cardHolders.cardName(cardName).next().size() != "Empty"){
				if(cardHolders.cardName(cardName).next().find("a")){
					found = true
				}
			}
		}
		return found
	}
	def deleContextItem(def cardName){
		cardHolders.cardItemMenu(cardName).click()
		assert cardHolders.cardItemMenuDD[1]
		assert cardHolders.cardItemMenuDD[2]
		cardHolders.cardItemMenuDD[2].click()
		waitFor(WAIT_INTERVAL, R_INTERVAL){ deleteAssayCards.deleteBtn }
		deleteAssayCards.deleteBtn.click()
	}
	def addCard(def cardName){
		addEditAssayCards.addNewCardBtn.click()
		waitFor{ addEditAssayCards.titleBar }
		assert addEditAssayCards.titleBar.text() ==~ "Create new card"
		assert addEditAssayCards.enterCardName
		assert addEditAssayCards.saveBtn
		assert addEditAssayCards.cancelBtn
		addEditAssayCards.enterCardName << cardName
		addEditAssayCards.saveBtn.click()
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isCardPresent(cardName) }
	}
	def verifyEmptyCardsMenu(def cardName){
		int INDEX = 1
		assert cardHolders.cardName(cardName)
		cardHolders.cardMenu(cardName).click()
		assert cardHolders.cardDDMenu[INDEX++]	// edit card name button
		assert cardHolders.cardDDMenu[INDEX++]	// add item wizard button
		assert cardHolders.cardDDMenu[INDEX++]	// delete card button
		cardHolders.cardDDMenu[0].click()
	}

	def deletAssayCard(def cardName){
		assert isCardPresent(cardName)
		cardHolders.cardMenu(cardName).click()
		cardHolders.cardDDMenu[4].click()
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
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ itemWizard.selectAttrib.selectLink }
		assert itemWizard.selectAttrib.selectLink
		assert itemWizard.nextBtn
		assert itemWizard.cancelBtn
		itemWizard.selectAttrib.selectLink.click()
		selectToSearch.enterResult.value(searchAttribValue)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ selectResult.resultPopup }
		Thread.sleep(SLEEP_INTERVAL)
		selectResult.resultPopup.click()
		itemWizard.nextBtn.click()
	}

	def defineValueType(val){
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ itemWizard.valueType(val) }
		assert itemWizard.valueType(val)
		assert itemWizard.nextBtn
		assert itemWizard.previousBtn
		itemWizard.valueType(val).click()
		itemWizard.nextBtn.click()
	}

	def defineValue(searchAttribValue, qualifier, unitVal, selectUnit){
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ itemWizard.selectValue.selectLink }
		assert itemWizard.selectValue.selectLink
		assert itemWizard.nextBtn
		assert itemWizard.cancelBtn
		assert itemWizard.previousBtn
		itemWizard.selectValue.selectLink.click()
		selectToSearch.enterResult.value(searchAttribValue)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ selectResult.resultPopup }
		Thread.sleep(SLEEP_INTERVAL)
		selectResult.resultPopup.click()
		//itemWizard.valueQualifier.value(qualifier)
		//itemWizard.numericVal.value(unitVal)
		//itemWizard.selectValueUnit.selectLink.click()
		//selectToSearch.enterResult.value("")
		//waitFor(WAIT_INTERVAL, R_INTERVAL){ selectResult.resultPopup }
		//Thread.sleep(SLEEP_INTERVAL)
		//selectResult.resultPopup.click()
		itemWizard.nextBtn.click()
	}

	def reviewAndSave(){
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ itemWizard.reviewContents.text() ==~ "Please review the information for this item above." }
		assert itemWizard.saveBtn
		assert itemWizard.cancelBtn
		assert itemWizard.previousBtn
		itemWizard.saveBtn.click()
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ itemWizard.successAlert }
		assert itemWizard.closeBtn
		assert itemWizard.addAnotherBtn
		itemWizard.closeBtn.click()
		waitFor { finishEditingBtn}
	}

	def getUIContexts(){
		def uiContexts = []
		if(cardHolders.cardTable.find("caption")){
			cardHolders.cardTable.find("caption").each{ contextName ->
				uiContexts.add(contextName.text())
			}
		}
		return uiContexts
	}

	def getContextCardItems(def card){
		def resultList = []
		def resultMap = [:]
		if(isContextItem(card))
			cardHolders.cardItems(card).each{
				resultMap = ['attributeLabel':it.find("td")[1].text(), 'valueDisplay':it.find("td")[2].text()]
				resultList.add(resultMap)
			}
		return resultList
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
		newCardHolder {cardName -> $("#cardId").value(cardName)}
		moveBtn (wait: true) { $("button.btn.btn-primary", text:"Move") }
		cancelBtn (wait: true) { $("button.btn.btn-primary").next() }
	}
}

class AddItemWizardModule extends Module {
	static content = {
		selectAttrib { module SelectToDropModule, $("div#s2id_attributeId") }
		selectValue { module SelectToDropModule, $("div#s2id_valueId") }
		selectValueUnit { module SelectToDropModule, $("div#s2id_valueUnitId") }
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
		reviewContents { $("div.content").find("h1") }
		successAlert { $("div.alert.alert-success") }
	}
}