package pages

import geb.Module
import geb.Page
import modules.BardCapHeaderModule

class EditProjectPage extends Page{
	static url=""
	static at = { waitFor(10, 0.5){	$("h4").text().contains("View Project")} }

	static content = {
		viewProjectDefinition {$("div", class:"pull-left").find("h4").text()}

		capHeaders { module BardCapHeaderModule }
		associateExpriment { module AssociateExperimentModule }
		exprimentCanvas { module ExprimentCanvasModule, $("div#canvasIsolated") }
		linkExpriment { module LinkExperimentModule }
		//editProjectSummary { module EditSummaryModule }
		//projectSummary { module SummaryModule, $("div#summaryDetailSection") }
	}
}

/*class EditSummaryModule extends Module {
	static content = {
		
		titleBar { $("span#ui-dialog-title-dialog_edit_project_summary") }
		editSummaryForm { $("form#editSummaryForm") }
		projNameField { editSummaryForm.find("input#projectName") }
		projDescField { editSummaryForm.find("input#description") }
		formCon { $("div#dialog_edit_project_summary") }
		updateBtn { formCon.parent().find("button", type:"button", text:"Update Summary") }
		cancelBtn { formCon.parent().find("button", type:"button", text:"Cancel") }
	}
}*/


class AssociateExperimentModule extends Module {
	static content = {
		titleBar { $("span#ui-dialog-title-dialog_add_experiment_step") }
		addExprimentBtn { $("div#dialog_add_experiment_step").parent().find("button", type:"button", text:"Add Experiment") }
		cancelBtn { $("div#dialog_add_experiment_step").parent().find("button", type:"button", text:"Cancel") }

		experimentBy { module ExperimentFormModule, $("form#addExperimentForm") }
		popupList { moduleList AutocompleteResult, $("li.ui-menu-item") }
		availableExperiments { module AvailableExpriments, $("form#addExperimentForm") }
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

class AvailableExpriments extends Module {
	static content = {
		availableExp { $("div#availableExperiment")}
		exprimentsList(wait: true) { $("#selectedExperiments") }
	}
}

class StageModule extends Module {
	static content = {
		stageLink(wait: true) { $("div#s2id_stageId").find("a") }
		stageField(wait: true) { $("input.select2-input") }
		resultPopup(wait: true) { $("li.select2-results-dept-0.select2-result.select2-result-selectable.select2-highlighted") }
	}
}

class ExprimentCanvasModule extends Module {
	static content = {
		expCanvasList { $("tspan") }
		expCanvasChildren { $("svg").children() }
		addedExperiment { expId -> $("svg").find("tspan", text:expId) }

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
