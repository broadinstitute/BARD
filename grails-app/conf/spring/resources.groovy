import bardqueryapi.QueryServiceWrapper
import grails.util.GrailsUtil
import bardqueryapi.QueryService
import bardqueryapi.MockQueryService

// Place your Spring DSL code here
beans = {

    switch (GrailsUtil.environment) {
        case "offline":
            queryService(MockQueryService) {
                queryHelperService = ref('queryHelperService')
            }
            break;
        default:
            final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
            String promiscuityScoreURL = grailsApplication.config.promiscuityscrores.root.url

            queryServiceWrapper(QueryServiceWrapper, ncgcBaseURL, promiscuityScoreURL) {

            }

            queryService(QueryService) {
                queryHelperService = ref('queryHelperService')
                queryServiceWrapper = ref('queryServiceWrapper')
                promiscuityScoreService = ref('promiscuityScoreService')
            }
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService)


}
