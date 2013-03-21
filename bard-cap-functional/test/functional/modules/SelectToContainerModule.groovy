package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SelectToContainerModule extends Module {
	static content = {
		selectLink(wait: true, required: false) { $("a") }
	}
}