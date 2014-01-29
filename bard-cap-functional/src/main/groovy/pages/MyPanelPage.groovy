package pages

import geb.Page

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/11/20
 */
class MyPanelPage extends Page {
	static url = "panel/myPanels"
	static at = { title.contains("My Panel") }

	static content = {
		
	}
	
}