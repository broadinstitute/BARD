package bard.core.rest.helper

import bard.core.SearchParams
import bard.core.helper.CapService
import bard.core.rest.spring.*
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.experiment.*
import grails.plugin.spock.IntegrationSpec
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Tests for ProjectRestService
 */
@Mixin(RESTTestHelper)
@Unroll
class CapServiceIntegrationSpec extends IntegrationSpec {
    CapService capService


    void "test getDictionaryElementPaths"() {
        when:
        final Map elementPaths = this.capService.getDictionaryElementPaths()

        then:
        elementPaths.assayType.contains('/BARD/assay protocol/assay type/')
        elementPaths.assayFormat.contains('/BARD/assay protocol/assay format/')
        elementPaths.biologicalProcess.contains('/BARD External Ontology/biological process/')
    }
}