package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Project

/**
 * This class includes all the possible test functions for annotation or context section of Project.
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Unroll
class ProjectContextSpec extends BardFunctionalSpec {
	def section = "annotations-header"
	def cardGroup = "cardHolderAssayComponents"
	def editContextGroup = "Unclassified"
	def contextCard = "test card"
	def dbContextType = "Unclassified"

	def setup() {
		logInSomeUser()
	}

	def "Test Add Context Card in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"Navigating to Context Item Page"
		addNewContextCard(editContextGroup, contextCard)

		and:"Verifying Context added successfully"
		assert isContext(editContextGroup, contextCard)

		when:"Context is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)

			when:"Context is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContexts(editContextGroup)
			def dbContentsAfterDelete = Project.getProjectContext(dbContextType, TestData.projectId)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContents = getUIContexts(cardGroup)
		def dbContents = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()

		report ""
	}

	def "Test Edit Context Card in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def editedContext = contextCard+Constants.edited
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"Add new Context Card and then Edit it"
		if(!isContext(editContextGroup, contextCard)){
			addNewContextCard(editContextGroup, contextCard)
			editContext(editContextGroup, contextCard, editedContext)
		}else{
			editContext(editContextGroup, contextCard, editedContext)
		}

		and:"Verifying Context edited successfully"
		assert isContext(editContextGroup, editedContext)

		when:"Context is edited, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, editedContext)){
			deleteContext(editContextGroup, editedContext)

			when:"Context is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContexts(editContextGroup)
			def dbContentsAfterDelete = Project.getProjectContext(dbContextType, TestData.projectId)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContents = getUIContexts(cardGroup)
		def dbContents = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()

		report ""
	}

	def "Test Delete Context Card in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"Add New Context Card then Delete it"
		if(!isContext(editContextGroup, contextCard)){
			addNewContextCard(editContextGroup, contextCard)
			assert isContext(editContextGroup, contextCard)
			while(isContext(editContextGroup, contextCard)){
				deleteContext(editContextGroup, contextCard)
			}
		}else{
			while(isContext(editContextGroup, contextCard)){
				deleteContext(editContextGroup, contextCard)
			}
		}

		and:"Verifying Context deleted successfully"
		assert !isContext(editContextGroup, contextCard)

		when:"Context is deleted, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterDelete = getUIContexts(editContextGroup)
		def dbContentsAfterDelete = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterDelete = getUIContexts(cardGroup)
		dbContentsAfterDelete = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		report ""
	}

	def "Test Add #TestName Type Context Item in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage

		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		then:"Add Context Card, Before adding context item"
		addNewContextCard(editContextGroup, contextCard)
		assert isContext(editContextGroup, contextCard)

		and:"Navigating to Context Item Page"
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
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)

		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute

	}
/*
	def "Test Add #TestName Type Context Item with Empty Values in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(section)
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		then:"Add Context Card, Before adding context item"
		addNewContextCard(editContextGroup, contextCard)
		assert isContext(editContextGroup, contextCard)

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		if(TestName == "NoAttribute"){
			addContextItemValidation("", true, false)
		}else if(TestName == "Element"){
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

		and:"Verifying Context Item added"
		at EditContextPage
		assert !isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(section, contextCard)

		report "$TestName"

		where:
		TestName					| inputData								| contextItem
		"NoAttribute"				| TestData.ValueType_Element			| TestData.ValueType_Element.AttributeFromDictionary
		"Element"					| TestData.ValueType_ElementwithoutValue			| TestData.ValueType_ElementwithoutValue.AttributeFromDictionary
		"FreeText"					| TestData.ValueType_FreeTextwithoutDisplayValue			| TestData.ValueType_FreeTextwithoutDisplayValue.AttributeFromDictionary
		"NumericValue"				| TestData.ValueType_NumericValuewithoutNumericValue		| TestData.ValueType_NumericValuewithoutNumericValue.AttributeFromDictionary
		"ExOntologyIntegtegrated"	| TestData.ValueType_ExternalOntologywithoutValues	| TestData.ValueType_ExternalOntologywithoutValues.AttributeFromDictionary
		//		"ExOntologyNoIntegtegrated"	| TestData.ValueType_ExternalOntology	| TestData.ValueType_ExternalOntology.AttributeFromDictionary
	}
*/
	def "Test Edit #TestName Type Context Item in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)

		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
		//		"ExOntologyNoIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

	def "Test Delete #TestName Type Context Item in Project"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		addNewContextCard(editContextGroup, contextCard)
		assert isContext(editContextGroup, contextCard)

		and:"If context Item does nto exists, add new before deleting"
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
			while(isContextItem(editContextGroup, contextCard, contextItem)){
				deleteContextItem(editContextGroup, contextCard, contextItem)
			}
		}else{
			while(isContextItem(editContextGroup, contextCard, contextItem)){
				deleteContextItem(editContextGroup, contextCard, contextItem)
			}
		}

		and:"Verify that Context Item deleted succesfully"
		assert !isContextItem(editContextGroup, contextCard, contextItem)
		
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterDelete = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)

		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

}