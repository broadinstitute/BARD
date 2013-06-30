package bard.core.rest.spring

import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.CapRestService
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Tests for ProjectRestService
 */
@Mixin(RESTTestHelper)
@Unroll
class CapRestServiceIntegrationSpec extends IntegrationSpec {
    CapRestService capRestService


    void "test getDictionaryElementPaths"() {
        when:
        final Map elementPaths = this.capRestService.getDictionaryElementPaths()

        then:
        elementPaths.assayType.contains('/BARD/assay protocol/assay type/')
        elementPaths.assayFormat.contains('/BARD/assay protocol/assay format/')
        elementPaths.biologicalProcess.contains('/BARD External Ontology/biological process/')
    }
}