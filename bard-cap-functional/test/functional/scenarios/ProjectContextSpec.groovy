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

	def "Test Project Context Item Add with Element Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_Element.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Add with Free Text Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.valueType_FreeText)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.valueType_FreeText.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.valueType_FreeText.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.valueType_FreeText.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Add with Numeric Value Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Add with External Ontology Type using Intergratged Search"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology, true, true)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Add with External Ontology Type not using Intergratged Search"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Add with Element Type having Element field empty"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_ElementwithoutElement, false)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Project Context Item Add with Element Type having Element value empty"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_ElementwithoutValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}
	
	def "Test Project Context Item Add with Free Text Type having Display Value empty"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeTextwithoutDisplayValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Project Context Item Add with Numeric Value Type having Numeric Value empty"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValuewithoutNumericValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Project Context Item Add with External Ontology Type having Ontology values empty"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologywithoutValues)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Project Context Item Edit with Element Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ELEMENT, Constants.ValueType_ElementEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Edit with Free Text Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeText)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.FREE, Constants.ValueType_FreeTextEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Edit with Numeric Value Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValueEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Edit with External Ontology Type having no Intergration search"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologyEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Edit with External Ontology Type having Intergration search"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology, true, true)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, testData.ContextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologyEdit, false, true)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		def dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterAdd = getUIContextItems(Constants.contextCard)
		dbContentsAfterAdd = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContextPage()
		at EditContextPage
		while(isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

			and:"Navigating to View Project Page"
			finishEditing.buttonPrimary.click()

			when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
			at ViewProjectDefinitionPage
			uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
			dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}

	}

	def "Test Project Context Item Delete with External Ontology Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			while(isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			}
		}

		assert !isContextItem(Constants.contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

	def "Test Project Context Item Delete with Numeric Value Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			while(isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			}
		}

		assert !isContextItem(Constants.contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

	def "Test Project Context Item Delete with Free Text Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeText)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			while(isContextItem(Constants.contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(Constants.contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			}
		}

		assert !isContextItem(Constants.contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

	def "Test Project Context Item Delete with Element Type"(){
		when:"At View Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiContentsBefore = getUIContextItems(Constants.contextCard)
		def dbContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Project Context Page"
		navigateToEditContextPage()

		when:"At Edit Project Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(Constants.contextCard)
		uiContentsBefore = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, testData.ContextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)
			while(isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_Element.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, testData.ContextCard, Constants.ValueType_Element.AttributeFromDictionary)
			}
		}

		assert !isContextItem(Constants.contextCard, Constants.ValueType_Element.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		def dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Project Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Project Page, Fetching Contexts Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		uiContentsAfterDelete = getUIContextItems(Constants.contextCard)
		dbContentsAfterDelete = Project.getProjectContextItem(testData.ProjectID,Constants.contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

}