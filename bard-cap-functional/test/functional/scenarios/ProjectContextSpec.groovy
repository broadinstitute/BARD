package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestData
import common.Constants.ContextItem
import common.Constants.ExpectedValueType

import db.Project

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Last Updated: 13/10/10
 */
class ProjectContextSpec extends BardFunctionalSpec {
//	def testData = TestDataReader.getTestData()
	def section = "annotations"
	def cardGroup = "cardHolderAssayComponents"
	def editContextGroup = "Unclassified"
	def contextCard = "test card"
	def dbContextType = "Unclassified"

	def setup() {
		logInSomeUser()
	}
	
	def "Test Project Context Card Add"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def contexts = Project.getContexts(TestData.projectId)
		println contexts
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContexts(editContextGroup)
		dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		addNewContextCard(editContextGroup, contextCard)

		and:"Verifying Context added successfully"
		assert isContext(editContextGroup, contextCard)

		when:"Context is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContents = getUIContexts(cardGroup)
		def dbContents = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
		
		report "ProjectContextCardAdd"
	}

	def "Test Project Context Card Edit"(){
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

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContexts(editContextGroup)
		dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Edting Context"
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
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContents = getUIContexts(cardGroup)
		def dbContents = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
		
		report "ProjectContextCardEdit"
	}

	def "Test Project Context Card Delete"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContexts(editContextGroup)
		dbContentsBefore = Project.getProjectContext(dbContextType, TestData.projectId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Deleting Context"
		if(!isContext(editContextGroup, contextCard)){
			addNewContextCard(editContextGroup, contextCard)
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
		
		report "ProjectContextCardDelete"
	}

	def "Test Project Context Item Add with Element Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
			def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Project Context Page"
			navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, TestData.ValueType_Element)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
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
		
		report "ProjectContextItemAddwithElementType"
	}

	def "Test Project Context Item Add with Free Text Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem =  TestData.valueType_FreeText.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, TestData.valueType_FreeText)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
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
		
		report "ProjectContextItemAddwithFreeTextType"
	}

	def "Test Project Context Item Add with Numeric Value Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_NumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, TestData.ValueType_NumericValue)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
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
		
		report "ProjectContextItemAddwithNumericValueType"
	}

	def "Test Project Context Item Add with External Ontology Type using Intergratged Search"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntology, true, true)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
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
		
		report "ProjectContextItemAddwithExternalOntologyTypewithIntegratedSearch"
	}

	def "Test Project Context Item Add with External Ontology Type not using Intergratged Search"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntology)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
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
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
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
		
		report "ProjectContextItemAddwithExternalOntolgyTypewithoutIntegratedSearch"
	}
	

	def "Test Project Context Item Add with Element Type having Element field empty"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, TestData.ValueType_ElementwithoutElement, false)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)
		
		report "ProjectContextItemAddwithElementTypeEmpty"
	}

	def "Test Project Context Item Add with Element Type having Element value empty"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, TestData.ValueType_ElementwithoutValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)
		
		report "ProjectContextItemAddwithElementTypeValueEmpty"
	}

	def "Test Project Context Item Add with Free Text Type having Display Value empty"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_FreeTextwithoutDisplayValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, TestData.ValueType_FreeTextwithoutDisplayValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)
		
		report "ProjectContextItemAddwithFreeTextTypeEmpty"
	}

	def "Test Project Context Item Add with Numeric Value Type having Numeric Value empty"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_NumericValuewithoutNumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, TestData.ValueType_NumericValuewithoutNumericValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)
		
		report "ProjectContextItemAddwithNumericValueTypeEmpty"
	}

	def "Test Project Context Item Add with External Ontology Type having Ontology values empty"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntologywithoutValues.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntologywithoutValues)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		
		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Project Definition Page"
		at ViewProjectDefinitionPage
		assert !isContext(cardGroup, contextCard)
		
		report "ProjectContextItemAddwithExternalOntologyTypeEmpty"
	}

	def "Test Project Context Item Edit with Element Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		def contextItemEdit = TestData.ValueType_ElementEdit.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, TestData.ValueType_Element)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(editContextGroup, contextCard, contextItem)
			navigateToUpdateContextItem(editContextGroup, contextCard, contextItemEdit)
		}else{
			navigateToUpdateContextItem(editContextGroup, contextCard, contextItemEdit)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ELEMENT, TestData.ValueType_ElementEdit, false)

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
		
		report "ProjectContextItemEditwithElementType"
	}

	def "Test Project Context Item Edit with Free Text Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_FreeText.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, TestData.ValueType_FreeText)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.FREE, TestData.ValueType_FreeTextEdit, false)

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
		
		report "ProjectContextItemEditwithFreeTextType"
	}

	def "Test Project Context Item Edit with Numeric Value Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_NumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, TestData.ValueType_NumericValue)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.NUMERIC, TestData.ValueType_NumericValueEdit, false)

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
		
		report "ProjectContextItemEditwithNumericValueType"
	}

	def "Test Project Context Item Edit with External Ontology Type having no Intergration search"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntology)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntologyEdit, false)

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
		
		report "ProjectContextItemEditwithExternalOntologyTypewithoutIntegratedSearch"
	}

	def "Test Project Context Item Edit with External Ontology Type having Intergration search"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntology, true, true)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntologyEdit, false, true)

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
		
		report "ProjectContextItemEditwithExternalOntolgoyTypewithIntegratedSearch"
	}

	def "Test Project Context Item Delete with External Ontology Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, TestData.ValueType_ExternalOntology)
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
		
		report "ProjectContextItemDeletewithExternalOntologyType"
	}

	def "Test Project Context Item Delete with Numeric Value Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_NumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, TestData.ValueType_NumericValue)
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
		
		report "ProjectContextItemDeletewithNumericValueType"
	}

	def "Test Project Context Item Delete with Free Text Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_FreeText.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, TestData.ValueType_FreeText)
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
		
		report "ProjectContextItemDeletewithFreeTextType"
	}

	def "Test Project Context Item Delete with Element Type"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(TestData.projectId, dbContextType, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, TestData.ValueType_Element)
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
		
		report "ProjectContextItemDeletewithElementType"
	}

}