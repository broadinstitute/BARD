package pages

import geb.Module;
import geb.Page

class ViewProjectDefinitionPage extends Page{
	static url=""
	static at = { waitFor(10, 0.5){	$("h4").text().contains("View Project")} }

	static content = {
		viewProjectDefinition {$("div", class:"pull-left").find("h4").text()}
		
		capHeaders { module BardCapHeaderModule }
		projectSummary { module ProjectSummaryModule }
		experimentBtns { module ExperimentBtnsModule }
	}
}

class ProjectSummaryModule extends Module {
	static content = {
		summaryDetailsSection { $("div#summaryDetailSection") }
		projectName { summaryDetailsSection.find("dt", text:"Name") }
		projectId { summaryDetailsSection.find("dt", text:"ID") }
	}
}

class ExperimentBtnsModule extends Module {
	static content = {
		addExperimentBtn { $("button#addExperimentToProject") }
		linkExperimentBtn { $("button#linkExperiment") }
		redrawExperimentBtn { $("button#redraw") }
	}
}