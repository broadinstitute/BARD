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

    externalUrlDTO(ExternalUrlDTO){
        baseUrl = ncgcBaseURL
        promiscuityUrl = badApplePromiscuityUrl
    }

    restTemplate(RestTemplate)
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

    dataExportRestService(DataExportRestService) {
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
}

