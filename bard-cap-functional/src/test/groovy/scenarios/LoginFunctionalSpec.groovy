package scenarios

import pages.HomePage
import pages.LoginPage
import base.BardFunctionalSpec

/**
 * This class includes all the possible test functions for login and logout.
 * @author Muhammad.Rafique
 * Date Created: 13/02/11
 */

class LoginFunctionalSpec extends BardFunctionalSpec {
    String invalidUserName = "baduser"
    String invalidPassword = "badpassword"
    String validUserName = getCredentialsForTest().username
    String validPassword = getCredentialsForTest().password

    def setup() {
        to LoginPage
    }

    def "Test login with invalid username"() {
        given: "User visits the Login page"
        to LoginPage

        when: "User attempts to login with an invalid username"
        at LoginPage
        logInNoValidation(invalidUserName, validPassword)

        then: "The system should redirect the user to the login page"
        at LoginPage
        errorMessage.text() ==~ 'Sorry, we were not able to find a user with that username and password.'

        !loginForm.j_username
        !loginForm.j_password

        report "LoginwithInvalidUsername"
    }

    def "Test login with invalid password"() {
        given: "User visits the Login page"
        to LoginPage

        when: "User attempts to login with an invalid password"
        at LoginPage
        logInNoValidation(validUserName, invalidPassword)

        then: "The system should redirect the user to the login page"
        at LoginPage
        errorMessage.text() ==~ 'Sorry, we were not able to find a user with that username and password.'
        !loginForm.j_username
        !loginForm.j_password

        report "LoginwithInvalidPassword"
    }

    def "Test login with valid credentials"() {
        given: "User visits the Login page"
        to LoginPage

        when: "User attempts to login with an invalid username"
        at LoginPage
        logInNoValidation(validUserName, validPassword)

        then: "The system should display a message stating that the user is logged in"
        at HomePage
        assert isLoggedInAsUser(validUserName)

        report "LoginwithValidCredentials"
    }

    def "Test logout"() {
        given: "User is logged in to the system"
        to LoginPage
        logInNoValidation(validUserName, validPassword)
        assert isLoggedInAsUser(validUserName)

        when: "User clicks the 'Log Out' link"
        at HomePage
        logout()

        then: "The user should be logged out of the system"
        at LoginPage

        report "Logout"
    }
}
