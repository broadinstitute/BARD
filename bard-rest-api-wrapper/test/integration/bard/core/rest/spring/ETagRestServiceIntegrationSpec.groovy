package bard.core.rest.spring

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.rest.spring.etags.ETags

@Unroll
class ETagRestServiceIntegrationSpec extends IntegrationSpec {
    ETagRestService eTagRestService
    AssayRestService assayRestService
    ProjectRestService projectRestService
    CompoundRestService compoundRestService

    void "test Make Composite ETags #label"() {
        given: "Create an Assay ETag"
        final String assayETag = assayRestService.newETag("My Assay collection", adids);
        and: "Create a Project ETag"
        final String projectETag = projectRestService.newETag("My Project Collection", pids)
        and: "Create a Compound ETag"
        final String compoundETag = compoundRestService.newETag("My Compound Collection", cids)
        when: "We call the composite Etags"
        final String compositeETag = this.eTagRestService.newCompositeETag("My Composite ETags", [assayETag, projectETag, compoundETag])

        then:
        assert compositeETag

        where:
        label             | adids              | pids               | cids
        "Composite ETags" | [5155, 5158, 5157] | [1581, 1563, 1748] | [3235555, 3235556]
    }
    void "test Get Composite ETags #label"() {
        given: "Create an Assay ETag"
        final String assayETag = assayRestService.newETag("My Assay collection", adids);
        and: "Create a Project ETag"
        final String projectETag = projectRestService.newETag("My Project Collection", pids)
        and: "Create a Compound ETag"
        final String compoundETag = compoundRestService.newETag("My Compound Collection", cids)
        and: "Create a composite ETag"
        final String compositeETag = this.eTagRestService.newCompositeETag("My Composite ETags", [assayETag, projectETag, compoundETag])
        when: "We call to get the composite Etags"
        final ETags eTags = this.eTagRestService.getComponentETags(compositeETag)

        then:
        assert eTags
        assert eTags.getByType("assays")
        assert eTags.getByType("compounds")
        assert eTags.getByType("projects")

        where:
        label             | adids              | pids               | cids
        "Composite ETags" | [5155, 5158, 5157] | [1581, 1563, 1748] | [3235555, 3235556]
    }

}

