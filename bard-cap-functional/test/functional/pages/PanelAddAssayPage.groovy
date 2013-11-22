package pages

import common.TestData;

import modules.ButtonsModule;
import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class PanelAddAssayPage extends Page {
	static url = "panel/addAssays/"+TestData.panelId
	static at = { title.contains("Add Assay") }

	static content = {
		assayIds { $("#assayIds") }
		form { $("form") }
		cancelBtn { form.find("a.btn") }
		addToPanelBtn { form.find("input.btn.btn-primary", type:"submit") }
	}
	
	def AddAssaysToPanel(def assays){
		assert assayIds
		String assayList = assays.join(' ')
		assayIds.value(assayList)
		addToPanelBtn.click()
	}
	
}