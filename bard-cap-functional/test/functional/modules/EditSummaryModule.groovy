package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class EditSummaryModule extends Module {
	static content = {
		label { $("dt") }
		ddValue { index -> $("dd")[index].find("input") }
		ddSelect { index -> $("dd")[index].find("#assayStatus") }
	}
}