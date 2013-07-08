package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class BardCapHeaderModule extends Module {
//	def index_0 = 0
//	def index_1 = 1
//	def index_4 = 4
//	def index_7 = 7
	static content = {
		menuTab { $("a") }
		dropdownMenu {index-> $("ul.dropdown-menu").find("a")[index] }
	}
}