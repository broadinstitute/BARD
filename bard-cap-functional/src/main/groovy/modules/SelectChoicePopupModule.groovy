package modules

import geb.Module
import geb.Page
import geb.navigator.Navigator

class SelectChoicePopupModule extends Module {
	def containerId
	static content = {
		label(wait: true, required: false) { $("label.control-label")}
		selectContainer(wait: true, required: false) { $("#$containerId") }
		selectChoice(wait: true, required: false) { selectContainer.find("a") }
		selectClose(wait: true, required: false) { selectContainer.find(".select2-search-choice-close") }
//		selectClose(wait: true, required: false) { selectChoice.find(".select2-search-choice-close") }
		selectToSearch(wait: true, required: false) { selectContainer.find("div.select2-search") }
//		searchInput(wait: true, required: false) { selectContainer.find(".select2-focusser.select2-offscreen") }
		searchInput(wait: true, required: false) { $("#s2id_autogen3") }
		
		offScreen(wait: true, required: false) { selectContainer.find(".select2-focusser.select2-offscreen") }
		selectArrow(wait: true, required: false) { selectContainer.find(".select2-arrow") }
	}
}