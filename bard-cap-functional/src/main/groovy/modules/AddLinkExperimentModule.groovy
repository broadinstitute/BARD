package modules

import geb.Module
import geb.navigator.Navigator

class AddLinkExperimentModule extends Module {
	static content = {
		title { $("#ui-dialog-title-dialog_add_experiment_step") }
		addExperimentForm(wait: true, required: false) { $("#addExperimentForm") }
		experimentBy(wait: true, required: false) { index -> addExperimentForm.find("input", type:"radio", name:"addExperimentBy")[index] }
		addByExperimentId(wait: true, required: false) { addExperimentForm.find("input#addByExperimentId") }
		addByAssayId(wait: true, required: false) { addExperimentForm.find("input#addByAssayId") }
		addByExperimentName(wait: true, required: false) { addExperimentForm.find("input#addByExperimentName") }
		availableExperiments(wait: true, required: false) { addExperimentForm.find("#selectedExperiments") }
		selectStage { module SelectChoicePopupModule, containerId:"s2id_stageId" }
		selectToDrop { module SelectToDropModule, $("div.select2-drop.select2-drop-active") }
		
		linkExperimentForm(wait: true, required: false) { $("#linkExperimentForm") }
		fromExperiment { $("input#fromExperimentId") }
		toExperiment { $("input#toExperimentId") }
		buttonSet { $("div.ui-dialog-buttonset") }
		addExperimentBtn { module ButtonsModule, buttonSet, buttonName:"Add Experiment" }
		linkExperimentBtn { module ButtonsModule, buttonSet, buttonName:"Link Experiment" }
		cancelBtn { module ButtonsModule, buttonSet, buttonName:"Cancel" }
	}
}