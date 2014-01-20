package main.groovy.pages

import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class CreateProjectPage extends Page {
	static url = "project/create"
	static at = { title.contains("Create New Project") }

	static content = {
		nameField { $("#name") }
		descriptionField { $("#description") }
		projectStatus{ $("#projectStatus") }
		groupType{ $("#projectGroupType") }
		ownerRole { $("#ownerRole") }
		form { $("form") }
		cancelBtn { form.find("a.btn") }
		createBtn { form.find("input.btn.btn-primary", type:"submit") }
	}
	
	ViewProjectDefinitionPage CreateNewProject(def testData){
		nameField.value(testData.name)
		descriptionField.value(testData.description)
		projectStatus.value(testData.status)
		groupType.value(testData.group)
		ownerRole.value(testData.owner)
		createBtn.click()
		
		return new ViewProjectDefinitionPage()
	}
}