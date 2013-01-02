package bardqueryapi

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import javax.servlet.http.HttpServletRequest

class MobileService {

    SpringSecurityService springSecurityService
    def grailsApplication

    Boolean detect(HttpServletRequest request) {

        if (request.session.getAttribute('mobileExperienceDisabled')) {
            return false
        }

        if (SpringSecurityUtils.ifAnyGranted('ROLE_MOBILE')) {
            return true
        }

        def device = request.getAttribute('currentDevice')

        Boolean detected = device.isMobile()

        if (detected) {

            def userAgent = request.getHeader('User-Agent')

            if (userAgent?.contains('iPad')) { // skip iPads
                detected = false
            } else if (userAgent?.contains('Android') && !userAgent?.contains('Mobile')) {
                // and android tablets
                detected = false
            }

        }

        return detected
    }


    Boolean gspExists(String gspName) {
        return getAvailableGSPs().find { String gsp -> ('/' + gsp - '.gsp') == gspName}
    }


    private List<String> getAvailableGSPs() {
        List<String> gsps = []
        if (grailsApplication.isWarDeployed()) {
            findWarGsps('/WEB-INF/grails-app/views', gsps)
        }
        else {
            findDevGsps('grails-app/views', gsps)
        }

        return gsps
    }

    private void findDevGsps(String current, List<String> gsps) {
        for (File file in new File(current).listFiles()) {
            if (file.path.endsWith('.gsp')) {
                gsps << (file.path.replaceAll('\\\\', '/') - 'grails-app/views/')
            }
            else {
                findDevGsps(file.path, gsps)
            }
        }
    }

    private void findWarGsps(String current, List gsps) {
        def servletContext = ServletContextHolder.servletContext
        for (String path in servletContext.getResourcePaths(current)) {
            if (path.endsWith('.gsp')) {
                gsps << (path - '/WEB-INF/grails-app/views/')
            }
            else {
                findWarGsps(path, gsps)
            }
        }
    }
}
