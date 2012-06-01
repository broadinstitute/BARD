import bardqueryapi.MockQueryAssayApiService
import grails.util.Environment

// Place your Spring DSL code here
beans = {
//    switch (Environment.current.name) {
//        case "qa":
//        case "production":

            clientBasicAuth(wslite.http.auth.HTTPBasicAuthorization) {
                username = "bogus"
                password = "bogus"
            }
            httpClient(wslite.http.HTTPClient) {
                sslTrustAllCerts = true
            }
            restClient(wslite.rest.RESTClient) {
                url = grailsApplication.config.ncgc.server.root.url
                httpClient = ref('httpClient')
                authorization = ref('clientBasicAuth')
            }
//            break
//        default:
//            queryAssayApiService(MockQueryAssayApiService) {
//
//            }
//
//    }

}
