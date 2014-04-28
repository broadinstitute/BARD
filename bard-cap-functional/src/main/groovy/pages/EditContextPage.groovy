/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pages

import modules.AddContextCardModule
import modules.ButtonsModule
import modules.CardsHolderModule
import modules.LoadingModule


/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class EditContextPage extends CapScaffoldPage{
	static url=""
	static at = { waitFor { $("h4").text().contains("Edit") }
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
//		ajaxRequestCompleted()
		waitFor { isContext(groupId, contextName) }
	}

	def deleteContext(def cardGroup, def contextName){
		if(isContextCardNotEmpty(cardGroup, contextName)){
			while(isContextCardNotEmpty(cardGroup, contextName)){
				cleanUpContext(cardGroup,contextName)
//				ajaxRequestCompleted()
			}
			withConfirm {cardTable(cardGroup, contextName).contextBtnGroup.iconTrash.click()}
		}else{
			withConfirm {cardTable(cardGroup, contextName).contextBtnGroup.iconTrash.click()}
		}
		waitFor { !isContext(cardGroup, contextName) }
	}

	def editContext(def cardGroup, def contextName, def editedValue){
		if(isContext(cardGroup, contextName)){
			if(!isContextCardNotEmpty(cardGroup, contextName)){
				assert cardTable(cardGroup, contextName).editContext
				cardTable(cardGroup, contextName).editContext.editIconPencil.click()
				assert editableForm.buttons.iconOk
				assert editableForm.buttons.iconRemove
				fillFieldsInput(editedValue)
//				ajaxRequestCompleted()
			}
		}
		waitFor { isContext(cardGroup, editedValue) }
	}
	
	def finishEditing(){
		finishEditing.buttonPrimary.click()
	}
}
