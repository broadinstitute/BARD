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
