package pages

import geb.Page
import geb.Module
import geb.navigator.Navigator
import geb.report.Reporter;



class EditAssayContextPage extends Page{
	static url=""
	static at = {waitFor(10, 0.5){$("h4").text().contains("Edit Assay Context")}
	}

	static content = {
		editAssayDefinition {$("div", class:"pull-left").find("h4").text()}
		finishEditingBtn { $("a.btn.btn-small.btn-primary")}  // Finish Editing asssay context btn

		addEditAssayCards { module AddEditAssayCardModule }
		editAssayCards { module EditAssayCardModule }
		deleteAssayCards { module DeleteAssayCardModule }

		assayCardsHolder { module AssayCardsHolder }

		moveAssayCardItem { module MoveCardItemsModule }

		itemWizard { module AddItemWizardModule }
		//cardHolder {$("table.table.table-hover").find("caption")}
		loadingSpinner(wait:true) { $("div#spinner") } //loading message appear after action

		cardItemsMenu {$("table.table.table-hover").find("caption", text:"target").next().find("a")} // Context card items menu in each card caption tbody
	}

	def addNewContextCard(def cardName){
		assert addEditAssayCards.titleBar.text() ==~ "Create new card"
		assert addEditAssayCards.enterCardName
		assert addEditAssayCards.saveBtn
		assert addEditAssayCards.cancelBtn
		addEditAssayCards.enterCardName << "$cardName"
		addEditAssayCards.saveBtn.click()
		//waitFor(20, 0.5) { loadingSpinner }
		waitFor(20, 7){ assayCardsHolder.cardCaption("$cardName")}
		//assert assayCardsHolder.cardCaption("$cardName")
	}

	def verifyEmptyCardsMenu(def cardName){
		//assert assayCardsHolder.cardCaption("$cardName")
		assayCardsHolder.cardMenu("$cardName") //click to open context card menu drop down
		assert assayCardsHolder.cardMenuDD[1] // edit card name button
		assert assayCardsHolder.cardMenuDD[2] // add item wizard button
		assert assayCardsHolder.cardMenuDD[3] // delete card button
		assayCardsHolder.cardMenuDD[0].click()
	}

	def isCardEmpty(def cardName){
		cardItemsLabel("$cardName")
		cardItemsvalue("$cardName")

	}

	def deletAssayCard(def cardName){
		assayCardsHolder.cardMenu("$cardName")
		assayCardsHolder.cardMenuDD[3].click()
		waitFor { deleteAssayCards.deleteBtn }
		deleteAssayCards.deleteBtn.click()
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
	//	static base = { $("div#summaryView") }

	static content = {
		//titleBar {$("span#ui-dialog-title-dialog_confirm_delete_item").text()}
		titleBar { $("span#ui-dialog-title-dialog_move_item") }
		selectCardId(wait: true) {$("#cardId")}
		newCardHolder {cardName -> $("#cardId").value("$cardName")}
		moveBtn (wait: true) { $("button.btn.btn-primary", text:"Move") }
		cancelBtn (wait: true) { $("button.btn.btn-primary").next() }

		//deleteBtn { $("button.btn.btn-danger")}
	}
}

class AssayCardsHolder extends Module {
	//	static base = { $("div#summaryView") }

	static content = {
		assayProtocolComponentHolders { $("div.roundedBorder.card-group.assay-protocol--assay-component-").find("table") }
		cardCaption(wait: true) {cardValue -> $("div.roundedBorder.card-group.assay-protocol--assay-component-").find("table.table.table-hover").find("caption").find("div.cardTitle", text:"$cardValue")}
		cardMenu {cardValue -> $("div.roundedBorder.card-group.assay-protocol--assay-component-").find("table.table.table-hover").find("caption", text:"$cardValue").find("a")[0].click()}
		cardMenuDD(wait: true) { $("div.btn-group.dropup.open").find("a")} //card menu drop down
		//Cards Items
		cardItemsLabel { cardValue -> $("table.table.table-hover").find("caption", text:"$cardValue").next().find("tr.context_item_row.ui-droppable") }
		//cardItemsvalue { cardValue -> $("table.table.table-hover").find("caption", text:"$cardValue").next().find("td.valuedLabel").text() != "Empty"}
		cardItemMenu {cardValue -> $("table.table.table-hover").find("caption", text:"$cardValue").next().find("a")[0].click()}
		cardItemMenuDD(wait: true) { $("div.btn-group.dropup.open").find("a")}
	}
}


class AddItemWizardModule extends Module {
	static content = {
		titleBar {$("span#ui-dialog-title-dialog_add_item_wizard")}
		closeBtn { $("div.ui-dialog-buttonset").find("button.btn", text:"Close") }
		cancelBtn { $("div.ui-dialog-buttonset").find("button", text:"Cancel") }
		addAnOtherAttrib { $("button.btn.btn-primary", text:"Add another item") }

		//Wizard Step 1
		searchFor { $("div#s2id_attributeTextField").find("a").find("span")}
		searchForAttrib(wait: true) { $("input.select2-input") }

		attribChoicePopup(wait: true) { $("li.select2-results-dept-0.select2-result.select2-result-selectable.select2-highlighted") }

		nextBtn { $("input", name:"next") }

		//Wizard Step 2
		valueType {value -> $("input", name:"valueTypeOption", value:"$value")}
		previousBtn { $("input", name:"previous") }

		//Wizard Step 3
		searchForVal { $("input#valueTextField") }
		valueQualifier { value -> $("#valueQualifier").value("$value") }
		valueUnit { $("input#valueUnits") }
		currentChoice { $("input#currentChoice") }

		//Wizard Step 4
		saveBtn { $("input", name:"save") }

		//Wizard Step 5
		successMsg { $("div.alert.alert-success").find("strong")}
	}


	def defineAttributes(searchAttribValue){
		waitFor(20, 1){ searchForAttrib }
		assert searchForAttrib
		assert nextBtn
		searchFor.text() ==~ "Search for attribute name"
		searchForAttrib.value("$searchAttribValue")
		waitFor(20, 5){ attribChoicePopup }
		//attribChoicePopup.mouseover()
		attribChoicePopup[0].click()
		nextBtn.click()
		waitFor(15, 1){ previousBtn }

	}

	def defineValueType(value){
		waitFor { previousBtn }
		assert nextBtn
		assert previousBtn
		valueType("$value").click()

		nextBtn.click()
		waitFor(15, 1){ searchForAttrib }

	}

	def defineValue(searchAttribValue, qualifier, unitVal){
		waitFor{ previousBtn }
		assert nextBtn
		assert previousBtn
		assert searchForAttrib
		searchForAttrib << "$searchAttribValue"
		waitFor(20, 5){ attribChoicePopup }
		//assert attribChoicePopup.findAll() != "Empty"
		attribChoicePopup[0].click()
		valueQualifier("$qualifier")
		valueUnit << "$unitVal"

		nextBtn.click()
		waitFor(15, 1){ saveBtn }

	}

	def reviewnSave(){
		waitFor { previousBtn }
		assert saveBtn
		assert previousBtn
		saveBtn.click()
		waitFor(15, 1){ successMsg }
	}

}