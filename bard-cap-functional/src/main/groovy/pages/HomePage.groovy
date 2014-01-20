package main.groovy.pages

import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/11
 */
class HomePage extends ScaffoldPage {
	static url = "bardWebInterface/index"
	static at = { $("div.head-holder h2").text() ==~ "SEARCH BARD" }

	static content = {
		logOut { $("form#logoutForm").find("button") }
		bardBrandLogo { $("a.brand") }
		//		navigationMenu {index -> moduleList BardCapHeaderModule, $("ul.nav").children(), index }
	}
}