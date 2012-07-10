package elasticsearchplugin

import grails.converters.JSON
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

class QueryExecutorService {

    HTTPBasicAuthorization clientBasicAuth
    //we use this as a template so that we can clone it for every request
    RESTClient restClient

    def executeGetRequestJSON(final String url, final Map headers) throws RESTClientException {
        final RESTClient cloneRestClient = new RESTClient(url, restClient.httpClient)
        cloneRestClient.setAuthorization(clientBasicAuth)
        Response response
        if (headers) {
            response = cloneRestClient.get(headers: headers)
        } else {
            response = cloneRestClient.get()
        }
        return response.json
    }

    def executeGetRequestString(final String url, final Map headers) throws RESTClientException {
        final RESTClient cloneRestClient = new RESTClient(url, restClient.httpClient)
        cloneRestClient.setAuthorization(clientBasicAuth)
        Response response
        if (headers) {
            response = cloneRestClient.get(headers: headers)
        } else {
            response = cloneRestClient.get()
        }
        //check for no content and others
        return response.getContentAsString()
    }
}
