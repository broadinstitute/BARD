package pages

import modules.ButtonsModule;
import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class CreatePanelPage extends Page {
	static url = "panel/create"
	static at = { title.contains("Create Panel") }

	static content = {
		nameField(wait:true) { $("#name") }
		descriptionField { $("#description") }
		ownerRole { $("#ownerRole") }
		form { $("form") }
		cancelBtn { form.find("a.btn") }
		createBtn { form.find("input.btn.btn-primary", type:"submit") }
	}
	
	ViewPanelPage CreateNewPanel(def testData){
		nameField.value(testData.name)
		descriptionField.value(testData.description)
		ownerRole.value(testData.owner)
		createBtn.click()
		
		return new ViewPanelPage()
	}
}