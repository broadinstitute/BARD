package pages

import geb.Page
import geb.Module

class CAPUtilPage extends Page {
	static url = ""
	static at = {}
	static content = {
		
	}
}

class SearchFieldsModule extends Module {
	def searchBtns
	static content = {
		inputBtns { $("input", name: searchBtns) }
		searchBtn { $("input", name: "search") }
	}
}

class AutocompleteResult extends Module {
	static content = {
		items { $("a", it) }
		itemsList { items(0) }
		itemsListSize { itemsList.size() }
	}
}

class SearchResultTable extends Module {
	static content = {
		cell { $("td", it) }
		assayId { cell(0).find("a") }
		shortName { cell(1).text() }
		assayName { cell(2).text() }
		designedBy { cell(3).text() }
	}
}