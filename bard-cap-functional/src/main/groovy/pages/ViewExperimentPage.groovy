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
