package pages

import grails.plugin.remotecontrol.RemoteControl
import geb.Module

class HomePage extends ScaffoldPage {

	static url = ""
	//static String url = getAppUrl()
	static at = {
		//title ==~ /BARD: Catalog of Assay Protocols/
		$("h3").text() ==~ "CAP - Catalog of Assay Protocols"
	}

	static content = {
		//Home page dropdown links
		//dropDownMenu { $("a")}
		logOut { $("form#logoutForm").find("button") }
		
		// Home Page links with images
		createNewAssay { $("a").find("img", title:"Create New Assay")}
		findExistingAssay { $("a").find("img", title:"Find Exiting Assay")}
		viewFavoriteAssay { $("a").find("img", title:"View Favorite Assay")}
		readAboutAssayTerminology { $("a").find("img", title:" Read about assay terminology")}
		
		capHeaders { module BardCapHeaderModule }
	}

	static String getAppUrl() {
		RemoteControl remotec = new RemoteControl()
		return remotec {ctx.grailsApplication.config.grails.serverURL}
	}
}

class BardCapHeaderModule extends Module {
	static content = {
		bardLogo { $("img", alt: "BioAssay Research Database") }
		navigationBar { $("ul.nav").find("li") }
		navigationChildTabs { navigationBar.find("ul.dropdown-menu")}
		
		capTab { navigationBar[0].find("a") }
		
		assayTab { navigationBar[1].find("a")[0] }
		assayChildTabs { navigationBar[1].find("ul.dropdown-menu").find("a") }
		
		projectTab { navigationBar[4].find("a")[0] }
		projectChildTabs { navigationBar[4].find("ul.dropdown-menu").find("a") }
		
		webclientTab { navigationBar[7].find("a") }
	}
}