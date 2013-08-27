package bardqueryapi

import grails.plugins.springsecurity.SpringSecurityService

class BardUtilitiesService {

    SpringSecurityService springSecurityService
    String getUsername() {
        def principal = springSecurityService.principal
        return principal?.hasProperty("username") ? principal.username : null
    }
}
