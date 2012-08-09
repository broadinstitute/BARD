// Place your Spring DSL code here
beans = {
    clientBasicAuth(wslite.http.auth.HTTPBasicAuthorization) {
        username = "bogus"
        password = "bogus"
    }
    httpClient(wslite.http.HTTPClient) {
        sslTrustAllCerts = true
    }
    restClient(wslite.rest.RESTClient) {
        url = grailsApplication.config.ncgc.server.root.url
        httpClient = ref('httpClient')
        authorization = ref('clientBasicAuth')
    }
    //url to get accession numbers
    final String accessionUrl = grailsApplication.config.ncgc.server.accession.root.url
    //url to get geneid's
    final String geneIdUrl = grailsApplication.config.ncgc.server.gene.root.url

    queryTargetApiService(bardqueryapi.QueryTargetApiService, accessionUrl, geneIdUrl) {
        queryExecutorInternalService = ref('queryExecutorInternalService')
    }

    elasticSearchService(elasticsearchplugin.ElasticSearchService) {
        elasticSearchBaseUrl = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        assayIndexName = 'assays'
        assayIndexTypeName = 'assay'
        compoundIndexName = 'compounds'
        compoundIndexTypeName = 'compound'
        queryExecutorService = ref('queryExecutorService')
        xcompoundIndexName = 'compound'
        xcompoundIndexTypeName = 'xcompound'
        defaultElementsPerPage = 500
        eSsElasticSearchRequester = '/_search'
    }
    queryService(bardqueryapi.QueryService) {
        queryExecutorService = ref('queryExecutorService')
        elasticSearchService = ref('elasticSearchService')
        ncgcSearchBaseUrl = grailsApplication.config.ncgc.server.structureSearch.root.url
        elasticSearchRootURL = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        bardAssayViewUrl = grailsApplication.config.bard.assay.view.url
    }

}
