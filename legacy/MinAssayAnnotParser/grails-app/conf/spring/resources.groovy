// Place your Spring DSL code here
beans = {
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService){
        grailsApplication = application
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    userDetailsService(org.broadinstitute.cbip.crowd.MultiProviderUserDetailsService){
        crowdAuthenticationProviders = [ref('inMemMapAuthenticationProviderService'), ref('crowdAuthenticationProvider')]
    }
    externalOntologyFactory(bard.validation.ext.BardExternalOntologyFactory)
}
