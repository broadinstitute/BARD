package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SelectInputModule extends Module {
	static content = {
		select2Input { $("div.select2-drop.select2-drop-active")}
		enterResult(wait: true) { select2Input.find("input.select2-input") }
	}
}