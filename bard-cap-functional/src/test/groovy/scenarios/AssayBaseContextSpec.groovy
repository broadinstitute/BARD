package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.ViewAssayDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

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

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContexts = getUIContexts(cardGroup)
		def dbContexts = Assay.getAssayContext(dbContextType, TestData.assayId)
		def uiContextItems = getUIContextItems(cardGroup, contextCard)
		def dbContextItems = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verify Context Info with UI & DB"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		assert uiContextItems.size() == dbContextItems.size()
		assert uiContextItems.sort() == dbContextItems.sort()

		and:"Navigate to Edit Assay Context Page and cleanup data"
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
			editContext(editContextGroup, inputData, inputData+TestData.edited)
		}
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()
		
		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verify Context Info with UI & DB and then cleanup the context"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, inputData+TestData.edited)){
			deleteContext(editContextGroup, inputData+TestData.edited)
		}
		report "$TestName"

		where:"Execute tests with following conditions to add and edit contexts"
		TestName				| inputData
		"Add and Delete"		| contextCard
		"Edit and Delete"		| contextCard
	}

	def "Test Add #TestName Type Context Item in Assay"(){
		when:"Add new Card and then Add New Context Item in it"
		at EditContextPage
		addNewContextCard(editContextGroup, contextCard)
		navigateToAddContextItem(editContextGroup, contextCard)
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
		at EditContextPage
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		report "$TestName"

		where:"Execute tests with following conditions to add ontexts items"
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

	def "Test Edit #TestName Type Context Item in Assay"(){
		when:"Add New Context Item and then edit it"
		at EditContextPage
		addNewContextCard(editContextGroup, contextCard)
		navigateToAddContextItem(editContextGroup, contextCard)
		at ContextItemPage
		if(TestName == "Element"){
			addElementContextItem(inputData, true, false)
		}else if(TestName == "FreeText"){
			addFreeTextItem(inputData, true, false)
		}else if(TestName == "NumericValue"){
			addNumericValueItem(inputData, true, false)
		}else if(TestName == "ExOntologyIntegtegrated"){
			addExternalOntologyItem(inputData, true, false, true)
		}else if(TestName == "ExOntologyNoIntegtegrated"){
			addExternalOntologyItem(inputData, true, false, false)
		}
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)
		navigateToUpdateContextItem(editContextGroup, contextCard, contextItem)
		at ContextItemPage
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
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verif eidted contexts Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verif Contexts Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		report "$TestName"

		where:"Execute tests with following conditions to edit ontexts items"
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