import acl.CapPermissionService
import bard.auth.InMemMapAuthenticationProviderService
import grails.util.Environment

// Place your Spring DSL code here
beans = {
    inMemMapAuthenticationProviderService(InMemMapAuthenticationProviderService) {
        grailsApplication = application
    }

    capPermissionService(CapPermissionService) {
        aclUtilService = ref("aclUtilService")
        springSecurityService = ref("springSecurityService")
    }
}
