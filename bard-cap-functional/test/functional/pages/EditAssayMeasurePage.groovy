import java.awt.TextArea;

import geb.Page
import geb.Module
import geb.textmatching.TextMatcher;

class EditAssayMeasurePage extends Page{
	static url=""
	static at = { waitFor(10, 0.5){$("div.span12.well.well-small").find("h4").text().contains("Editing Measures") }
	}

	static content = {
		finishEditingBtn { $("a.btn.btn-small.btn-primary")}  // Finish Editing asssay measure btn
		addTopMeasureBtn { $("a#add-measure-at-top") } // add Top Measure button

		measuresHolder(wait: true) { $("ul.dynatree-container").find("li") }
		
		addMeasureModule {  module AddNewMeasureModule }
		measureDetailModule {  module MeasureDetailsModule }

	}

	def newMeasure(resultType, resultVal){
		waitFor(10, 5){ addMeasureModule.selectResultType }
		assert addMeasureModule.selectResultType
	//	assert addMeasureModule.enterResult
		
		addMeasureModule.selectResultType.click()
		addMeasureModule.enterResult.value("$resultType")
		waitFor(10, 5){ addMeasureModule.resultPopup }
		addMeasureModule.resultPopup.click()

		addMeasureModule.selectStatistics.click()
		addMeasureModule.enterResult.value("$resultVal")
		waitFor(10, 5){ addMeasureModule.resultPopup }
		addMeasureModule.resultPopup.click()

		addMeasureModule.footerBtns[1].click()
		waitFor(10, 3) { measuresHolder }
	}

	def isMeasureAdded(resultType, resultVal){
		waitFor(){ measuresHolder }
		println $("a.dynatree-title", text:"$resultType ($resultVal)")
		assert $("a.dynatree-title", text:"$resultType ($resultVal)")
	}
	
	def navigateToChildMeasure(topMeasureType, topMeasureVal, childMeasureType, childMeasureVal){
		measuresHolder.find("a", text:"$topMeasureType ($topMeasureVal)").parent().find("span")[0].click()
		measuresHolder.find("ul").find("a", text:"$childMeasureType ($childMeasureVal)").click()
		
	//	waitFor{ measureDetailModule.measuresDetails("$childMeasureType").find("form.form-horizontal") }
	}
}

class AddNewMeasureModule extends Module {

	static content = {
		addNewMeasureTitle(wait:true) { $("h3#saveModalLabel").text() ==~"Add a new measure" }

		selectResultType(wait: true) { $("div#s2id_resultTypeId").find("a.select2-choice.select2-default")}
		selectStatistics(wait: true) { $("div#s2id_statisticId").find("a.select2-choice.select2-default")}

		enterResult(wait:true) { $("div.select2-drop.select2-drop-active").find("input") }
		resultPopup { $("li.select2-results-dept-0.select2-result.select2-result-selectable.select2-highlighted") }

		footerBtns { $("div.modal-footer").find("button") }
		//	cancelBtn { $("button.btn.btn-primary", text:"Save").next() }
	}
}

class MeasureDetailsModule extends Module {

	static content = {
		measuresDetails { value -> $("div.measure-detail-card").find("h1", text:"Measure: $value").parent() }
		addChildMeasureBtn { val -> $("a.btn", text:"Click to add new measure under $val").click() }
	
//		addAssociation { value -> $("#assayContextId").value("$value") }
//		associateBtn { $("button.btn", text:"Associate") }
	
	}
}
