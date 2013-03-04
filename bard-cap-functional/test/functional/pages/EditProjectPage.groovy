package pages

import geb.Module;
import geb.Page
import pages.ViewProjectDefinitionPage

class EditProjectPage extends Page{
	static url=""
	static at = { waitFor(10, 0.5){	$("h4").text().contains("View Project")} }

	static content = {
		viewProjectDefinition {$("div", class:"pull-left").find("h4").text()}

		capHeaders { module BardCapHeaderModule }

		associateExpriment { module AssociateExperimentModule }
		exprimentCanvas { module ExprimentCanvasModule, $("div#canvasIsolated") }
		linkExpriment { module LinkExperimentModule }
	}
	
	def addNewExperiment(exprimentName){
		assert associateExpriment.titleBar
		associateExpriment.experimentBy.addExperimentBy("ExperimentName")
		associateExpriment.experimentBy.addExperimentByValue("ExperimentName") << exprimentName 
		waitFor { associateExpriment.popupList.itemsList }
		associateExpriment.popupList.requiredItem(0).click()
		String expName = associateExpriment.availableExperiments.exprimentsList[0].text()
		associateExpriment.availableExperiments.exprimentsList[0].value(expName).click()
		def experimentId = expName.takeWhile { it != '-' }
		associateExpriment.stageSelect.stageLink.click()
		associateExpriment.stageSelect.stageField << stageValue
		waitFor { associateExpriment.stageSelect.resultPopup }
		associateExpriment.stageSelect.resultPopup.click()
		assert associateExpriment.addExprimentBtn
		associateExpriment.addExprimentBtn.click()
		waitFor(20, 5){	exprimentCanvas.addedExperiment(experimentId) }
		def ci = exprimentCanvas.addedExperiment(experimentId)
		assert ci
		ci.parent().click()
	}
}

class AssociateExperimentModule extends Module {
	static content = {
		titleBar { $("span#ui-dialog-title-dialog_add_experiment_step") }
		addExprimentBtn { $("div#dialog_add_experiment_step").parent().find("button", type:"button", text:"Add Experiment") }
		cancelBtn { $("div#dialog_add_experiment_step").parent().find("button", type:"button", text:"Cancel") }

		experimentBy { module ExperimentFormModule, $("form#addExperimentForm") }
		popupList { module AutoPopupList }
		availableExperiments { moduleList AvailableExpriments, $("form#addExperimentForm") }
		stageSelect { module StageModule }
	}
}


class ExperimentFormModule extends Module {
	static content = {
		addExperimentBy { expByType -> $("input", type:"radio", name:"addExperimentBy").value("addBy$expByType") }
		addExperimentByValue {expByType -> $("input", type:"text", name:"addBy$expByType") }
		
		linkExpFromTo {fromToExpId -> $("input", type:"text", name:"$fromToExpId") }
	}
}

class AutoPopupList extends Module {
	static content = {
		itemsList { $("li.ui-menu-item") }
		requiredItem {index -> itemsList[index].find("a") }
	}
}

class AvailableExpriments extends Module {
	static content = {
		availableExp { $("div#availableExperiment")}
		exprimentsList(wait: true) { $("#selectedExperiments") }
	}
}

class StageModule extends Module {
	static content = {
		stageLink { $("div#s2id_stageId").find("a") }
		stageField { $("input.select2-input") }
		resultPopup { $("li.select2-results-dept-0.select2-result.select2-result-selectable.select2-highlighted") }
	}
}

class ExprimentCanvasModule extends Module {
	static content = {
		expCanvasList { $("tspan") }
		expCanvasChildren { $("svg").children() }
		addedExperiment { expId -> $("svg").find("tspan", text:expId) }

	}

	def getExpId(expCanvasList, exp){
		def getExpId
		expCanvasList.each {item ->
			println item.text()
			if(item.text()==exp){
				getExpId { item }
			}

		}
		return getExpId
	}
}

class LinkExperimentModule extends Module {
	static content = {
		titleBar { $("span#ui-dialog-title-dialog_link_experiment") }
		linkExprimentBtn { $("div#dialog_link_experiment").parent().find("button", type:"button", text:"Link Experiment") }
		cancelBtn { $("div#dialog_link_experiment").parent().find("button", type:"button", text:"Cancel") }

		experimentForm { module ExperimentFormModule, $("form#linkExperimentForm") }
	}
}
