package pages

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/11
 * Date Updated: 13/10/07
 */
class LoginPage extends ScaffoldPage {
	static url = "bardLogin/auth"
	//	static at = { $("h2.form-signin-heading").text().equalsIgnoreCase("Please sign in") }
	static at = { title.contains("BioAssay Research Database")}

	static content = {
		loginForm { $("form#loginForm") }
		errorMessage(wait:true) { $("div.login_message") }
		signIn { $("button", type:"submit") }
	}

	HomePage logIn(String username, String password, String email) {
		logInNoValidation(username,password, email)
		assert isLoggedInAsUser(username), "Not logged in as $username"

		return new HomePage()
	}

	/*def logInNoValidation(String username, String password) {
	 loginForm.j_username = username
	 loginForm.j_password = password
	 signIn.click()
	 }*/

	def logInNoValidation(String username, String password, String email) {
		//		$("a#signin").click()

		browser.withNewWindow(close:false, { $("a#signin").click() }){
			waitFor{ $("#rp_name").displayed }
			if($("#rptospp").displayed){
				waitFor { $("input#authentication_email").displayed }
				$("input#authentication_email").value(email)
				$("button.isDesktop.isStart.isAddressInfo").click()
				waitFor { $("input#authentication_password").displayed }
				$("input#authentication_password").value(password)
				$("button.isReturning.isTransitionToSecondary").click()
				waitFor{ !$("#rptospp").displayed }
				waitFor { !$("button.isReturning.isTransitionToSecondary").displayed }
			}else {
				$("button#signInButton").click()
				waitFor { !$("button#signInButton").displayed }
			}
		}
	}

}
