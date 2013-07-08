package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class InputFieldsModule extends Module {
	def buttonName
	static content = {
		fieldButton { $("input", type:"text", name: buttonName) }
		//searchBtn { $("input", name: "search") }
	}
}