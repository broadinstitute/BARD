package elasticsearchplugin

import wslite.rest.RESTClient

class RestClientFactoryService {
    static transactional = false

    RESTClient restClient

    RESTClient createNewRestClient(final String yourURL) {
        RESTClient restClientClone = new RESTClient(yourURL)
        restClientClone.httpClient = this.restClient.httpClient
        restClientClone.httpClient.authorization = this.restClient.httpClient.authorization
        return restClientClone;

    }
}
