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

package bardqueryapi

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import javax.servlet.http.HttpServletRequest

class MobileService {
    SpringSecurityService springSecurityService
    def grailsApplication

    Boolean detect(HttpServletRequest request) {
        return false

//        if (request.session.getAttribute('mobileExperienceDisabled')) {
//            return false
//        }
//
//        if (SpringSecurityUtils.ifAnyGranted('ROLE_MOBILE')) {
//            return true
//        }
//
//        def device = request.getAttribute('currentDevice')
//
//        Boolean isMobile = device.isMobile()
//
//        def userAgent = request.getHeader('User-Agent')
//        Boolean isTablet = false
//
//        if (userAgent?.contains('iPad')) { // skip iPads
//            isTablet = true
//        } else if (userAgent?.contains('Android') && !userAgent?.contains('Mobile')) {
//            // and android tablets
//            isTablet = true
//        }
//
//        return isMobile || isTablet
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
