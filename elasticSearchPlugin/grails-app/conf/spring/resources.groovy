import elasticsearchplugin.ElasticSearchService

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
        url = 'http://localhost:9200'
        httpClient = ref('httpClient')
        authorization = ref('clientBasicAuth')
    }
    elasticSearchService(elasticsearchplugin.ElasticSearchService) {
        elasticSearchBaseUrl = 'http://localhost:9200'
        assayIndexName = 'assays'
        assayIndexTypeName = 'assay'
        compoundIndexName = 'compounds'
        compoundIndexTypeName = 'compound'
        queryExecutorService = ref('queryExecutorService')
    }
}
