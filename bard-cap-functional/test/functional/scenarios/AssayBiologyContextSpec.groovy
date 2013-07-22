package scenarios

import pages.CapSearchPage
import pages.ContextItemPage
import pages.EditContextPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.ContextItem
import common.Constants.ExpectedValueType
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Assay

class AssayBiologyContextSpec extends BardFunctionalSpec {
	def testData = TestDataReader.getTestData()
	def section = "biology"
//	def contextCard = "biology"
	def contextCard = "test card"
	def contextGroup = "biology>"
	def setup() {
		logInSomeUser()

		when: "User is at Home page, Navigating to Search Assay By Id page"
		at HomePage
		navigateTo(NavigateTo.ASSAY_BY_ID)

		then: "User is at Search Assay by Id page"
		at CapSearchPage

		when: "User is trying to search some Assay"
		at CapSearchPage
		capSearchBy(SearchBy.ASSAY_ID, testData.AssayId)

		then: "User is at View Assay Definition page"
		at ViewAssayDefinitionPage
	}

	def "Test Assay Biology Context Card Add"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContexts = getUIContexts(section)
		System.out.println(uiContexts)
//		def uiContentsBefore = getUIContextItems(contextCard)
//		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
//		assert uiContentsBefore.size() == dbContentsBefore.size()
//		assert uiContentsBefore.sort() == dbContentsBefore.sort()
//		assert !isContext(contextCard)
		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		addNewContextCard(contextCard)
//		uiContexts = getUIContexts(section)
//		System.out.println(uiContexts)
//		uiContentsBefore = getUIContextItems(contextCard)
//		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)
//
		then:"Verifying Context Info with UI & DB"
//		assert uiContentsBefore.size() == dbContentsBefore.size()
//		assert uiContentsBefore.sort() == dbContentsBefore.sort()
//		assert !isContext(section, contextCard)
		
