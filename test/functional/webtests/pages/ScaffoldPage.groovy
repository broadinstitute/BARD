package webtests.pages

import geb.Page
import geb.navigator.Navigator

class ScaffoldPage extends Page {
	static content = {
		heading { $("h1") }
		message { $("i.icon-shopping-cart")}
	}
    def logout() {
        def logoutLink = $("a", text: "Logout")
        assert logoutLink
        def firstLink = logoutLink[0]
        firstLink.click();
        return true
    }


    boolean isLoggedIn() {
        return isLoggedInAsUser("")
    }

    boolean isLoggedInAsUser(String username) {
        checkFor($(id: "loginLink"), "Logged in as $username")
    }

    boolean checkFor(Navigator element, String condition) {
        waitForPageToLoad()
        if (element) {
            if (element.text()) {
                if (element.text().contains(condition)) {
                    return true
                }
            }
        }
        return false
    }
    void waitForPageToLoad() {

       // waitFor(5, 0.5) { $("p").text().contains("BARD is a free") }
    }

}