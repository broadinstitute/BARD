import bard.login.TemporaryAuthenticationProvider
import bard.login.TemporaryUserDetailsService
import hibernate.ModifiedByListener
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners

// Place your Spring DSL code here
beans = {
    clientBasicAuth(wslite.http.auth.HTTPBasicAuthorization) {
        username = "bogus"
        password = "bogus"
    }
    httpClient(wslite.http.HTTPClient) {
        sslTrustAllCerts = true
    }
    restClient(wslite.rest.RESTClient) {
        url = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        httpClient = ref('httpClient')
        authorization = ref('clientBasicAuth')
    }
    elasticSearchService(elasticsearchplugin.ElasticSearchService) {
        elasticSearchBaseUrl = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        assayIndexName = 'assays'
        assayIndexTypeName = 'assay'
        compoundIndexName = 'compounds'
        compoundIndexTypeName = 'compound'
        queryExecutorService = ref('queryExecutorService')
    }

    temporaryUserDetailsService(TemporaryUserDetailsService)

    temporaryAuthenticationProvider(TemporaryAuthenticationProvider) {
        userDetailsService = ref('temporaryUserDetailsService')
    }

    modifiedByListener(ModifiedByListener) {
        springSecurityService = ref('springSecurityService')
    }
    hibernateEventListeners(HibernateEventListeners) {
        listenerMap = ['pre-insert': modifiedByListener,
                'pre-update': modifiedByListener,
        ]
    }


}
