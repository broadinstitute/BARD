package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

/*class SearchFieldsModule extends Module {
	def searchBtns
	static content = {
		inputBtns { $("input", name: searchBtns) }
		searchBtn { $("input", name: "search") }
	}
}

class AutocompleteResultModule extends Module {
	
	static content = {
		autoList { $("a", it) }
		listItem { index -> autoList[index] }
	}
}

class SearchResultTableModule extends Module {
	static content = {
		cell { $("td", it) }
		assayId { cell(0).find("a") }
		shortName { cell(1).text() }
		assayName { cell(2).text() }
		designedBy { cell(3).text() }
	}
}

class SummaryModule extends Module {
	static content = {
		label { $("dt") }
		value { $("dd") }
	}
}

class SelectInputModule extends Module {
	static content = {
		select2Input { $("div.select2-drop.select2-drop-active")}
		enterResult(wait: true) { select2Input.find("input.select2-input") }
	}
}

class SelectResultPopListModule extends Module {
	static content = {
		resultPopup(wait: true) { $("li.select2-results-dept-0.select2-result.select2-result-selectable.select2-highlighted") }
	}
}

class SelectToContainer extends Module {
	static content = {
		selectLink(wait: true, required: false) { $("a") }
	}
}*/