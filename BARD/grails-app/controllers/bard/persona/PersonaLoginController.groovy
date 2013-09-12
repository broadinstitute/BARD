package bard.persona

import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.Authentication
import persona.PersonaAuthenticationProvider

class PersonaLoginController {
    PersonaAuthenticationProvider personaAuthenticationProvider
    SpringSecurityService springSecurityService
    def index() {
        if (params.assertion) {
            println "assertion"
            final Authentication authentication = personaAuthenticationProvider.personaAuthentication(params.assertion)
            //redirect controller: "assayDefinition", action: "findById"
        }

        if (springSecurityService.isLoggedIn()) {
            redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
        }
        else {
            redirect action: 'auth', params: params
        }

    }
}
