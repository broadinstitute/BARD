import bardqueryapi.QueryServiceWrapper
import grails.util.GrailsUtil

// Place your Spring DSL code here
beans = {

    switch (GrailsUtil.environment) {
        case "offline":
            queryService(bardqueryapi.MockQueryService) {
                queryHelperService = ref('queryHelperService')
            }
            break;
        default:
            final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
            queryServiceWrapper(QueryServiceWrapper, ncgcBaseURL) {

            }

            queryService(bardqueryapi.QueryService) {
                queryHelperService = ref('queryHelperService')
                queryServiceWrapper = ref('queryServiceWrapper')
            }
    }

}
