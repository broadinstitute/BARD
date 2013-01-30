import bard.hibernate.ModifiedByListener
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

    modifiedByListener(ModifiedByListener) {
        springSecurityService = ref('springSecurityService')
    }
    hibernateEventListeners(HibernateEventListeners) {
        listenerMap = ['pre-insert': modifiedByListener,
                'pre-update': modifiedByListener,
        ]
    }

    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService)

}
