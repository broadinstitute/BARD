package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SearchFieldsModule extends Module {
	def searchBtns
	static content = {
		inputBtns { $("input", name: searchBtns) }
		searchBtn { $("input", name: "search") }
	}
}