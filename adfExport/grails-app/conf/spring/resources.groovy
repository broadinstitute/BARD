import acl.CapPermissionService
import bard.auth.BardUserDetailsService
import bard.auth.InMemMapAuthenticationProviderService


// Place your Spring DSL code here
beans = {



    inMemMapAuthenticationProviderService(InMemMapAuthenticationProviderService) {
        grailsApplication = application
    }

    userDetailsService(BardUserDetailsService) {
        inMemMapAuthenticationProviderService = ref('inMemMapAuthenticationProviderService')
    }

    capPermissionService(CapPermissionService) {
        aclUtilService = ref("aclUtilService")
        springSecurityService = ref("springSecurityService")
    }
}

