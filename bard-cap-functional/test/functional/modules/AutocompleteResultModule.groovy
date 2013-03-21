package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class AutocompleteResultModule extends Module {
	
	static content = {
		autoList { $("a", it) }
		listItem { index -> autoList[index] }
	}
}