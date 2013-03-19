package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectExperimentUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()


    public static final String BARD_EXPT = '''
    {
        "bardExptId": 12666,
        "capExptId": 2617,
        "bardAssayId": 7540,
        "capAssayId": 2622,
        "pubchemAid": 2572,
        "category": -1,
        "type": -1,
        "summary": 0,
        "assays": 0,
        "classification": -1,
        "substances": 748,
        "compounds": 745,
        "name": "Confirmation qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
        "description": null,
        "source": null,
        "grantNo": null,
        "deposited": null,
        "updated": "2013-01-26",
        "hasProbe": false,
        "projectIdList": [
            1703
        ],
        "resourcePath": "/experiments/12666"
    } '''



    void "test serialization to BardExpt"() {
        when:
        final ProjectExperiment bardExpt = objectMapper.readValue(BARD_EXPT, ProjectExperiment.class)
        then:
        assert 12666 ==bardExpt.bardExptId
        assert 2617== bardExpt.capExptId
        assert 7540 == bardExpt.bardAssayId
        assert 2622==bardExpt.capAssayId
        assert 2572==bardExpt.pubchemAid
        assert -1==bardExpt.category
        assert -1== bardExpt.type
        assert 0== bardExpt.summary
        assert 0==bardExpt.assays
        assert -1==bardExpt.classification
        assert 748==bardExpt.substances
        assert 745==bardExpt.compounds
        assert "Confirmation qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)"==bardExpt.name
        assert !bardExpt.source
        assert !bardExpt.grantNo
        assert !bardExpt.isHasProbe()
        assert [1703] == bardExpt.projectIdList
        assert !bardExpt.precedingProjectSteps
        assert !bardExpt.followingProjectSteps
        assert  "/experiments/12666"==bardExpt.resourcePath
     }

}

