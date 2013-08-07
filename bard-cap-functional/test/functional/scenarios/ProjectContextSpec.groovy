package scenarios

import pages.CapSearchPage
import pages.ContextItemPage
import pages.EditContextPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.ContextItem
import common.Constants.ExpectedValueType
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Project

//@Stepwise
class ProjectContextSpec extends BardFunctionalSpec {
	def testData = TestDataReader.getTestData()
	def section = "contexts"
	def cardGroup = "cardHolderAssayComponents"
	def editContextGroup = "Unclassified"
	def contextCard = "test card"
	def dbContextGroup = "Unclassified"

	def setup() {
		logInSomeUser()

		when: "User is at Home page, Navigating to Search Project By Id page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_ID)

		then: "User is at Search Project by Id page"
		at CapSearchPage

		when: "User is trying to search some project"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_ID, testData.ProjectID)

		then: "User is at View Project Definition page"
		at ViewProjectDefinitionPage
	}
	
	def "Test Project Context Card Add"(){
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContexts(editContextGroup)
		dbContentsBefore = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		addNewContextCard(editContextGroup, contextCard)

		and:"Verifying Context added successfully"
		assert isContext(editContextGroup, contextCard)

		when:"Context is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextGroup, testData.ProjectID)

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
			def dbContentsAfterDelete = Project.getProjectContext(dbContextGroup, testData.ProjectID)

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
		def dbContents = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
		
		report "ProjectContextCardAdd"
	}

	def "Test Project Context Card Edit"(){
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def editedContext = contextCard+Constants.edited
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContexts(editContextGroup)
		dbContentsBefore = Project.getProjectContext(dbContextGroup, testData.ProjectID)

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
		def dbContentsAfterAdd = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Project.getProjectContext(dbContextGroup, testData.ProjectID)

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
			def dbContentsAfterDelete = Project.getProjectContext(dbContextGroup, testData.ProjectID)

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
		def dbContents = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
		
		report "ProjectContextCardEdit"
	}

	def "Test Project Context Card Delete"(){
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContexts(editContextGroup)
		dbContentsBefore = Project.getProjectContext(dbContextGroup, testData.ProjectID)

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
		def dbContentsAfterDelete = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterDelete = getUIContexts(cardGroup)
		dbContentsAfterDelete = Project.getProjectContext(dbContextGroup, testData.ProjectID)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		
		report "ProjectContextCardDelete"
	}

	def "Test Project Context Item Add with Element Type"(){
		def contextItem = Constants.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
			def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Project Context Page"
			navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem =  Constants.valueType_FreeText.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.valueType_FreeText)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_NumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology, true, true)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_ElementwithoutElement, false)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_ElementwithoutValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_FreeTextwithoutDisplayValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeTextwithoutDisplayValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_NumericValuewithoutNumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValuewithoutNumericValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_ExternalOntologywithoutValues.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddContextItem(editContextGroup, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologywithoutValues)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_Element.AttributeFromDictionary
		def contextItemEdit = Constants.ValueType_ElementEdit.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ELEMENT, Constants.ValueType_ElementEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_FreeText.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeText)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.FREE, Constants.ValueType_FreeTextEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_NumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValueEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologyEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology, true, true)
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
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologyEdit, false, true)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(editContextGroup, contextCard, contextItem)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(editContextGroup, contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(cardGroup, contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_ExternalOntology.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)
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
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_NumericValue.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)
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
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_FreeText.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeText)
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
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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
		def contextItem = Constants.ValueType_Element.AttributeFromDictionary
		when:"At View Project Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		if(!isContext(cardGroup, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Project Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContext(section)
		}
		
		then:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

		and:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(editContextGroup, contextCard, contextItem)){
			navigateToAddContextItem(editContextGroup, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
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
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID, dbContextGroup, contextCard)

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