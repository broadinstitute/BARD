package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class DialogButtonsSetModule extends Module {
	static content = {
		button { $("button") }
	}
}