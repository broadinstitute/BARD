/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package webtests.pages

/**
 * Created by IntelliJ IDEA.
 * User: jlev
 * Date: 10/17/11
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
class LoginPage extends ScaffoldPage {
    static url = ""

    static at = { title == "BioAssay Research Database" }

    static content = {
        loginForm { $("form") }
        errorMessage { $("div.login_message") }
        signIn { $("button", type: "submit") }
    }

    HomePage logIn(String username, String password) {
        if (!isLoggedInAsUser(username)) {
            if(isLoggedIn()) {                  // logged in as someone else
                logout()
                waitFor(5, 0.5){$(id: "username")}
            }
            logInNoValidation(username, password)
        }

        assert isLoggedInAsUser(username), "Not logged in as $username"

        return new HomePage()
    }

    def logInNoValidation(String username, String password) {
            loginForm.j_username = username
            loginForm.j_password = password
            signIn.click()
    }
}
