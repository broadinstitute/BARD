/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pages

import modules.EditIconModule
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
