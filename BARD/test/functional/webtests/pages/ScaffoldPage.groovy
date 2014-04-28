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

import geb.Page
import geb.navigator.Navigator

class ScaffoldPage extends Page {
	static content = {
		heading { $("h1") }
		message { $("i.icon-shopping-cart")}
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
        checkFor($(id: "logoutForm"), "Logged in as")
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
      waitFor(10, 0.5) { title.contains("BioAssay Research Database") }
    }

}
