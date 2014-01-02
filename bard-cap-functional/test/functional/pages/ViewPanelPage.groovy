package pages

import geb.Page
import modules.EditIconModule;
import modules.SummaryModule
import common.TestData
/**
 * This class hold methods which are performed on show panel page. 
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class ViewPanelPage extends CapScaffoldPage {

	static url = "panel/show/"+TestData.panelId
	static at = { title.contains("Panel ID") }
	static content = {
		viewSummary { module SummaryModule, $("#showSummary") }	//This summary content can be overridden in CapScaffoldPage class
		deletePanelButton { module EditIconModule, $("#showSummary").find("a.btn", title:"Delete Panel") }
		addAssayToPanelButton { module EditIconModule, $("#showSummary").find("a.btn", text:contains("Add Assay Definitions To This Panel")) }
		panelAssayTable(wait:true, required: false) { $("table.table tbody tr") }
	}

	MyPanelPage deletePanel(){
		assert deletePanelButton
		withConfirm { deletePanelButton.iconTrash.click() }
		
		return new MyPanelPage()
	}

	PanelAddAssayPage navigateToAddAssayToPanel(){
		assert addAssayToPanelButton
		addAssayToPanelButton.iconPlus.click()
		
		return new PanelAddAssayPage()
	}
	
	def getUiPanelAssays(){
		def panelAdids = []
		if(panelAssayTable){
			panelAssayTable.each {
				panelAdids.add(it.find("td")[0].text().toInteger())
			}
		}
		return panelAdids
	}

	boolean deletePanelAssay(){
		while(panelAssayTable){
			withConfirm  { panelAssayTable.find("td")[2].find("a").click() }
		}
		return true
	}
	
}
