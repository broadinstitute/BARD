import java.awt.TextArea;
import java.util.Map;

import geb.Page
import geb.Module
import geb.navigator.Navigator
import modules.SelectToDropModule
import modules.SelectChoicePopupModule
import modules.SelectResultPopListModule
import modules.CardsHolderModule

class EditAssayMeasurePage extends Page{
	final static WAIT_INTERVAL = 15
	final static R_INTERVAL = 3
	final static SLEEP_INTERVAL = 2000
	static url=""
	static at = { waitFor(WAIT_INTERVAL, R_INTERVAL){$("div.span12.well.well-small").find("h4").text().contains("Editing Measures") }
	}

	static content = {
		finishEditingBtn { $("a.btn.btn-small.btn-primary")} 	// Finish Editing asssay measure btn
		addTopMeasureBtn { $("a#add-measure-at-top") } 			// add Top Measure button

		measuresHolder(wait: true) { $("ul.dynatree-container").find("li") }
		addMeasureForm { $("form#add-measure-form") }
		selectResultTye { module SelectToDropModule, $("div#s2id_resultTypeId") }
		selectStatistics { module SelectToDropModule, $("div#s2id_statisticId") }
		measureDetail { module MeasureDetailsModule }
		enterInput { module SelectChoicePopupModule }
		resultPopulated { module SelectResultPopListModule, $("div.select2-drop.select2-drop-active") }
		footerBtns { $("div.modal-footer").find("button") }
	}

	def newMeasure(resultType, resultVal){
		waitFor(WAIT_INTERVAL, R_INTERVAL){ selectResultTye.selectLink.displayed }
		assert selectResultTye.selectLink
		selectResultTye.selectLink.click()
		waitFor { enterInput.enterResult }
		enterInput.enterResult.value(resultType)
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ resultPopulated.resultPopup }
		resultPopulated.resultPopup[0].click()
		assert selectStatistics.selectLink
		selectStatistics.selectLink.click()
		waitFor { enterInput.enterResult }
		enterInput.enterResult.value(resultVal)
		Thread.sleep(SLEEP_INTERVAL)
		waitFor(WAIT_INTERVAL, R_INTERVAL){ resultPopulated.resultPopup }
		resultPopulated.resultPopup[0].click()
		footerBtns[1].click()
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isMeasure(resultType, resultVal) }
	}

	def getUIMeasures(){
		def uiMeasures = []
		if(measuresHolder){
			measuresHolder.each{ measureName ->
				def m = measureName.text()
				uiMeasures.add(m.takeWhile { it != '('}.trim())
			}
		}
		return uiMeasures
	}

	boolean isMeasure(def measureName, def stat){
		boolean found = false
		if(measuresHolder){
			measuresHolder.each{ measures ->
				def m = measures.text()
				if(m.contains(measureName) && m.contains(stat)){
					found = true
				}
			}
		}
		return found
	}

	def navigateToChildMeasure(topMeasureType, topMeasureVal, childMeasureType, childMeasureVal){
		def treeNaviSpan = measuresHolder.find("a", text:"$topMeasureType ($topMeasureVal)")
		treeNaviSpan.parent().next().find("a").click()
	}

	Map<String, String> getUIAssociatedContext(mName){
		def associateMeasurContexts = [:]
		def mTable = $("h1", text:"Measure: $mName").parent().find("table.table.table-hover")
		if(mTable){
			mTable.each { cards ->
				def cardCaption = cards.find("div.cardTitle").find("p")
				def context = cardCaption[0].text()
				associateMeasurContexts = ['measure':mName, 'context':context]
			}
		}
		return associateMeasurContexts
	}

}


class MeasureDetailsModule extends Module {
	static content = {
		assayContext(wait: true) { mName -> $("h1", text:"Measure: $mName").parent().find("#assayContextId")}
		associateBtn(wait: true) { mName -> $("h1", text:"Measure: $mName").parent().find("button", text:"Associate") }
		addChildMeasureBtn(wait: true) { BtnText -> $("a.btn", text:"Click to add new measure under $BtnText") }
		disasiciateBtn(wait: true) { BtnText -> $("button", text:"Disassociate context from $BtnText") }
		deleteMeasure(wait: true) { BtnText -> $("button", text:"Click to delete $BtnText"+" entirely")}
		associatedCard(wait: true) { mName -> $("h1", text:"Measure: $mName").parent().find("table.table.table-hover").find("caption") }
	}
}