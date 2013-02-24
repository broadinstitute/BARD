import geb.spock.GebReportingSpec
import grails.plugin.remotecontrol.RemoteControl
import spock.lang.Shared
import pages.HomePage
import pages.LoginPage

import java.lang.reflect.Method

/**
 * Created by IntelliJ IDEA.
 * User: jlev
 * Date: 10/21/11
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class BardFunctionalSpec extends GebReportingSpec {
    @Shared protected Map<String, Map> usernameUserPropsMap = [:]

    Serializable build(Class clazz, properties=[:]) {
        XRemoteControl remote = new XRemoteControl()
        def newId = remote {
            Method method = null;

            Class z = ctx.scaffoldService.class
            while(z!= null) {
                if (method == null) {
                    method = z.getMethods().find {it.getName() == "testMethod"}
                }
                println("class ${z.getName()} ${method}")
                println("methods ${z.methods}")
                z = z.getSuperclass()
            }

            println("methods=${method}")
            method.invoke(ctx.scaffoldService)

            ctx.scaffoldService.testMethod();

            String name = clazz.getName();
            Map p = new HashMap()
            def obj = ctx.scaffoldService.build(name);
            if (obj.id == null) {
                throw new RuntimeException("Object had no id")
            }
            return obj.id
        }

        return newId;
    }

    void setupSpec() {
        XRemoteControl remote = new XRemoteControl()
        //  String baseUrl = remote { ctx.grailsApplication.config.grails.serverURL }

        def mockUsers = remote { ctx.grailsApplication.config.CbipCrowd.mockUsers }
        //Map userProps =[:]
        mockUsers.each {user ->
            Map userProps = user.value
            usernameUserPropsMap.put(userProps.username, userProps)
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

    Map<String, String> getCredentialsForTest() {
        return usernameUserPropsMap.find{it}.value    // returns the first entry
    }
}
