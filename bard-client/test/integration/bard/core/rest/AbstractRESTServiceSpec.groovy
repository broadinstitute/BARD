package bard.core.rest

import grails.plugin.spock.IntegrationSpec

/**
 * Abstract class to hold configuration
 */
abstract class AbstractRESTServiceSpec extends IntegrationSpec {
    RESTExperimentService restExperimentService
    RESTAssayService restAssayService
    RESTProjectService restProjectService
    RESTCompoundService restCompoundService
    RESTSubstanceService restSubstanceService
    CombinedRestService combinedRestService
}