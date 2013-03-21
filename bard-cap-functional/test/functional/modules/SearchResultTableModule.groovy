package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SearchResultTableModule extends Module {
	static content = {
		cell { $("td", it) }
		assayId { cell(0).find("a") }
		shortName { cell(1).text() }
		assayName { cell(2).text() }
		designedBy { cell(3).text() }
	}
}