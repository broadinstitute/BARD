import acl.CapPermissionService
import grails.util.Environment

// Place your Spring DSL code here
beans = {
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService) {
        grailsApplication = application
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }

    switch (Environment.current) {
        case Environment.PRODUCTION:
            userDetailsService(org.broadinstitute.cbip.crowd.MultiProviderUserDetailsService) {
                crowdAuthenticationProviders = [ref('crowdAuthenticationProvider')]
            }
            break
        default:
            inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService) {
                grailsApplication = application
            }
            userDetailsService(org.broadinstitute.cbip.crowd.MultiProviderUserDetailsService) {
                crowdAuthenticationProviders = [ref('inMemMapAuthenticationProviderService'), ref('crowdAuthenticationProvider')]
            }

    }
    capPermissionService(CapPermissionService) {
        aclUtilService = ref("aclUtilService")
        springSecurityService = ref("springSecurityService")
    }
}
