package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class EditSummaryModule extends Module {
	static content = {
		title { $("span.ui-dialog-title") }
		label { $("dt") }
		ddValue { index -> $("dd")[index].find("input") }
		ddSelect { index -> $("dd")[index].find("#assayStatus") }
		ddPSelect { index -> $("dd")[index].find("#projectStatus") }
	}
}