package pages


import geb.Page
import geb.navigator.Navigator

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Date Updated: 13/10/07
 *
 */
class ScaffoldPage extends CommonFunctionalPage {
    static content = {
        heading { $("h1") }
    }

    def logout() {
        def logoutLink = $("#logoutButton")
        assert logoutLink
        def firstLink = logoutLink[0]
        firstLink.click();
        return true
    }

    boolean isLoggedIn() {
        return isLoggedInAsUser("")
    }

    boolean isLoggedInAsUser(String username) {
        def logoutFormText = waitFor { $(id: "logoutForm").text()}
        logoutFormText.contains( "Logged in as") &&  logoutFormText.contains(username)
    }

}