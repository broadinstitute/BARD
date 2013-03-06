import java.util.Map;

import geb.spock.GebReportingSpec
import grails.plugin.remotecontrol.RemoteControl
import spock.lang.Shared
import pages.HomePage
import pages.LoginPage
import pages.ViewAssayDefinitionPage
import pages.FindAssayByIdPage
import pages.EditAssayContextPage
import pages.FindProjectByIdPage
import pages.ViewProjectDefinitionPage
import pages.EditProjectPage
/**
 * Created by GGTS.
 * User: mrafique
 * Date: 13/02/06
 */
abstract class BardFunctionalSpec extends GebReportingSpec {
	@Shared protected Map testData =[:]
	@Shared protected Map<String, Map> usernameUserPropsMap = [:]
	void setupSpec() {
		RemoteControl remote = new RemoteControl()
		// String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }

		// def mockUsers = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers }
		def mockUsers = remote { ctx.grailsApplication.config.mockUsers }
		//Map userProps =[:]
		mockUsers.each {user ->
			Map userProps = user.value
			usernameUserPropsMap.put(userProps.username, userProps)
		}

		// load and read the property file for test data
		Properties props = new Properties()
		File propsFile = new File('test/resources/bard-cap-v1-config.properties')
		props.load(propsFile.newDataInputStream())
		props.propertyNames().each { prop ->
			testData[prop]=props.getProperty(prop)
		}

		driver.manage().window().maximize()
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

	FindAssayByIdPage searchAssayById(){
		at HomePage
		capHeaders.assayTab.click()
		capHeaders.assayChildTabs[0].click()
		return new FindAssayByIdPage()
	}

	ViewAssayDefinitionPage searchAsssay(assayId){
		at FindAssayByIdPage
		assaySearchBtns.inputBtns << assayId
		assaySearchBtns.searchBtn.click()
		return new ViewAssayDefinitionPage()
	}

	EditAssayContextPage editAssayContext(){
		at ViewAssayDefinitionPage
		editAssayCotextBtn[0].click()
		return new EditAssayContextPage()
	}

	EditAssayMeasurePage editMeasureNavigate(){
		at ViewAssayDefinitionPage
		assert editAssayMeasureBtn
		editAssayMeasureBtn.click()
		return new EditAssayMeasurePage()
	}
	
	FindProjectByIdPage navigateToSearchProjectById(){
		at HomePage
		capHeaders.projectTab.click()
		capHeaders.projectChildTabs[0].click()
		return new FindProjectByIdPage()
	}
	
	ViewProjectDefinitionPage searchProject(pId){
		at FindProjectByIdPage
		projectSearchBtns.inputBtns << pId
		projectSearchBtns.searchBtn.click()
		return new ViewProjectDefinitionPage()
	}
	
	HomePage logoutFromApp(){
		logOut.click()
		return new HomePage()
	}

}