import bard.hibernate.ModifiedByListener
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners
import bard.validation.ext.ExternalOntologyPerson
import bard.validation.ext.ExternalOntologyFactory

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
                'pre-update': modifiedByListener
        ]
    }
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService){
        grailsApplication = application
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    userDetailsService(org.broadinstitute.cbip.crowd.MultiProviderUserDetailsService){
        crowdAuthenticationProviders = [ref('inMemMapAuthenticationProviderService'), ref('crowdAuthenticationProvider')]
    }

    def extOntologyFactory = externalOntologyFactory(bard.validation.ext.RegisteringExternalOntologyFactory){ bean ->
                bean.factoryMethod = "getInstance"
    }

}
