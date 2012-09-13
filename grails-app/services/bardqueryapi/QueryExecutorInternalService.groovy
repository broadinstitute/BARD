package bardqueryapi

import grails.converters.JSON
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response
import org.apache.commons.lang3.time.StopWatch

class QueryExecutorInternalService {

    HTTPBasicAuthorization clientBasicAuth
    //we use this as a template so that we can clone it for every request
    RESTClient restClient
    /**
     * Executes a GET request to the specified url, with additional (optional) rest-client params.
     * Here is an example to the usage of the additional params:
     * def client = new RESTClient("http://api.twitter.com/1/")
     * def response = client.get( path:'/users/show.json',
     *  accept: ContentType.JSON,
     *  query:[screen_name:'jwagenleitner', include_entities:true],
     *  headers:["X-Foo":"bar"],
     *  connectTimeout: 5000,
     *  readTimeout: 10000,
     *  followRedirects: false,
     *  useCaches: false,
     *  sslTrustAllCerts: true
     * )
     *
     * @param url
     * @param restClientParams - optional rest-client parameters (e.g., [accept: ContentType.JSON, headers:["X-Foo":"bar"], connectTimeout: 5000, readTimeout: 10000])
     * If no optional params are needed, leave this parameter empty in the calling method (rather than passing in a null).
     * @return
     * @throws RESTClientException
     */
    def executeGetJSON(final String url, Map restClientParams = [:]) throws RESTClientException {

        final RESTClient clonedRestClient = new RESTClient(url, restClient.httpClient)
        clonedRestClient.setAuthorization(clientBasicAuth)

        StopWatch sw = new StopWatch()
        Date now = new Date()
        sw.start()
        Response response = clonedRestClient.get(restClientParams)
        sw.stop()
        Map loggingMap = [url: url, requestMethod: response?.request?.method, time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time]
        log.info(loggingMap.toString())

        return response.json
    }
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
