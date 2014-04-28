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

import common.Constants.SearchBy;

import geb.Page

class CapSearchPage extends Page {
	static url = ""
	static at = {
		//		$("div.hero-unit-v1").find("h4").text() ==~ "Search Project by ID"
	}

	static content = {
		contentArea { $("div.bs-docs") }
		inputProjectId(wait: true, required: false) { contentArea.find("input", name:"projectId") }
		inputProjectName(wait: true, required: false) { contentArea.find("input", name:"projectName") }
		inputAssayId(wait: true, required: false) { contentArea.find("input", name:"assayId") }
		inputAssayName(wait: true, required: false) { contentArea.find("input", name:"assayName") }
		searchBtn(wait: true, required: false) { contentArea.find("input", name:"search") }
		
		resultAccordian { $("div#results_accordion") }
		accordianHeader { resultAccordian.find("h3") }
		gridTable { resultAccordian.find("table.gridtable") }
		resultTable  { gridTable.find("tbody").find("tr") }
	}

	def capSearchBy(SearchBy by, def searchQuery){
		switch(by){
			case SearchBy.ASSAY_ID:
				assert inputAssayId
				assert searchBtn
				inputAssayId << searchQuery
				searchBtn.click()
				break;
			case SearchBy.ASSAY_NAME:
				assert inputAssayName
				assert searchBtn
				inputAssayName << searchQuery
				searchBtn.click()
				break;
			case SearchBy.PROJECT_ID:
				assert inputProjectId
				assert searchBtn
				inputProjectId << searchQuery
				searchBtn.click()
				break;
			case SearchBy.PROJECT_NAME:
				assert inputProjectName
				assert searchBtn
				inputProjectName << searchQuery
				searchBtn.click()
				break;
		}

	}
	
	def searchResultCount(){
		return resultTable.size()
	}
	
	def seachResults(){
		def searchResult = []
		resultTable.each {columns ->
			def ids = columns.find("td")[0].text()
			searchResult.add(ids.toString())
		}
		return searchResult
	}
}
