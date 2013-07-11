package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.util.TargetClassification
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.rest.spring.compounds.TargetClassInfo

@Unroll
class TargetRestServiceIntegrationSpec extends IntegrationSpec {
    TargetRestService targetRestService
    /**
     * http://bard.nih.gov/api/v15/targets/accession/P20393
     */
    void "getTargetByAccessionNumber acc - P20393"() {
        given:
        String accessionNumber = "P20393"
        when:
        final Target target = targetRestService.getTargetByAccessionNumber(accessionNumber)
        then:
        assert target
        assert "P20393" == target.acc
        assert "Nuclear receptor subfamily 1 group D member 1" == target.name
        assert "Reviewed" == target.status
        assert "http://www.uniprot.org/uniprot/P20393" == target.url
        assert 9572 == target.geneId
        assert 9606 == target.taxId
        assert "/targets/accession/P20393" == target.resourcePath
        assert 4 <= target.targetClassifications.size()
        for (TargetClassification targetClassification : target.getTargetClassifications()) {
            assert targetClassification
        }
    }
    void "test construct Target information"(){
        given:
        String accessionNumber = "P20393"
        final Target target = targetRestService.getTargetByAccessionNumber(accessionNumber)
        when:
        List<TargetClassInfo> list= Target.constructTargetInformation(target)
        then:
        assert list

    }
    /**
     *  Example http://bard.nih.gov/api/v15/targets/accession/P20393/classification/panther
     */
    void "getClassificationsFromSourceWithTarget acc - P20393"() {
        given:
        final String source = "panther"
        final String targetAccessionNumber = "P20393"
        when:
        final List<TargetClassification> targetClassifications = targetRestService.getClassificationsFromSourceWithTarget(source, targetAccessionNumber)
        then:
        assert targetClassifications
        assert 4 <= targetClassifications.size()
        for (TargetClassification targetClassification : targetClassifications) {
            assert targetClassification
        }
    }
    /**
     * http://bard.nih.gov/api/v15/targets/classification/panther/PC00169
     */
    void "getTargetsFromClassificationId  - PC00169"() {
        given:
        final String source = "panther"
        final String targetClassificationId = "PC00169"
        when:
        final List<Target> targets = targetRestService.getTargetsFromClassificationId(source, targetClassificationId)
        then:
        assert targets
        assert 30 <= targets.size()
        for (Target target : targets) {
            assert target
        }
    }

    void "getResourceContext"() {

        when:
        final String resourceContext = targetRestService.getResourceContext()
        then:
        assert RestApiConstants.TARGETS_RESOURCE == resourceContext
    }

    void "getResource"() {
        when:
        final String resourceContext = targetRestService.getResource()
        then:
        assert "http://bard.nih.gov/api/v17.1/targets/" == resourceContext
    }

    void "getSearchResource"() {
        when:
        final String resourceContext = targetRestService.getSearchResource()
        then:
        assert !resourceContext
    }

}