		and:"Navigating to Context Item Page"
//		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
		
		
//		when: "At Context Item Page"
//		at ContextItemPage
//
//		then:"Adding New Context Item"
//		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
//
//		and:"Verifying Context Item added successfully"
//		at EditContextPage
//		assert isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)
//
//		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
//		def uiContentsAfterAdd = getUIContextItems(contextCard)
//		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)
//
//		then:"Verifying Context Info with UI & DB"
//		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
//		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
//		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
//
//		and:"Navigating to View Assay Page"
//		finishEditing.buttonPrimary.click()
//
//		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
//		at ViewAssayDefinitionPage
//		uiContentsAfterAdd = getUIContextItems(contextCard)
//		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)
//
//		then:"Verifying Context Info with UI & DB"
//		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
//		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
//		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
//
//		and:"Cleaning up Context Items"
//		navigateToEditContext(section)
//		at EditContextPage
//		while(isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
//			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_Element.AttributeFromDictionary)
//
//			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
//			def uiContentsAfterDelete = getUIContextItems(contextCard)
//			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)
//
//			then:"Verifying Context Info with UI & DB"
//			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
//			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
//			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
//		}
//		and:"Navigating to View Assay Page"
//		finishEditing.buttonPrimary.click()
//
//		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
//		at ViewAssayDefinitionPage
//		def uiContents = getUIContextItems(contextCard)
//		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)
//
//		then:"Verifying Context Info with UI & DB"
//		assert uiContents.size() < uiContentsAfterAdd.size()
//		assert dbContents.size() < dbContentsAfterAdd.size()
//		assert uiContents.sort() == dbContents.sort()
	}
	/*
	def "Test Assay Biology Context Item Add with Element Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_Element.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Add with Free Text Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.valueType_FreeText)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.valueType_FreeText.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.valueType_FreeText.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.valueType_FreeText.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Add with Numeric Value Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()

	}

	def "Test Assay Biology Context Item Add with External Ontology Type using Intergratged Search"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology, true, true)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Add with External Ontology Type not using Intergratged Search"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)

		and:"Verifying Context Item added successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() > uiContentsBefore.size()
		assert dbContentsAfterAdd.size() > dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.size() < uiContentsAfterAdd.size()
			assert dbContentsAfterDelete.size() < dbContentsAfterAdd.size()
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.size() < uiContentsAfterAdd.size()
		assert dbContents.size() < dbContentsAfterAdd.size()
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Add with Element Type having Element field empty"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_ElementwithoutElement, false)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Assay Biology Context Item Add with Element Type having Element value empty"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_ElementwithoutValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Assay Biology Context Item Add with Free Text Type having Display Value empty"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeTextwithoutDisplayValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Assay Biology Context Item Add with Numeric Value Type having Numeric Value empty"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValuewithoutNumericValue)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Assay Biology Context Item Add with External Ontology Type having Ontology values empty"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Context Item Page"
		navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)

		when: "At Context Item Page"
		at ContextItemPage

		then:"Adding New Context Item"
		addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologywithoutValues)

		and:"Verifying Context Item added"
		at EditContextPage

		when:"Context Item  is added, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
	}

	def "Test Assay Biology Context Item Edit with Element Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ELEMENT, Constants.ValueType_ElementEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ElementEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Edit with Free Text Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeText)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.FREE, Constants.ValueType_FreeTextEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_FreeTextEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Edit with Numeric Value Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValueEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_NumericValueEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Edit with External Ontology Type having no Intergration search"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologyEdit, false)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Edit with External Ontology Type having Intergration search"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before updating"
		if(!isContextItem(contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology, true, true)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}else{
			navigateToAddEditDeleteContextItemPage(ContextItem.UPDATE, contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)
		}

		when: "At Context Item Page"
		at ContextItemPage

		then:"Updating Context Item"
		addEditContextItem(ContextItem.UPDATE, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntologyEdit, false, true)

		and:"Verifying Context Item updated successfully"
		at EditContextPage
		assert isContextItem(contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

		when:"Context Item  is updated, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContextItems(contextCard)
		def dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContextItems(contextCard)
		dbContentsAfterAdd = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ExternalOntologyEdit.AttributeFromDictionary)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(contextCard)
			def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContextItems(contextCard)
		def dbContents = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
	}

	def "Test Assay Biology Context Item Delete with External Ontology Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ONTOLOGY, Constants.ValueType_ExternalOntology)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			while(isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
			}
		}

		assert !isContextItem(contextCard, Constants.ValueType_ExternalOntology.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(contextCard)
		def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterDelete = getUIContextItems(contextCard)
		dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

	def "Test Assay Biology Context Item Delete with Numeric Value Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.NUMERIC, Constants.ValueType_NumericValue)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			while(isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
			}
		}

		assert !isContextItem(contextCard, Constants.ValueType_NumericValue.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(contextCard)
		def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterDelete = getUIContextItems(contextCard)
		dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

	def "Test Assay Biology Context Item Delete with Free Text Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.FREE, Constants.ValueType_FreeText)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			while(isContextItem(contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
			}
		}

		assert !isContextItem(contextCard, Constants.ValueType_FreeText.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(contextCard)
		def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterDelete = getUIContextItems(contextCard)
		dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}

	def "Test Assay Biology Context Item Delete with Element Type"(){
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		uiContentsBefore = getUIContextItems(contextCard)
		uiContentsBefore = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"If context Item does nto exists, add new before deleting"
		if(!isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
			navigateToAddEditDeleteContextItemPage(ContextItem.ADD, contextCard)
			and:"Navigating to Context Item Page"
			at ContextItemPage
			addEditContextItem(ContextItem.ADD, ExpectedValueType.ELEMENT, Constants.ValueType_Element)
			and:"Navigating to Edit Context Page"
			at EditContextPage
			assert isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)
			while(isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_Element.AttributeFromDictionary)
			}
		}else{
			while(isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)){
				navigateToAddEditDeleteContextItemPage(ContextItem.DELETE, contextCard, Constants.ValueType_Element.AttributeFromDictionary)
			}
		}

		assert !isContextItem(contextCard, Constants.ValueType_Element.AttributeFromDictionary)
		when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsAfterDelete = getUIContextItems(contextCard)
		def dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterDelete = getUIContextItems(contextCard)
		dbContentsAfterDelete = Assay.getAssayContextItem(testData.AssayId, contextGroup, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
	}
*/
/*	def "Test Add Assay Context Card"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		def uiContexts = getUIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()

		then: "Add New Context Card"
		addNewContextCard(card1)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isCardPresent(card1) }

		when: "Card is added, Fetching card info from database and UI"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Validating card info with UI and database"
		assert isCardPresent(card1)
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		report "Card1Add"

		when: "Card is added and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage

		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Validating card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}

	def "Test Empty Card Dropdown Menu"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage

		then: "Verifing empty card drop down menu"
		verifyEmptyCardsMenu(card1)
		report "EmptyCardMenu"

		when: "Empty Card menu is validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Add Assay Context Item"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def contextItemData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		def uiContextItems = getContextCardItems(card1)

		then: "Add context Item to card"
		assert uiContextItems == contextItemData
		cardHolders.cardMenu(card1).click()
		cardHolders.cardDDMenu[2].click()
		defineAttributes(arrtibuteSearch)
		defineValueType(valueType)
		defineValue(arrtibuteSearch, valueTypeQualifier, unitValue, unitSelect)
		reviewAndSave()
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isContextItem(card1) }
		assert isContextItem(card1)

		when: "Context item is added, Fetch context info to validate"
		contextItemData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		uiContextItems = getContextCardItems(card1)

		then: "Validate context item data with UI and database"
		assert uiContextItems == contextItemData
		report "AddContextItem"

		when: "Context item is added and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}


	def "Test Move Assay Context Item"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage

		then: "Add Context Card 2 for Item Move"
		addNewContextCard(card2)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isCardPresent(card2) }
		assert isCardPresent(card2)

		when: "Card2 is added, Fetch cards info to validate"
		def card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		def card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		def uiCardItems = getContextCardItems(card1)
		def uiCard2Items = getContextCardItems(card2)

		then: "Validate context cards info before moving an item"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData

		and: "Move context item from one card to another card"
		cardHolders.cardItemMenu(card1).click()
		cardHolders.cardItemMenuDD[1].click()
		waitFor(WAIT_INTERVAL, R_INTERVAL){ moveAssayCardItem.selectCardId }
		moveAssayCardItem.newCardHolder(card2)
		moveAssayCardItem.moveBtn.click()
		report "MoveContextItem"
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isContextItem(card2) }
		waitFor(WAIT_INTERVAL, R_INTERVAL) { !isContextItem(card1) }
		assert !isContextItem(card1)
		assert isContextItem(card2)

		when: "Context item is moved to other card, Fetch cards info to validate"
		card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		uiCardItems = getContextCardItems(card1)
		uiCard2Items = getContextCardItems(card2)

		then: "Context item is moved, Validate context cards info"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData

		when: "Context card are moved and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Delete Assay Context Item"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		def card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		def uiCardItems = getContextCardItems(card1)
		def uiCard2Items = getContextCardItems(card2)

		then: "Validate cards info before deleting context item"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData

		and: "Delete context item from card"
		deleContextItem(card2)
		report "DeleteContextItem"
		waitFor(WAIT_INTERVAL, R_INTERVAL) { !isContextItem(card2) }
		assert !isContextItem(card2)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { at EditAssayContextPage }

		when: "Context item is deleted, Fetch cards info to validate"
		card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		uiCardItems = getContextCardItems(card1)
		uiCard2Items = getContextCardItems(card2)

		then: "Context item is deleted, validate careds info"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData

		when:"Context items are deleted and validated"
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Edit Assay Context Card"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def cGroup = "assay protocol> assay component>"
		def dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		def uiContexts = getUIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()

		then: "Editing Assay Context Card"
		cardHolders.cardMenu(card1).click()
		cardHolders.cardDDMenu[1].click()
		assert editAssayCards.titleBar.text() ==~ "Edit Card Name"
		editAssayCards.enterCardName.value("")
		editAssayCards.enterCardName << cardUpdated
		editAssayCards.saveBtn.click()
		report "EditContextCard"
		waitFor(WAIT_INTERVAL, R_INTERVAL){ isCardPresent(cardUpdated) }

		when: "Context card is updated, Fetch updated card info to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Context card is updated, validate updated card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()

		when: "Context card is validate with UI and database"
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage

		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Validating card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}

	def "Test Delete Assay Card1"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def cGroup = "assay protocol> assay component>"
		def dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		def uiContexts = getUIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()

		then: "Delet Assay Context Card1"
		deletAssayCard(cardUpdated)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { !isCardPresent(cardUpdated) }
		assert !isCardPresent(cardUpdated)

		when: "Card1 is deleted, Fetch cards info to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Card1 is deleted, validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		report "DeleteContextCard1"

		when: "Card1 is deleted and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage

		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}

	def "Test Delete Assay Card2"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def cGroup = "assay protocol> assay component>"
		def dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		def uiContexts = getUIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()

		then: "Delet Assay Context Card2"
		deletAssayCard(card2)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { !isCardPresent(card2) }
		assert !isCardPresent(card2)

		when: "Card2 is deleted, Fetch cards info to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Card2 is deleted, validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		report "DeleteContextCard2"

		when: "Card1 is deleted and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage

		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = getUIContexts()

		then: "Validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}*/
}