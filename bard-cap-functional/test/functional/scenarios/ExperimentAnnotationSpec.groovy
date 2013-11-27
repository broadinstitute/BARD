package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.HomePage
import pages.ViewExperimentPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Experiment

/**
 * This class holds all the test functions of Experiment Annotations including context card and context item add/edit/delete
 * @author Muhammad.Rafique
 * Date Created: 2013/11/27
 */
@Unroll
class ExperimentAnnotationSpec extends BardFunctionalSpec {
	def section = "contexts-header"
	def editContextGroup = "Unclassified"
	def contextCard = "Test Card"
	def dbContextType = "Unclassified"

	def setup() {
		logInSomeUser()
	}

	def "Test Add Context Card in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContentsBefore = getUIContexts(section)
		def dbContentsBefore = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContexts(section)
		dbContentsAfterAdd = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)

			when:"Context is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContexts(editContextGroup)
			def dbContentsAfterDelete = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContents = getUIContexts(section)
		def dbContents = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()

		report ""
	}

	def "Test Edit Context Card in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def editedContext = contextCard+Constants.edited
		def uiContentsBefore = getUIContexts(section)
		def dbContentsBefore = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContexts(section)
		dbContentsAfterAdd = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, editedContext)){
			deleteContext(editContextGroup, editedContext)

			when:"Context is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContexts(editContextGroup)
			def dbContentsAfterDelete = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContents = getUIContexts(section)
		def dbContents = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContents.sort() == dbContents.sort()

		report ""
	}

	def "Test Delete Context Card in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContentsBefore = getUIContexts(section)
		def dbContentsBefore = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterDelete = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterDelete = getUIContexts(section)
		dbContentsAfterDelete = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		report ""
	}

	def "Test Add #TestName Type Context Item in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage

		def uiContentsBefore = getUIContexts(section)
		def dbContentsBefore = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContextItems(section, contextCard)
		dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Experiment Definition Page"
		at ViewExperimentPage
		assert !isContext(section, contextCard)

		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute

	}
/*
	def "Test Add #TestName Type Context Item with Empty Values in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContentsBefore = getUIContexts(section)
		def dbContentsBefore = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Experiment Definition Page"
		at ViewExperimentPage
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
	def "Test Edit #TestName Type Context Item in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContentsBefore = getUIContextItems(section, contextCard)
		def dbContentsBefore = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		then:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContextItems(section, contextCard)
		dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()

		and:"Cleaning up Context Items"
		navigateToEditContext(section)
		at EditContextPage
		while(isContextItem(editContextGroup, contextCard, contextItem)){
			deleteContextItem(editContextGroup, contextCard, contextItem)

			when:"Context Item  is cleaned up, Fetching Contexts Info from UI and DB for validation"
			def uiContentsAfterDelete = getUIContextItems(editContextGroup, contextCard)
			def dbContentsAfterDelete = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

			then:"Verifying Context Info with UI & DB"
			assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()
		}
		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Experiment Definition Page"
		at ViewExperimentPage
		assert !isContext(section, contextCard)

		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
		//		"ExOntologyNoIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

	def "Test Delete #TestName Type Context Item in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Definition Page, Fetching Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContentsBefore = getUIContextItems(section, contextCard)
		def dbContentsBefore = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsBefore.size() == dbContentsBefore.size()
		assert uiContentsBefore.sort() == dbContentsBefore.sort()

		and:"Navigating to Edit Experiment Context Page"
		navigateToEditContext(section)

		when:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		then:"At Edit Experiment Context Page, Fetching Contexts Info from UI and DB for validation"
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
		def dbContentsAfterDelete = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterDelete.sort() == dbContentsAfterDelete.sort()

		and:"Cleaning up Contexts"
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}

		and:"Navigating to View Experiment Definition Page"
		finishEditing.buttonPrimary.click()

		and:"At View Experiment Definition Page"
		at ViewExperimentPage
		assert !isContext(section, contextCard)

		report "$TestName"

		where:
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute
	}

}