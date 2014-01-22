package modules

import geb.Module
import geb.Page
import geb.navigator.Navigator

class BardCapHeaderModule extends Module {
	static content = {
//		menuTab { $("a") }
		menuTab { menuText -> $("a", text:contains(menuText)) }
//		dropdownMenu {index-> $("ul.dropdown-menu").find("a")[index] }
		dropdownMenu {menuText-> $("ul.dropdown-menu").find("a", text:contains(menuText)) }
	}
}