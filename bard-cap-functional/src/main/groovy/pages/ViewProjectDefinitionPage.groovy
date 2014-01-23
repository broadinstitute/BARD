package pages

import common.TestData


/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ViewProjectDefinitionPage extends CapScaffoldPage{
	static url="project/show/"+TestData.projectId
	static at = { $("h2").text().contains("Project") }

	static content = {
//		experimentHeader { $("#experiment-and-step-header") }
//		addExperimentBtn { module ButtonsModule, experimentHeader, buttonName:"Add Experiment" }
//		linkExperimentBtn { module ButtonsModule, experimentHeader, buttonName:"Link Experiments" }
//		addLinkExperiment { module AddLinkExperimentModule }
//		experimentBtns { module ExperimentBtnsModule }
	}

/*	def addExperiment(def addByValue, def stageVal){
		assert addExperimentBtn.buttonBtn
		addExperimentBtn.buttonBtn.click()
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ addLinkExperiment.title }
		addLinkExperiment.experimentBy(0).click()
		waitFor { addLinkExperiment.addByExperimentId.displayed }
		addLinkExperiment.addByExperimentId.value("")
		addLinkExperiment.addByExperimentId.value(addByValue)
		addLinkExperiment.availableExperiments.click()
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){ addLinkExperiment.availableExperiments.size() > 0 }
		addLinkExperiment.availableExperiments[0].click()
		addLinkExperiment.selectStage.selectChoice.click()
		selectAutoChoiceValue(addLinkExperiment.selectStage, addLinkExperiment.selectToDrop, stageVal)
		addLinkExperiment.addExperimentBtn.click()
	}
	
	def selectAutoChoiceValue(def element1, def element2,  def inputValue){
		element1.selectChoice.click()
		int index = 0
		element1.searchInput.value(inputValue)
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { element2.searchResult[index].text() != "Searching..." && element2.searchResult[index].text() != "Please enter 1 more characters"}
		element2.searchResult[index].click()
		waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL) { !formLoading.loading.displayed }
	}*/
	
}