package pages

import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/11
 * Date Updated: 13/10/07
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