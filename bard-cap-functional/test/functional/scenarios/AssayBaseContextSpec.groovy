package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import spock.lang.Unroll;
import base.BardFunctionalSpec
import common.Constants
import common.TestData
import common.Constants.ContextItem
import common.Constants.ExpectedValueType
import db.Assay

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Unroll
abstract class AssayBaseContextSpec extends BardFunctionalSpec {
	def contextCard = "test card"
	def section
	def cardGroup
	def editContextGroup
	def dbContextType
//	def oldGroup

	def "Test Add Context Card in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)

			when:"Context is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContexts(editContextGroup)
			def dbContentsAfterDelete = Assay.getAssayContext(dbContextType, TestData.assayId)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContexts(cardGroup)
		def dbContents = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
		
		report ""
	}

	def "Test Edit Context Card in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def editedContext = contextCard+Constants.edited
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"Edting Context"
		if(!isContext(editContextGroup, contextCard)){
			addNewContextCard(editContextGroup, contextCard)
			waitFor { isContext(editContextGroup, contextCard) }
			editContext(editContextGroup, contextCard, editedContext)
		}else{
			editContext(editContextGroup, contextCard, editedContext)
		}

		and:"Verifying Context edited successfully"
		assert isContext(editContextGroup, editedContext)

		when:"Context is edited, Fetching Contexts Info from UI and DB for validation"
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterAdd = getUIContexts(cardGroup)
		dbContentsAfterAdd = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, editedContext)){
			deleteContext(editContextGroup, editedContext)

			when:"Context is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContexts(editContextGroup)
			def dbContentsAfterDelete = Assay.getAssayContext(dbContextType, TestData.assayId)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At VIew Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContents = getUIContexts(cardGroup)
		def dbContents = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()
		
		report ""
	}

	def "Test Delete Context Card in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContexts(cardGroup)
		def dbContentsBefore = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterDelete = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Assay Page"
		finishEditing.buttonPrimary.click()

		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		uiContentsAfterDelete = getUIContexts(cardGroup)
		dbContentsAfterDelete = Assay.getAssayContext(dbContextType, TestData.assayId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		
		report ""
	}

	def "Test Add #TestName Type Context Item in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()
		
		and:"Navigating to Edit Assay Context Page"
		navigateToEditContext(section)
		
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

		and:"Navigating to View Assay Page"
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
		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
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
	def "Test Assay Context Item Add with Element Type having Element field empty"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		if(!isContext(section, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Assay Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(section, contextCard)
			def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Assay Context Page"
			navigateToEditContext(section)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

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
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
		assert !isContext(section, contextCard)
		
		report "AssayContextItemAddwithElementTypeEmpty"
	}

	def "Test Assay Context Item Add with Element Type having Element value empty"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		def contextItem = TestData.ValueType_Element.AttributeFromDictionary
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		if(!isContext(section, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Assay Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(section, contextCard)
			def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Assay Context Page"
			navigateToEditContext(section)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

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
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
		assert !isContext(section, contextCard)
		
		report "AssayContextItemAddwithElementTypeValueEmpty"
	}

	def "Test Assay Context Item Add with Free Text Type having Display Value empty"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		def contextItem = TestData.ValueType_FreeTextwithoutDisplayValue.AttributeFromDictionary
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		if(!isContext(section, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Assay Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(section, contextCard)
			def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Assay Context Page"
			navigateToEditContext(section)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

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
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
		assert !isContext(section, contextCard)
		
		report "AssayContextItemAddwithFreeTextTypeEmpty"
	}

	def "Test Assay Context Item Add with Numeric Value Type having Numeric Value empty"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		def contextItem = TestData.ValueType_NumericValuewithoutNumericValue.AttributeFromDictionary
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		if(!isContext(section, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Assay Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(section, contextCard)
			def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Assay Context Page"
			navigateToEditContext(section)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

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
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
		assert !isContext(section, contextCard)
		
		report "AssayContextItemAddwithNumericValueTypeEmpty"
	}

	def "Test Assay Context Item Add with External Ontology Type having Ontology values empty"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		def contextItem = TestData.ValueType_ExternalOntologywithoutValues.AttributeFromDictionary
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		if(!isContext(section, contextCard)){
			navigateToEditContext(section)
			then:"At Edit Assay Context Page, Adding Context"
			at EditContextPage
			addNewContextCard(editContextGroup, contextCard)
		}else{
			def uiContentsBefore = getUIContextItems(section, contextCard)
			def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsBefore.size() == dbContentsBefore.size()
			assert uiContentsBefore.sort() == dbContentsBefore.sort()

			and:"Nagating to Edit Assay Context Page"
			navigateToEditContext(section)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		def uiContentsBefore = getUIContextItems(editContextGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

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
		def dbContentsAfterAdd = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.size() == uiContentsBefore.size()
		assert dbContentsAfterAdd.size() == dbContentsBefore.size()
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
		assert !isContext(section, contextCard)
		
		report "AssayContextItemAddwithExternalOntologyTypeEmpty"
	}
*/
	
	def "Test Edit #TestName Type Context Item in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)

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
		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
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
/*
	def "Test Delete #TestName Type Context Item in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Contexts Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiContentsBefore = getUIContextItems(cardGroup, contextCard)
		def dbContentsBefore = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Nagating to Edit Assay Context Page"
		navigateToEditContext(section)
		
		when:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"At Edit Assay Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterDelete = Assay.getAssayContextItem(TestData.assayId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Assay Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Assay Definition Page"
		at ViewAssayDefinitionPage
		assert !isContext(cardGroup, contextCard)
		
		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}
	*/
}