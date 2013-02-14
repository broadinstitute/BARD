package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectStepUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()


    public static final String PROJECT_STEP = '''
    {
        "prevBardExpt": {
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
        },
        "nextBardExpt": {
            "bardExptId": 12661,
            "capExptId": 2610,
            "bardAssayId": 7535,
            "capAssayId": 2615,
            "pubchemAid": 2565,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 564,
            "compounds": 561,
            "name": "Counterscreen for APE1 Inhibitors: qHTS Assay for Inhibitors of Endonuclease IV",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-01-26",
            "hasProbe": false,
            "projectIdList": [
                1703
            ],
            "resourcePath": "/experiments/12661"
        },
        "bardProjId": 1703,
        "stepId": 3803,
        "edgeName": "Linked by Compound set (Swamidass)",
        "annotations": [
            {
                "entityId": 3803,
                "entity": "project-step",
                "source": "cap-context",
                "id": 4056,
                "display": "561",
                "contextRef": "Compound Overlap",
                "key": "1242",
                "value": null,
                "extValueId": null,
                "url": null,
                "displayOrder": 0,
                "related": "1703"
            }
        ],
        "resourcePath": ""
    }
    '''



    void "test serialization to ProjectStep"() {
        when:
        final ProjectStep projectStep = objectMapper.readValue(PROJECT_STEP, ProjectStep.class)
        then:
        BardExpt nextBardExpt = projectStep.nextBardExpt
        BardExpt prevBardExpt = projectStep.prevBardExpt

        assert nextBardExpt
        assert prevBardExpt
        assert 1703 == projectStep.bardProjId
        assert 3803 == projectStep.stepId
        assert "Linked by Compound set (Swamidass)" == projectStep.edgeName
        assert 1 == projectStep.annotations.size()
    }

}

