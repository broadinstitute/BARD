package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class ButtonsModule extends Module {
	def buttonName
	static content = {
		button { $("a.btn", text:buttonName) }
		buttonBtn { $("button", text:buttonName) }
		buttonPrimary { $("a.btn.btn-small.btn-primary", text:buttonName) }
		buttonSubmitPrimary { $("button.btn.btn-primary", text:buttonName) }
		inputSubmitPrimary { $("input.btn.btn-primary", name:buttonName) }
		inputSubmitButton { $("input.btn.btn-primary", value:buttonName) }
	}
}