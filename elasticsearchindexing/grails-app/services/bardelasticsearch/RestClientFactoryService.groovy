package bardelasticsearch

import wslite.rest.RESTClient

class RestClientFactoryService {
    RESTClient restClient

    RESTClient createNewRestClient(final String yourURL) {
        RESTClient restClientClone = new RESTClient(yourURL)
        restClientClone.httpClient = this.restClient.httpClient
        restClientClone.httpClient.authorization = this.restClient.httpClient.authorization
        return restClientClone;

    }
}
