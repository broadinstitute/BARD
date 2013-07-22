package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SelectChoicePopupModule extends Module {
	def containerId
	static content = {
		label(wait: true) { $("label.control-label")}
		selectContainer(wait: true) { $("div", id:containerId) }
		selectChoice(wait: true) { selectContainer.find("a") }
		selectClose(wait: true, required: false) { selectContainer.find("abbr.select2-search-choice-close") }
		selectClose(required: false) { selectChoice.find(".select2-search-choice-close") }
		selectToSearch(wait: true, required: false) { selectContainer.find("div.select2-search") }
		searchInput(wait: true, required: false) { selectToSearch.find("input.select2-input") }
	}
}