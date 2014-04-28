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

package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.ViewAssayDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Assay

/**
 * This class extends all the test functions used for Assay Biology, Assay Component, Assay Design, Assay Protocol, Assay Readout, Experimental Variables sections
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Unroll
abstract class AssayBaseContextSpec extends BardFunctionalSpec {
	static def contextCard = "test card"
	static def section
	static def cardGroup
	static def editContextGroup
	static def dbContextType
	def setup(){
		logInSomeUser()
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContexts = getUIContexts(cardGroup)
		def dbContexts = Assay.getAssayContext(dbContextType, TestData.assayId)
		def uiContextItems = getUIContextItems(cardGroup, contextCard)
		def dbContextItems = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		assert uiContextItems.size() == dbContextItems.size()
		assert uiContextItems.sort() == dbContextItems.sort()

		and:"Navigate to Edit Assay Context Page and Add/Edit context"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
	}

	def "Test #TestName Context Card in Assay"(){
		when:"Navigate to Edit Assay Context Page and Add/Edit context"
		at EditContextPage
		if(TestName == "Add and Delete"){
			addNewContextCard(editContextGroup, inputData)
		}else{
			addNewContextCard(editContextGroup, inputData)
			editContext(editContextGroup, inputData, inputData+Constants.edited)
		}
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		when:"Finish editing and Fetching Contexts Info from UI and DB for validation"
		finishEditing()
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB and then cleanup the context"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		if(TestName == "Add and Delete"){
			while(isContext(editContextGroup, inputData)){
				deleteContext(editContextGroup, inputData)
			}
		}else{
			while(isContext(editContextGroup, inputData+Constants.edited)){
				deleteContext(editContextGroup, inputData)
			}
		}
		report "$TestName"
		where:
		TestName				| inputData
		"Add and Delete"		| contextCard
		"Edit and Delete"		| contextCard
	}


	def "Test Add #TestName Type Context Item in Assay"(){
		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"Navigating to Context Item Page"
		addNewContextCard(editContextGroup, contextCard)
		assert isContext(editContextGroup, contextCard)

		and:"Navigate to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		if(TestName == "Element"){
			addElementContextItem(inputData, true, false)
		}else if(TestName == "FreeText"){
			addFreeTextItem(inputData, true, false)
		}else if(TestName == "NumericValue"){
			addNumericValueItem(inputData, true, false)
		}else if(TestName == "ExOntologyIntegtegrated"){
			addExternalOntologyItem(inputData, true, false, true)
		}
		else if(TestName == "ExOntologyNoIntegtegrated"){
			addExternalOntologyItem(inputData, true, false, false)
		}

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigate to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

	def "Test Edit #TestName Type Context Item in Assay"(){
		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		addNewContextCard(editContextGroup, contextCard)
		assert isContext(editContextGroup, contextCard)

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			if(TestName == "Element"){
				addElementContextItem(inputData, true, false)
			}else if(TestName == "FreeText"){
				addFreeTextItem(inputData, true, false)
			}else if(TestName == "NumericValue"){
				addNumericValueItem(inputData, true, false)
			}else if(TestName == "ExOntologyIntegtegrated"){
				addExternalOntologyItem(inputData, true, false, true)
			}
			else if(TestName == "ExOntologyNoIntegtegrated"){
				addExternalOntologyItem(inputData, true, false, false)
			}
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(editContextGroup, contextCard, contextItem)
			navigateToUpdateContextItem(editContextGroup, contextCard, contextItem)
		}else{
			navigateToUpdateContextItem(editContextGroup, contextCard, contextItem)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		if(TestName == "Element"){
			addElementContextItem(inputData, false, false)
		}else if(TestName == "FreeText"){
			addFreeTextItem(inputData, false, false)
		}else if(TestName == "NumericValue"){
			addNumericValueItem(inputData, false, false)
		}else if(TestName == "ExOntologyIntegtegrated"){
			addExternalOntologyItem(inputData, false, false, true)
		}
		else if(TestName == "ExOntologyNoIntegtegrated"){
			addExternalOntologyItem(inputData, false, false, false)
		}

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
		//		"ExOntologyNoIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

	def cleanup(){
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		finishEditing()
		at ViewAssayDefinitionPage
		assert !isContext(cardGroup, contextCard)
	}
}
