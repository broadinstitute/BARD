package pages

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/11
 * Date Updated: 13/10/07
 */
class LoginPage extends ScaffoldPage {
	static url = "bardLogin/auth"
	static at = { $("h2.form-signin-heading").text().equalsIgnoreCase("Please sign in") }

	static content = {
		loginForm { $("form#loginForm") }
		errorMessage(wait:true) { $("div.login_message") }
		signIn { $("button", type:"submit") }
	}

	HomePage logIn(String username, String password) {
        logInNoValidation(username,password)

		assert isLoggedInAsUser(username), "Not logged in as $username"

		return new HomePage()
	}

	def logInNoValidation(String username, String password) {
		loginForm.j_username = username
		loginForm.j_password = password
		signIn.click()
	}

}
