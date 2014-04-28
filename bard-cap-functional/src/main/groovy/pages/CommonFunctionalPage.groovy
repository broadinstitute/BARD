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

import geb.Page

public class CommonFunctionalPage extends Page {
//	def seachADID = "Search by Assay Definition ID"
//	def seachADN = "Search by Assay Definition Name"
//	def seachPDN = "Search by Project Name"
//	def seachPDID = "Search by Project ID"
//	def createPD = "Create a New Project"
//	def capMenu = "CAP"
//	def ADMenu = "Assay Definitions"
//	def projectMenu = "Projects"
//	def webClientMenu = "Bard Web Client"
	
	static url = ""
	static at = {}
	static content = {
		formLoading { module LoadingModule }
		navigationMenu { module BardCapHeaderModule, $("ul.nav") }
	}

	boolean validationError(def element, def condition){
		waitFor{ element }
		if(element){
			if(element.text()){
				element.text().contains(condition)
				return true
			}
		}
		return false
	}

	boolean ajaxRequestCompleted(){
		waitFor { !formLoading.loading.displayed }
	}
	
	/*def navigateTo(NavigateTo to){
		switch(to){
			case NavigateTo.ASSAY_BY_ID:
				navigation(ADMenu, seachADID)
				break;
			case NavigateTo.ASSAY_BY_NAME:
				navigation(ADMenu, seachADN)
				break;
			case NavigateTo.PROJECT_BY_ID:
				navigation(projectMenu, seachPDID)
				break;
			case NavigateTo.PROJECT_BY_NAME:
				navigation(projectMenu, seachPDN)
				break;

		}
	}*/

	def navigation(def tabName, def menuName){
		assert navigationMenu.menuTab(tabName)
		navigationMenu.menuTab(tabName).click()
		assert navigationMenu.dropdownMenu(menuName)
		navigationMenu.dropdownMenu(menuName).click()
	}
	void waitForPageToLoad() {
		waitFor(15, 0.5) { title.contains("BARD: Catalog of Assay Protocols") }
	}
}
