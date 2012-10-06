package util

import groovyx.net.http.RESTClient


class RestClientFactoryService {

    /**
     *  Create a RESTClient from the given base URL
     *  Connection time out and socket time out both defaults to 10K milliseconds
     * @param baseURL
     * @param connectionTimeOut
     * @param socketTimeOut
     * @return RESTClient
     */
    RESTClient createRestClient(final String baseURL, final int connectionTimeOut = 10000, final int socketTimeOut = 10000) {
        RESTClient restClient = new RESTClient(baseURL)
        restClient.client.params.setIntParameter("http.connection.timeout", connectionTimeOut)
        restClient.client.params.setIntParameter("http.socket.timeout", socketTimeOut)
        return restClient
    }
}
