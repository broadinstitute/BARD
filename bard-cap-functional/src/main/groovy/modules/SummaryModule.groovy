package modules

import geb.Module
import geb.Page
import geb.navigator.Navigator

class SummaryModule extends Module {
	static content = {
		dtLabel { $("dt") }
		ddValue { $("dd") }
	}
}