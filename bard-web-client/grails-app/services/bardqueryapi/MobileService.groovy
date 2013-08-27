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

        Boolean isMobile = device.isMobile()

        def userAgent = request.getHeader('User-Agent')
        Boolean isTablet = false

        if (userAgent?.contains('iPad')) { // skip iPads
            isTablet = true
        } else if (userAgent?.contains('Android') && !userAgent?.contains('Mobile')) {
            // and android tablets
            isTablet = true
        }

        return isMobile || isTablet
    }


    Boolean gspExists(String gspName) {
        List<String> gsps = getAvailableGSPs()
        return gsps.find { String gsp -> ('/' + gsp - '.gsp') == gspName} ?: false
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
        final paths = servletContext.getResourcePaths(current)
        for (String path in paths) {
            if (path.endsWith('.gsp')) {
                gsps << (path - '/WEB-INF/grails-app/views/')
            }
            else {
                findWarGsps(path, gsps)
            }
        }
    }
}