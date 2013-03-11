import org.springframework.web.client.RestTemplate

import java.util.concurrent.Executors

import bard.core.rest.spring.*
import bard.core.helper.LoggerService

/**
 * Spring Configuration of resources
 */
beans = {

    final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
    final String badApplePromiscuityUrl = grailsApplication.config.promiscuity.badapple.url



    restTemplate(RestTemplate)
    loggerService(LoggerService)

    compoundRestService(CompoundRestService) {
        baseUrl = ncgcBaseURL
        promiscuityUrl = badApplePromiscuityUrl
        restTemplate = ref('restTemplate')
        executorService = Executors.newCachedThreadPool()
        loggerService = ref('loggerService')
    }

    experimentRestService(ExperimentRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    projectRestService(ProjectRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    assayRestService(AssayRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    substanceRestService(SubstanceRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }

    dataExportRestService(DataExportRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    eTagRestService(ETagRestService){
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    targetRestService(TargetRestService){
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
}
