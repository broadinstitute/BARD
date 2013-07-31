package pages

import geb.Page
import geb.navigator.Navigator

class ScaffoldPage extends Page {
    static content = {
        heading { $("h1") }
    }

    def logout() {
        def logoutLink = $("button.btn", text:"Logout")
        assert logoutLink
        def firstLink = logoutLink[0]
        firstLink.click();
        return true
    }

    boolean isLoggedIn() {
        return isLoggedInAsUser("")
    }

    boolean isLoggedInAsUser(String username) {
        if(checkFor($("form#loginForm"), "Not logged in")) { return false }
        return (checkFor($("form#logoutForm"), "Logged in as") && checkFor($("form#logoutForm"), "$username"))
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
        waitFor(5, 0.5) { title.contains("BARD") }
    }

}