package webtests

import geb.Page

class ScaffoldPage extends Page {
	static content = {
		heading { $("h1") }
		message { $("i.icon-shopping-cart")}
	}
}