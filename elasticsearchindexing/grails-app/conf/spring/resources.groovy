import bardelasticsearch.ElasticSearchIndexService

// Place your Spring DSL code here
beans = {
    final String elasticSearchURL = grailsApplication.config.elasticsearch.server.url
    final String ncgcRootUrl = grailsApplication.config.ncgc.server.url.rest.api.root
    final String ncgcRelativeUrl= grailsApplication.config.ncgc.server.url.rest.api.relative

    //We do not need the user name and password here. We use it just to make the rest client happy
    //TODO: In future though, when we start supporting basic auth in ELasticSearch we might need to reconfigure
    clientBasicAuth(wslite.http.auth.HTTPBasicAuthorization) {
        username = "bogus"
        password = "bogus"
    }
    httpClient(wslite.http.HTTPClient) {
        sslTrustAllCerts = true
    }
    restClient(wslite.rest.RESTClient) {
        url = "${elasticSearchURL}"
        httpClient = ref('httpClient')
        authorization = ref('clientBasicAuth')
    }
    elasticSearchIndexService(ElasticSearchIndexService) {
        elasticSearchURL = "${elasticSearchURL}"
        ncgcRootURL = "${ncgcRootUrl}"
        ncgcRelativeURL="${ncgcRelativeUrl}"
        restClient = ref('restClient')
    }
}
