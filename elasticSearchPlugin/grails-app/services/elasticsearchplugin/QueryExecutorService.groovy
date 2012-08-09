package elasticsearchplugin

import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response
import org.apache.commons.lang3.time.StopWatch

class QueryExecutorService {

    static transactional = false

    //We use this as a template so that we can clone it for every request
    RESTClient restClient
    RestClientFactoryService restClientFactoryService

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
    def executeGetRequestJSON(final String url, Map restClientParams = [:]) throws RESTClientException {

        final RESTClient clonedRestClient = this.restClientFactoryService.createNewRestClient(url)

        StopWatch sw = new StopWatch()
        Date now = new Date()
        sw.start()
        Response response = clonedRestClient.get(restClientParams)
        sw.stop()
        Map loggingMap = [url: url, requestMethod: response?.request?.method, time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time]
        log.info(loggingMap.toString())

        return response.json
    }

    def executeGetRequestString(final String url, Map restClientParams = [:]) throws RESTClientException {

        final RESTClient clonedRestClient = this.restClientFactoryService.createNewRestClient(url)

        StopWatch sw = new StopWatch()
        Date now = new Date()
        sw.start()
        Response response = clonedRestClient.get(restClientParams)
        sw.stop()
        Map loggingMap = [url: url, requestMethod: response?.request?.method, time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time]
        log.info(loggingMap.toString())

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

        StopWatch sw = new StopWatch()
        Date now = new Date()
        sw.start()
        def response = restClientClone.post() {
            text data
        }
        sw.stop()
        Map loggingMap = [url: url, requestMethod: response?.request?.method, time: now.format('MM/dd/yyyy  HH:mm:ss.S'), responseTimeInMilliSeconds: sw.time]
        log.info(loggingMap.toString())

        return response.json
    }
}
