import bardqueryapi.QueryServiceWrapper
import grails.util.GrailsUtil
import bardqueryapi.QueryService
import mockServices.MockQueryService
/**
 * Spring Configuration of resources
 */
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
            }
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService)


}
