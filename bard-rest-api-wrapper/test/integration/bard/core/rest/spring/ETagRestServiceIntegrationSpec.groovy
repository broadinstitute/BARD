package bard.core.rest.spring

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

@Unroll
class ETagRestServiceIntegrationSpec extends IntegrationSpec {
    ETagRestService eTagRestService
    AssayRestService assayRestService
    ProjectRestService projectRestService
    CompoundRestService compoundRestService

    void "test Get Composite ETags #label"() {
        given: "Create an Assay ETag"
        final String assayETag = assayRestService.newETag("My Assay collection", adids);
        and: "Create a Project ETag"
        final String projectETag = projectRestService.newETag("My Project Collection", pids)
        and: "Create a Compound ETag"
        final String compoundETag = compoundRestService.newETag("My Compound Collection", cids)
        when: "We call the composite Etags"
        final String compositeETag = this.eTagRestService.newCompositeETag("My Composite ETags", [assayETag, projectETag, compoundETag])

        then:
        println(compositeETag)
        assert compositeETag
        //TODO Make a call to get the composite

        where:
        label             | adids              | pids            | cids
        "Composite ETags" | [5155, 5158, 5157] | [129, 102, 100] | [3235555, 3235556]
    }


}

