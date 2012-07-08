package bardelasticsearch

import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

abstract class ElasticSearchIndexAbstractService {
    RESTClient restClient
    String elasticSearchURL //root url to elastic search server
    String ncgcRootURL  //root url to NCGC server
    String ncgcRelativeURL

    /**
     * We clone the rest client because the urls are different and this is a multi threaded application
     * @param yourURL
     * @return @link RESTClient
     */
    RESTClient cloneRestClient(final String yourURL) {
        RESTClient restClientClone = new RESTClient(yourURL)
        restClientClone.httpClient = this.restClient.httpClient
        restClientClone.httpClient.authorization = this.restClient.httpClient.authorization
        return restClientClone;
    }

    /**
     * Execute a get query
     * @param url
     * @return
     */
    def executeGetRequestJSON(final RESTClient restClientClone) {
        try {

            Response response = restClientClone.get()
            return response.json
        } catch (RESTClientException ex) {
            String message = ex.response.statusMessage
            final int code = ex.response.statusCode
            log.error([errorMessage: message, errorCode: code].toString())
            throw ex
        }
    }
    /**
     * @param restClientClone the rest client to execute request
     * @param requestToSend the request to send to server
     */
    public void putRequest(final RESTClient restClientClone, final String requestToSend) {
        final Response response = restClientClone.put() {
            text requestToSend
        }
        log.info(response.statusCode + " " + response.statusMessage)

    }
    /**
     * @param restClientClone the rest client to execute request
     * @param requestToSend the request to send to server
     */
    def postRequest(final RESTClient restClientClone, final String requestToPost) {

        def response = restClientClone.post() {
            text requestToPost
        }
        return response.json
    }
}
