import bardqueryapi.QueryService
import grails.util.GrailsUtil
import mockServices.MockQueryService

/**
 * Spring Configuration of resources
 */
beans = {
    final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
    restAssayService(bard.core.rest.RESTAssayService, ncgcBaseURL) {}
    restProjectService(bard.core.rest.RESTProjectService, ncgcBaseURL) {}
    restExperimentService(bard.core.rest.RESTExperimentService, ncgcBaseURL) {
        restAssayService = ref('restAssayService')
    }
    restCompoundService(bard.core.rest.RESTCompoundService, ncgcBaseURL) {}
    restSubstanceService(bard.core.rest.RESTSubstanceService, ncgcBaseURL) {}

    combinedRestService(bard.core.rest.CombinedRestService) {
        restSubstanceService = ref('restSubstanceService')
        restCompoundService = ref('restCompoundService')
        restExperimentService = ref('restExperimentService')
        restProjectService = ref('restProjectService')
        restAssayService = ref('restAssayService')
    }
    switch (GrailsUtil.environment) {
        case "offline":
            queryService(MockQueryService) {
                queryHelperService = ref('queryHelperService')
            }
            break;
        default:
            queryService(QueryService) {
                queryHelperService = ref('queryHelperService')
                restCompoundService = ref('restCompoundService')
                restProjectService = ref('restProjectService')
                combinedRestService = ref('combinedRestService')
                restAssayService = ref('restAssayService')
            }
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService)
}
