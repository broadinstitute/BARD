package bardqueryapi

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import grails.plugins.springsecurity.SpringSecurityService
import grails.converters.JSON

class BardLogoutController {

    MobileService mobileService
    SpringSecurityService springSecurityService

    def index = {
        // TODO put any pre-logout code here
        redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
    }

    def afterLogout = {
        //If mobile page, return an AJAX logout message.
//        if (mobileService.detect(request) && springSecurityService.isAjax(request)) {
//            return 'Logged out' as JSON
//        }
        redirect uri: SpringSecurityUtils.securityConfig.auth.loginFormUrl
    }
}
