package bardqueryapi

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

import java.awt.image.BufferedImage

@Unroll
class ETagsServiceIntegrationSpec extends IntegrationSpec {

    ETagsService eTagsService

    void "test createCompositeETags #label"() {
        given:

        when:
        String compositeETag = eTagsService.createCompositeETags(cids, pids, adids)
        then:
        assert compositeETag
        where:
        label             | adids              | pids            | cids
        "Composite ETags" | [5155, 5158, 5157] | [129, 102, 100] | [3235555, 3235556]
    }
     void "test createETag #label"() {
        when:
        String compositeETag = eTagsService.createETag(entityType, ids, [])
        then:
        assert compositeETag
        where:
        label           | ids                | entityType
        "Assay etag"    | [5155, 5158, 5157] | EntityType.ASSAY
        "Project etag"  | [129, 102, 100]    | EntityType.PROJECT
        "Compound eTag" | [3235555, 3235556] | EntityType.COMPOUND
    }

    void "test createCompositeETag #label"() {
        given:
        String compoundETag = eTagsService.createETag(EntityType.COMPOUND, cids, [])
        String assayETag = eTagsService.createETag(EntityType.ASSAY, adids, [])
        String projectETag = eTagsService.createETag(EntityType.PROJECT, pids, [])
        when:
        String compositeETag = eTagsService.createETag(EntityType.COMPOSITE, [], [compoundETag, assayETag, projectETag])
        then:
        assert compositeETag
        where:
        label             | adids              | pids            | cids
        "Composite ETags" | [5155, 5158, 5157] | [129, 102, 100] | [3235555, 3235556]


    }
}
