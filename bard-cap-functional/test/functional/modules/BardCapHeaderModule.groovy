package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class BardCapHeaderModule extends Module {
	static content = {
		menuTab { $("a") }
		dropdownMenu {index-> $("ul.dropdown-menu").find("a")[index] }
	}
}