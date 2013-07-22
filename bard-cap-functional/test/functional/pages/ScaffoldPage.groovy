package pages

import geb.Page
import geb.navigator.Navigator

class ScaffoldPage extends CommonFunctionalPage {
    static content = {
        heading { $("h1") }
    }

    def logout() {
        def logoutLink = $("button.btn.btn-primary", text:"Logout")
        assert logoutLink
        def firstLink = logoutLink[0]
        firstLink.click();
        return true
    }

    boolean isLoggedIn() {
        return isLoggedInAsUser("")
    }

    boolean isLoggedInAsUser(String username) {
        checkFor($(id: "logoutForm"), "Logged in as") && checkFor($(id: "logoutForm"), "$username")
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