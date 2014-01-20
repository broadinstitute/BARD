package main.groovy.pages

import main.groovy.common.TestData;
import geb.Page

/**
 * This class includes create experiment page objects and also related functions.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class CreateExperimentPage extends Page {
	static url = "experiment/create?assayId="+TestData.assayId
	static at = { title.contains("Create Experiment") }

	static content = {
		nameField { $("#experimentName") }
		descriptionField{ $("#description") }
		ownerRole { $("#ownerRole") }
		form { $("form") }
		cancelBtn { form.find("a.btn") }
		createBtn { form.find("input.btn.btn-primary", type:"submit") }
	}
	
	ViewExperimentPage CreateNewExperiment(def testData){
		nameField.value(testData.name)
		descriptionField.value(testData.description)
		ownerRole.value(testData.owner)
		createBtn.click()
		
		return new ViewExperimentPage()
	}
}