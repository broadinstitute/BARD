package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.ViewProjectDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.TestData

import db.Project

/**
 * This class includes all the possible test functions for annotation or context section of Project.
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Unroll
class ProjectContextSpec extends BardFunctionalSpec {
	static def section = "annotations-header"
	static def cardGroup = "cardHolderAssayComponents"
	static def editContextGroup = "Unclassified"
	static def contextCard = "test card"
	static def dbContextType = "Unclassified"

	def setup() {
		logInSomeUser()

		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContexts = getUIContexts(cardGroup)
		def dbContexts = Project.getProjectContext(dbContextType, TestData.projectId)
		def uiContextItems = getUIContextItems(cardGroup, contextCard)
		def dbContextItems = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

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

	def "Test #TestName Context Card in Project"(){
		when:"At Edit Project Context Page, Add/Edit new context"
		at EditContextPage
		if(TestName == "Add and Delete"){
			addNewContextCard(editContextGroup, inputData)
		}else{
			addNewContextCard(editContextGroup, inputData)
			editContext(editContextGroup, inputData, inputData+TestData.edited)
		}
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

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

	def "Test Add #TestName Type Context Item in Project"(){
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
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		report "$TestName"

		where:"Execute tests with following conditions to add Contexts items"
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

	def "Test Edit #TestName Type Context Item in Project"(){
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
		}
		else if(TestName == "ExOntologyNoIntegtegrated"){
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
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verif eidted contexts Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

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
	}

	def cleanup(){
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		finishEditing()
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)
	}
}