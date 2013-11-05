package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SelectToDropModule extends Module {
	static content = {
		dropdown(wait: true, required: false) { $("#select2-drop") }
		selectToResult(wait: true, required: false) { $("ul.select2-results") }
		searchResultSearching(wait: true, required: false) { selectToResult.find("li.select2-searching") }
		searchNoResult(wait: true, required: false) { dropdown.find("li.select2-no-results") }
		searchResult(wait: true, required: false) { dropdown.find("li") }

		selectToSearch(wait: true, required: false) { $("div.select2-search") }
		searchInput(wait: true, required: false) { dropdown.find("input.select2-input") }
		selectDropMask(wait: true, required: false){ $("#select2-drop-mask")}
	}
}