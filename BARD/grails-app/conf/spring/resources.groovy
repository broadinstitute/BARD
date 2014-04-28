/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import acl.CapPermissionService
import acl.SpringSecurityUiService
import bard.PreservingExceptionGrailsExceptionResolver
import bard.auth.BardUserDetailsService
import bard.auth.InMemMapAuthenticationProviderService
import bard.auth.BardUserDetailsService
import bard.core.helper.LoggerService
import bard.core.rest.spring.*
import bard.core.util.ExternalUrlDTO
import bard.db.ReadyForExtractFlushListener
import bard.db.util.BardEditorRegistrar
import bard.hibernate.ModifiedByListener
import bard.validation.extext.BardExternalOntologyFactory
import bardqueryapi.ETagsService
import bardqueryapi.QueryService
import bardqueryapi.experiment.ExperimentBuilder
import grails.util.Environment
import mockServices.MockQueryService
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import persona.OnlinePersonaVerifyer
import persona.PersonaAuthenticationFilter
import persona.PersonaAuthenticationProvider

// Place your Spring DSL code here
beans = {

    externalOntologyFactory(bard.validation.extext.BardExternalOntologyFactory){bean ->
        bean.autowire = "byName"
    }
    customPropertyEditorRegistrar(BardEditorRegistrar)
    springSecurityUiService(SpringSecurityUiService) {
        messageSource = ref('messageSource')
        springSecurityService = ref('springSecurityService')
        grailsApplication = grailsApplication

    }
    /**
     * setting timeouts for connections established by the restTemplate
     *
     * just using the SimpleClientHttpRequestFactory there is also CommonsClientHttpRequestFactory which would offer
     * more configuration options if we need it
     */
    simpleClientHttpRequestFactory(SimpleClientHttpRequestFactory) {
        connectTimeout = 5 * 1000 // in milliseconds
        readTimeout = 40 * 1000 // in milliseconds
    }

    restTemplate(RestTemplate, ref('simpleClientHttpRequestFactory'))

    //Wiring for Persona
    personaAuthenticationFilter(PersonaAuthenticationFilter) {
        filterProcessesUrl = grailsApplication.config.Persona.filterProcessesUrl
        authenticationSuccessHandler = ref('authenticationSuccessHandler')
        authenticationFailureHandler = ref('authenticationFailureHandler')
        authenticationManager = ref('authenticationManager')
    }

    onlinePersonaVerifyer(OnlinePersonaVerifyer) {
        restTemplate = ref('restTemplate')
        audience = grailsApplication.config.grails.serverURL
        verificationUrl = grailsApplication.config.Persona.verificationUrl
    }

    personaAuthenticationProvider(PersonaAuthenticationProvider) {
        onlinePersonaVerifyer = ref('onlinePersonaVerifyer')
    }

    modifiedByListener(ModifiedByListener) {
        springSecurityService = ref('springSecurityService')
    }
    readyForExtractFlushListener(ReadyForExtractFlushListener) {
    }
    hibernateEventListeners(HibernateEventListeners) {
        listenerMap = [
                'flush': readyForExtractFlushListener,
                'post-insert': readyForExtractFlushListener,
                'post-update': readyForExtractFlushListener,
                'pre-insert': modifiedByListener,
                'pre-update': modifiedByListener,
        ]
    }

    inMemMapAuthenticationProviderService(InMemMapAuthenticationProviderService) {
        grailsApplication = application
    }

    userDetailsService(BardUserDetailsService) {
        inMemMapAuthenticationProviderService = ref('inMemMapAuthenticationProviderService')
    }

    capPermissionService(CapPermissionService) {
        aclUtilService = ref("aclUtilService")
        springSecurityService = ref("springSecurityService")
    }

    // from web-client
    String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
    String badApplePromiscuityUrl = grailsApplication.config.promiscuity.badapple.url
    String bardCapUrl = grailsApplication.config.bard.cap.home

    externalUrlDTO(ExternalUrlDTO) {
        ncgcUrl = ncgcBaseURL
        promiscuityUrl = badApplePromiscuityUrl
        capUrl = bardCapUrl
    }

    loggerService(LoggerService)


    compoundRestService(CompoundRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }

    experimentBuilder(ExperimentBuilder) {
        grailsApplication = grailsApplication
        compoundRestService=ref('compoundRestService')
        ontologyDataAccessService=ref('ontologyDataAccessService')
    }
    experimentRestService(ExperimentRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    projectRestService(ProjectRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    assayRestService(AssayRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    substanceRestService(SubstanceRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }

    dictionaryRestService(DictionaryRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    eTagRestService(ETagRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    targetRestService(TargetRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }

    exceptionHandler(PreservingExceptionGrailsExceptionResolver){
        exceptionMappings = ['java.lang.Exception': '/error']
    }

    switch (Environment.current) {
        case "offline":
            queryService(MockQueryService) {
                queryHelperService = ref('queryHelperService')
            }
            break;
        default:
            queryService(QueryService) {
                projectService=ref('projectService')
                queryHelperService = ref('queryHelperService')
                compoundRestService = ref('compoundRestService')
                projectRestService = ref('projectRestService')
                assayRestService = ref('assayRestService')
                substanceRestService = ref('substanceRestService')
                experimentRestService = ref('experimentRestService')
                experimentBuilder=ref('experimentBuilder')
            }
            eTagsService(ETagsService) {
                compoundRestService = ref('compoundRestService')
                projectRestService = ref('projectRestService')
                assayRestService = ref('assayRestService')
                eTagRestService = ref('eTagRestService')

            }
    }
}
