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

package base

import geb.spock.GebReportingSpec

import org.openqa.selenium.Dimension

import pages.HomePage
import pages.LoginPage
import spock.lang.Shared

import common.ConfigHelper
/**
 * Created by GGTS.
 * User: mrafique
 * Date Created: 13/02/06
 */
abstract class BardFunctionalSpec extends GebReportingSpec {
	@Shared protected Map<String, Map> usernameUserPropsMap = [:]
	def setupSpec() {
		def mockUsers = ConfigHelper.config.mockUsers
		mockUsers.each {user ->
			Map userProps = user.value
			usernameUserPropsMap.put(userProps.username, userProps)
		}
		driver.manage().window().setSize(new Dimension(1200, 1000))// maximize was creating tall skiny viewport on ghostdriver so specifically setting
	}

	HomePage logInWithRole(String role) {
		Map.Entry<String, Map> userInfoMap = usernameUserPropsMap.find { k, v ->
			v.roles.contains(role)
		}
		return logInAsUser(userInfoMap.key)
	}

	HomePage logInAsUser(String username) {
		to LoginPage
		return logIn(username, usernameUserPropsMap.get(username).password)
	}

	HomePage logInSomeUser() {
		String username = getCredentialsForTest().username
		return logInAsUser(username)
	}

	Map<String, String> getCredentialsForTest() {
		return usernameUserPropsMap.find{it}.value    // returns the first entry
	}
}
