import bardqueryapi.QueryServiceWrapper

// Place your Spring DSL code here
beans = {
    final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url

    queryServiceWrapper(QueryServiceWrapper, ncgcBaseURL) {

    }

    queryService(bardqueryapi.QueryService) {
        queryHelperService = ref('queryHelperService')
        queryServiceWrapper = ref('queryServiceWrapper')
    }
}
