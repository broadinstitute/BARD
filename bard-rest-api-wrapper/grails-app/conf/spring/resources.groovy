import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

import java.util.concurrent.Executors

import bard.core.rest.spring.*
import bard.core.helper.LoggerService
import bard.core.util.ExternalUrlDTO

/**
 * Spring Configuration of resources
 */
beans = {

    String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
    String badApplePromiscuityUrl = grailsApplication.config.promiscuity.badapple.url
    String bardCapUrl = grailsApplication.config.bard.cap.home

    externalUrlDTO(ExternalUrlDTO){
        ncgcUrl = ncgcBaseURL
        promiscuityUrl = badApplePromiscuityUrl
        capUrl = bardCapUrl
    }
    /**
     * setting timeouts for connections established by the restTemplate
     *
     * just using the SimpleClientHttpRequestFactory there is also CommonsClientHttpRequestFactory which would offer
     * more configuration options if we need it
     */
    simpleClientHttpRequestFactory(SimpleClientHttpRequestFactory){
        connectTimeout =  5 * 1000 // in milliseconds
        readTimeout    = 25 * 1000 // in milliseconds
    }
    restTemplate(RestTemplate, ref('simpleClientHttpRequestFactory'))
    loggerService(LoggerService)

    compoundRestService(CompoundRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
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
    eTagRestService(ETagRestService){
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    targetRestService(TargetRestService){
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    sunburstCacheService(SunburstCacheService){
        targetRestService = ref('targetRestService')
    }
    sunburstRestService(SunburstRestService){
        sunburstCacheService = ref('sunburstCacheService')
    }
    capRestService(CapRestService) {
        externalUrlDTO = ref('externalUrlDTO')
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
        grailsApplication = grailsApplication
    }
}

