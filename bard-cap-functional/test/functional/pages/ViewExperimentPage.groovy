package pages

import modules.ResultTableModule

import common.Constants
import common.TestData

/**
 * this is show experiment page class, most of the contents are inherited from CapScaffoldPage class to reuse.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/25
 */
class ViewExperimentPage extends CapScaffoldPage{
	static url="experiment/show/"+TestData.experimentId
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ title.contains("EID "+TestData.experimentId)} }

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
		for(int i=0; i<m.size()-1; i++){
			if(m[i] == '('){
				isValid = false
				//ignore the text till )
			}else if(m[i]==')'){
				isValid = true
				i++
				//get the text
			}
			if(isValid){
				result += m[i]
			}
		}
		return result
	}

	boolean isResultType(def value){
		boolean flag = false
		if(resultType){
			if(resultType.result(value.resultTypeId)){
				flag = true
			}
		}
		return flag
	}

	def deleteSpecificResultType(def value){
		if(resultTypeTable){
			withConfirm { resultType.deleteResultType(value).click() }
		}
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { !isResultType(value) }
	}

	def deleteResultTypes(){
		if(resultType.trashIcon){
			withConfirm { resultType.trashIcon.click() }
		}
		Thread.sleep(1000)
	}

	boolean isAnyResultType(){
		boolean flag = false
		if(resultType){
			if(resultType.find("span", text:contains("by any node in the tree below, means that the result type is a priority element"))){
				waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { resultType.resultList }
				flag = true
			}else if(resultType.find("p", text:contains("No result types specified"))){
				flag = false	
			}
		}
		return flag
	}

	def navigateToResultTypePage(){
		assert addResultTypeBtn
		addResultTypeBtn.click()
	}
	
}