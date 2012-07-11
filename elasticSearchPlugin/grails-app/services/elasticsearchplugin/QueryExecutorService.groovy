package elasticsearchplugin

import grails.converters.JSON
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

class QueryExecutorService {

    //we use this as a template so that we can clone it for every request
    RESTClient restClient

    def executeGetRequestJSON(final String url, final Map headers) throws RESTClientException {

        final RESTClient clonedRestClient = cloneRestClient(url)

        Response response
        if (headers) {
            response = clonedRestClient.get(headers: headers)
        } else {
            response = clonedRestClient.get()
        }
        return response.json
    }

    def executeGetRequestString(final String url, final Map headers) throws RESTClientException {
        final RESTClient clonedRestClient = cloneRestClient(url)
        Response response
        if (headers) {
            response = clonedRestClient.get(headers: headers)
        } else {
            response = clonedRestClient.get()
        }
        //check for no content and others
        return response.getContentAsString()
    }

    private RESTClient cloneRestClient(String url) {
        final RESTClient clonedRestClient = new RESTClient(url, restClient.httpClient)
        clonedRestClient.setAuthorization(restClient.httpClient.authorization)
        return clonedRestClient
    }
}
