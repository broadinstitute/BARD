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

    def executeGetRequestJSON(final String url, final Map headers) {
        final RESTClient cloneRestClient = new RESTClient(url, restClient.httpClient)
        cloneRestClient.setAuthorization(clientBasicAuth)
        try {
            Response response
            if (headers) {
                response = cloneRestClient.get(headers: headers)
            } else {
                response = cloneRestClient.get()
            }
            return response.json
        } catch (RESTClientException ex) {
            String message = ex.response.statusMessage
            final int code = ex.response.statusCode
            return [errorMessage: message, errorCode: code] as JSON
        }
    }

    def executeGetRequestString(final String url, final Map headers) {
        final RESTClient cloneRestClient = new RESTClient(url, restClient.httpClient)
        cloneRestClient.setAuthorization(clientBasicAuth)
        try {
            Response response
            if (headers) {
                response = cloneRestClient.get(headers: headers)
            } else {
                response = cloneRestClient.get()
            }
            //check for no content and others
            return response.getContentAsString()
        }
        catch (RESTClientException ex) {
            String message = ex.response.statusMessage
            final int code = ex.response.statusCode
            return [message: code] as JSON
        }

    }
}
