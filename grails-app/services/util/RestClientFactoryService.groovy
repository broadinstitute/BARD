package util

import groovyx.net.http.RESTClient


class RestClientFactoryService {


    RESTClient createRestClient(final String baseURL) {
        RESTClient restClient = new RESTClient(baseURL)
        int timeout = 10000
        restClient.client.params.setIntParameter("http.connection.timeout", timeout)
        restClient.client.params.setIntParameter("http.socket.timeout", timeout)
        return restClient
    }
}
