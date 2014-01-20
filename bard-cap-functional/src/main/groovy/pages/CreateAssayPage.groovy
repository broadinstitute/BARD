package main.groovy.pages

import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class CreateAssayPage extends Page {
	static url = "assayDefinition/create"
	static at = { title.contains("Create Assay Definition") }

	static content = {
		nameField { $("#assayName") }
		assayType{ $("#assayType") }
		ownerRole { $("#ownerRole") }
		form { $("form") }
		cancelBtn { form.find("a.btn") }
		createBtn { form.find("input.btn.btn-primary", type:"submit") }
	}
	
	ViewAssayDefinitionPage CreateNewAssay(def testData){
		nameField.value(testData.name)
		assayType.value(testData.definitionType)
		ownerRole.value(testData.owner)
		createBtn.click()
		
		return new ViewAssayDefinitionPage()
	}
}