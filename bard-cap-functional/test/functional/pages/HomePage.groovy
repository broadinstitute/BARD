package pages

import geb.Page

class HomePage extends ScaffoldPage {
	static url = ""
	static at = {
		$("h3").text() ==~ "CAP - Catalog of Assay Protocols"
	}

	static content = {
		logOut { $("form#logoutForm").find("button") }
		bardBrandLogo { $("a.brand") }
		//		navigationMenu {index -> moduleList BardCapHeaderModule, $("ul.nav").children(), index }
	}
}