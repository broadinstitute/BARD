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