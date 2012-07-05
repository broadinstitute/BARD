package bardelasticsearch

//import XML
//import JSONObject


import grails.converters.JSON
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

class ElasticSearchIndexService {
    String elasticSearchURL //Url to the assay type
    String ncgcRootURL
    String ncgcRelativeURL
    RESTClient restClient




    def logMessage(String message, String type){
       if(type == 'error') {
           log.error(message)
       }
        else{
          log.info(message)
       }
    }

    /**
     * Execute a get query
     * @param url
     * @return
     */
    def executeGetRequestJSON(final String url) {

        this.restClient.url = url
        try {

            Response response = this.restClient.get()
            return response.json
        } catch (RESTClientException ex) {
            String message = ex.response.statusMessage
            final int code = ex.response.statusCode
            return [errorMessage: message, errorCode: code] as JSON
        }
    }

}
