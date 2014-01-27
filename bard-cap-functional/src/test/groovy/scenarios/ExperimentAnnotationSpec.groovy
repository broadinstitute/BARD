package scenarios

import pages.ContextItemPage
import pages.EditContextPage
import pages.ViewExperimentPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.TestData

import db.Experiment

/**
 * This class holds all the test functions of Experiment Annotations including context card and context item add/edit/delete
 * @author Muhammad.Rafique
 * Date Created: 2013/11/27
 */
@Unroll
class ExperimentAnnotationSpec extends BardFunctionalSpec {
	static def section = "contexts-header"
	static def editContextGroup = "Unclassified"
	static def contextCard = "Test Card"
	static def dbContextType = "Unclassified"

	def setup() {
		logInSomeUser()

		given:"Navigate to Show Project page"
		to ViewExperimentPage

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		def uiContexts = getUIContexts(section)
		def dbContexts = Experiment.getExperimentContext(dbContextType, TestData.experimentId)
		def uiContextItems = getUIContextItems(section, contextCard)
		def dbContextItems = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

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

	def "Test #TestName Context Card in an Experiment"(){
		when:"At Edit Experiment Context Page, Add/Edit new context"
		at EditContextPage
		if(TestName == "Add and Delete"){
			addNewContextCard(editContextGroup, inputData)
		}else{
			addNewContextCard(editContextGroup, inputData)
			editContext(editContextGroup, inputData, inputData+TestData.edited)
		}
		def uiContentsAfterAdd = getUIContexts(editContextGroup)
		def dbContentsAfterAdd = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContexts(section)
		dbContentsAfterAdd = Experiment.getExperimentContext(dbContextType, TestData.experimentId)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		while(isContext(editContextGroup, contextCard)){
			deleteContext(editContextGroup, contextCard)
		}
		report "$TestName"

		where:"Execute tests with following conditions to add and edit contexts"
		TestName				| inputData
		"Add and Delete"		| contextCard
		"Edit and Delete"		| contextCard
	}

	def "Test Add #TestName Type Context Item in Experiment"(){
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
		def dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verify Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContextItems(section, contextCard)
		dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		navigateToEditContext(section)
		at EditContextPage
		report "$TestName"

		where: "Execute tests with following conditions to add Contexts items"
		TestName					| inputData						| contextItem
		"Element"					| TestData.contexts.Element		| TestData.contexts.Element.attribute
		"FreeText"					| TestData.contexts.FreeText	| TestData.contexts.FreeText.attribute
		"NumericValue"				| TestData.contexts.Numeric		| TestData.contexts.Numeric.attribute
		"ExOntologyIntegtegrated"	| TestData.contexts.ExtOntology	| TestData.contexts.ExtOntology.attribute

	}

	def "Test Edit #TestName Type Context Item in Experiment"(){
		when:"Add New Context Item and then edit it"
		at EditContextPage
		addNewContextCard(editContextGroup, contextCard)
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
		def dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verif eidted contexts Info with UI & DB"
		assert uiContentsAfterAdd.sort() == dbContentsAfterAdd.sort()
		finishEditing()

		when:"Fetch Contexts Info from UI and DB for validation"
		at ViewExperimentPage
		uiContentsAfterAdd = getUIContextItems(section, contextCard)
		dbContentsAfterAdd = Experiment.getExperimentContextItem(TestData.experimentId, dbContextType, contextCard)

		then:"Verifying Context Info with UI & DB"
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
		at ViewExperimentPage
		assert !isContext(section, contextCard)
	}

}