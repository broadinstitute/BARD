import acl.CapPermissionService
import bard.auth.InMemMapAuthenticationProviderService
import grails.util.Environment
import bard.auth.BardUserDetailsService

// Place your Spring DSL code here
beans = {
    userDetailsService(BardUserDetailsService) {
        inMemMapAuthenticationProviderService = ref('inMemMapAuthenticationProviderService')
    }

    inMemMapAuthenticationProviderService(InMemMapAuthenticationProviderService) {
        grailsApplication = application
    }

    capPermissionService(CapPermissionService) {
        aclUtilService = ref("aclUtilService")
        springSecurityService = ref("springSecurityService")
    }
}
