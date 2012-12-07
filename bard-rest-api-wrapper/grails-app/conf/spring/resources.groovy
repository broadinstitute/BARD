import org.springframework.web.client.RestTemplate
import bard.core.rest.spring.*

/**
 * Spring Configuration of resources
 */
beans = {

    final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
    final String badApplePromiscuityUrl = grailsApplication.config.promiscuity.badapple.url
    restTemplate(RestTemplate) {
    }
    compoundRestService(CompoundRestService) {
        baseUrl = ncgcBaseURL
        promiscuityUrl = badApplePromiscuityUrl
        restTemplate = ref('restTemplate')
    }
    substanceRestService(SubstanceRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
    }
    experimentRestService(ExperimentRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
    }
    projectRestService(ProjectRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
    }
    assayRestService(AssayRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
    }
}
