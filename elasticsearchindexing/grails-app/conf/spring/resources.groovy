import bardelasticsearch.ElasticSearchAssayIndexService
import bardelasticsearch.ElasticSearchCompoundIndexService
import bardelasticsearch.ElasticSearchCompoundsIndexService
import bardelasticsearch.RestClientFactoryService

// Place your Spring DSL code here
beans = {
    final String elasticSearchUrl = grailsApplication.config.elasticsearch.server.url
    final String ncgcRootUrl = grailsApplication.config.ncgc.server.url.rest.api.root
    final String ncgcRelativeUrl = grailsApplication.config.ncgc.server.url.rest.api.relative

    //We do not need the user name and password here. We use it just to make the rest client happy
    //TODO: In future though, when we start supporting basic auth in ELasticSearch we might need to reconfigure
    //With real username and passwords from external configs
    clientBasicAuth(wslite.http.auth.HTTPBasicAuthorization) {
        username = "bogus"
        password = "bogus"
    }
    httpClient(wslite.http.HTTPClient) {
        sslTrustAllCerts = true
        authorization = ref('clientBasicAuth')
    }
    restClient(wslite.rest.RESTClient) {
        url = "${elasticSearchUrl}"
        httpClient = ref('httpClient')
        authorization = ref('clientBasicAuth')
    }
    restClientFactoryService(RestClientFactoryService) {
        restClient = ref('restClient')
    }
    elasticSearchCompoundsIndexService(ElasticSearchCompoundsIndexService) {
        elasticSearchURL = "${elasticSearchUrl}"
        numberOfThreads = 20
        ncgcRootURL = "${ncgcRootUrl}"
        ncgcRelativeURL = "${ncgcRelativeUrl}"
        restClientFactoryService = ref('restClientFactoryService')
        executorService = ref('executorService')
    }
    elasticSearchCompoundIndexService(ElasticSearchCompoundIndexService) {
        elasticSearchURL = "${elasticSearchUrl}"
        ncgcRootURL = "${ncgcRootUrl}"
        ncgcRelativeURL = "${ncgcRelativeUrl}"
        restClientFactoryService = ref('restClientFactoryService')
    }
    elasticSearchAssayIndexService(ElasticSearchAssayIndexService) {
        elasticSearchURL = "${elasticSearchUrl}"
        ncgcRootURL = "${ncgcRootUrl}"
        ncgcRelativeURL = "${ncgcRelativeUrl}"
        restClientFactoryService = ref('restClientFactoryService')
    }
}
