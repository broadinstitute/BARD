package elasticsearchplugin

import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

class QueryExecutorService {

    static transactional = false

    //we use this as a template so that we can clone it for every request
    RESTClient restClient
    RestClientFactoryService restClientFactoryService
    def executeGetRequestJSON(final String url, final Map headers) throws RESTClientException {

        final RESTClient clonedRestClient = this.restClientFactoryService.createNewRestClient(url)

        Response response
        if (headers) {
            response = clonedRestClient.get(headers: headers)
        } else {
            response = clonedRestClient.get()
        }
        return response.json
    }

    def executeGetRequestString(final String url, final Map headers) throws RESTClientException {
        final RESTClient clonedRestClient = this.restClientFactoryService.createNewRestClient(url)
        Response response
        if (headers) {
            response = clonedRestClient.get(headers: headers)
        } else {
            response = clonedRestClient.get()
        }
        //check for no content and others
        return response.getContentAsString()
    }
    /**
     *
     * @param url
     * @param data
     * @return
     * @throws RESTClientException
     */
    def postRequest(final String url, final String data) throws RESTClientException {
        final RESTClient restClientClone = restClientFactoryService.createNewRestClient(url)
        def response = restClientClone.post() {
            text data
        }
        return response.json
    }
}
