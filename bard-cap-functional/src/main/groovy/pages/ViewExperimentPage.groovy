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

import modules.ResultTableModule

import common.Constants
import common.TestData


/**
 * this is show experiment page class, most of the contents are inherited from CapScaffoldPage class to reuse.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/26
 */
class ViewExperimentPage extends CapScaffoldPage{
	static url="experiment/show/"+TestData.experimentId
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ title.contains("EID")} }

	static content = {
		resultTypeSection(wait:true, required: false) {$("#result-type-header")}
		noResultType(wait:true, required: false) {$("#result-type-header p", text:contains("No result types specified"))}
		resultTypeTable(wait:true, required: false) { $("#result-type-table") }
		resultType(wait:true, required: false) { module ResultTableModule, resultTypeSection }
		addResultTypeBtn{ resultTypeSection.find("a", text:contains("Add result type")) }
		addDoseResponseeBtn{ resultTypeSection.find("a", text:contains("Add dose response result type")) }
	}

	def getUIRsultTypes(){
		def results = []
		resultType.resultList.each{ i->
			String m = i.find("div")[0].text().toString()
			String s = measure(m)
			results.add(s.trim())
		}
		return results
	}

	String measure(String m){
		boolean isValid = true
		String result = ""
		for(int i=0; i<m.size(); i++){
			if(m[i] == '('){
				isValid = false
				//ignore the text till )
			}else if(m[i]==')'){
				isValid = true
				i++
				//get the text
			}
			if(i<m.size()){
				if(isValid){
					result += m[i]
				}
			}
		}
		return result
	}

	boolean isResultType(def value){
		boolean flag = false
		if(resultType){
			if(resultType.result(value)){
				flag = true
			}
		}
		return flag
	}

	ViewExperimentPage deleteSpecificResultType(def value){
		if(resultTypeTable){
			withConfirm { resultType.deleteResultType(value).click() }
		}
		waitFor(25000, 0.5) { !isResultType(value) }
		
		return new ViewExperimentPage()
	}

	ViewExperimentPage deleteResultTypes(){
		if(resultType){
			withConfirm { resultType.trashIcon.click() }
			waitFor(5000) {
				resultType
			}
		}
		return new ViewExperimentPage()
	}

	boolean isAnyResultType(){
		boolean flag = false
		if(resultType){
			if(resultType.find("span", text:contains("by any node in the tree below, means that the result type is a priority element"))){
				waitFor(25000,0.5) { resultType.resultList }
				flag = true
			}else if(resultType.find("p", text:contains("No result types specified"))){
				flag = false
			}
		}
		return flag
	}
	
	ResultTypePage navigateToResultTypePage(){
		assert addResultTypeBtn
		addResultTypeBtn.click()
		
		return new ResultTypePage()
	}
	
	ResultTypePage editResultTypePage(def value){
		assert resultType.editResultType(value)
		resultType.editResultType(value).click()
		
		return new ResultTypePage()
	}
	
	ResultTypePage navigateToDoseResultTypePage(){
		assert addDoseResponseeBtn
		addDoseResponseeBtn.click()
		
		return new ResultTypePage()
	}

}
