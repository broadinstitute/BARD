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
        "prevBardExpt":
        {
            "bardExptId": 14084,
            "capExptId": 3360,
            "bardAssayId": 8959,
            "capAssayId": 3365,
            "pubchemAid": 1503,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 8,
            "compounds": 8,
            "activeCompounds": 2,
            "confidenceLevel": 1,
            "name": "Minimal Inhibitory Concentration assay in E. Coli for small molecule DnaK Modulators targeting the b-domain.",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-03-01",
            "hasProbe": false,
            "projectIdList": [
                1944
            ],
            "resourcePath": "/experiments/14084"
        },
        "nextBardExpt":
        {
            "bardExptId": 14103,
            "capExptId": 3807,
            "bardAssayId": 8978,
            "capAssayId": 3812,
            "pubchemAid": 1505,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 2,
            "compounds": 2,
            "activeCompounds": 1,
            "confidenceLevel": 1,
            "name": "Minimal Inhibitory Concentration assay in Y. pseudotuberculosis for small molecule DnaK Modulators targeting the beta-domain",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-03-01",
            "hasProbe": false,
            "projectIdList": [
                1944
            ],
            "resourcePath": "/experiments/14103"
        },
        "bardProjId": 1944,
        "prevStageRef": "primary assay",
        "nextStageRef": "confirmatory assay",
        "stepId": 5148,
        "edgeName": "Linked by Compound set (Swamidass)",
        "annotations": [
            {
                "entityId": 5148,
                "entity": "project-step",
                "source": "cap-context",
                "id": 5398,
                "display": "2",
                "contextRef": "Compound Overlap",
                "key": "1242",
                "value": null,
                "extValueId": null,
                "url": null,
                "displayOrder": 0,
                "related": "1944"
            }
        ],
        "resourcePath": ""
    }
    '''
    public static final String PROJECT_STEPS = '''
    [
        {
            "prevBardExpt":
            {
                "bardExptId": 14084,
                "capExptId": 3360,
                "bardAssayId": 8959,
                "capAssayId": 3365,
                "pubchemAid": 1503,
                "category": -1,
                "type": -1,
                "summary": 0,
                "assays": 0,
                "classification": -1,
                "substances": 8,
                "compounds": 8,
                "activeCompounds": 2,
                "confidenceLevel": 1,
                "name": "Minimal Inhibitory Concentration assay in E. Coli for small molecule DnaK Modulators targeting the b-domain.",
                "description": null,
                "source": null,
                "grantNo": null,
                "deposited": null,
                "updated": "2013-03-01",
                "hasProbe": false,
                "projectIdList": [
                    1944
                ],
                "resourcePath": "/experiments/14084"
            },
            "nextBardExpt":
            {
                "bardExptId": 14103,
                "capExptId": 3807,
                "bardAssayId": 8978,
                "capAssayId": 3812,
                "pubchemAid": 1505,
                "category": -1,
                "type": -1,
                "summary": 0,
                "assays": 0,
                "classification": -1,
                "substances": 2,
                "compounds": 2,
                "activeCompounds": 1,
                "confidenceLevel": 1,
                "name": "Minimal Inhibitory Concentration assay in Y. pseudotuberculosis for small molecule DnaK Modulators targeting the beta-domain",
                "description": null,
                "source": null,
                "grantNo": null,
                "deposited": null,
                "updated": "2013-03-01",
                "hasProbe": false,
                "projectIdList": [
                    1944
                ],
                "resourcePath": "/experiments/14103"
            },
            "bardProjId": 1944,
            "prevStageRef": "primary assay",
            "nextStageRef": "confirmatory assay",
            "stepId": 5148,
            "edgeName": "Linked by Compound set (Swamidass)",
            "annotations": [
                {
                    "entityId": 5148,
                    "entity": "project-step",
                    "source": "cap-context",
                    "id": 5398,
                    "display": "2",
                    "contextRef": "Compound Overlap",
                    "key": "1242",
                    "value": null,
                    "extValueId": null,
                    "url": null,
                    "displayOrder": 0,
                    "related": "1944"
                }
            ],
            "resourcePath": ""
        },
        {
            "prevBardExpt": {
                "bardExptId": 14076,
                "capExptId": 3351,
                "bardAssayId": 8951,
                "capAssayId": 3356,
                "pubchemAid": 1494,
                "category": -1,
                "type": -1,
                "summary": 0,
                "assays": 0,
                "classification": -1,
                "substances": 11,
                "compounds": 11,
                "activeCompounds": 2,
                "confidenceLevel": 1,
                "name": "ATPase - based assay for small molecule DnaK Modulators targeting the beta-domain",
                "description": null,
                "source": null,
                "grantNo": null,
                "deposited": null,
                "updated": "2013-03-01",
                "hasProbe": false,
                "projectIdList": [
                    1944
                ],
                "resourcePath": "/experiments/14076"
            },
            "nextBardExpt": {
                "bardExptId": 14084,
                "capExptId": 3360,
                "bardAssayId": 8959,
                "capAssayId": 3365,
                "pubchemAid": 1503,
                "category": -1,
                "type": -1,
                "summary": 0,
                "assays": 0,
                "classification": -1,
                "substances": 8,
                "compounds": 8,
                "activeCompounds": 2,
                "confidenceLevel": 1,
                "name": "Minimal Inhibitory Concentration assay in E. Coli for small molecule DnaK Modulators targeting the b-domain.",
                "description": null,
                "source": null,
                "grantNo": null,
                "deposited": null,
                "updated": "2013-03-01",
                "hasProbe": false,
                "projectIdList": [
                    1944
                ],
                "resourcePath": "/experiments/14084"
            },
            "bardProjId": 1944,
            "prevStageRef": "primary assay",
            "nextStageRef": "confirmatory assay",
            "stepId": 5150,
            "edgeName": "Linked by Compound set (Swamidass)",
            "annotations": [
                {
                    "entityId": 5150,
                    "entity": "project-step",
                    "source": "cap-context",
                    "id": 5400,
                    "display": "8",
                    "contextRef": "Compound Overlap",
                    "key": "1242",
                    "value": null,
                    "extValueId": null,
                    "url": null,
                    "displayOrder": 0,
                    "related": "1944"
                }
            ],
            "resourcePath": ""
         }
    ]
    '''


    void "test serialization to ProjectStep"() {
        when:
        final ProjectStep projectStep = objectMapper.readValue(PROJECT_STEP, ProjectStep.class)
        then:
        ProjectExperiment nextBardExpt = projectStep.nextBardExpt
        ProjectExperiment prevBardExpt = projectStep.prevBardExpt

        assert nextBardExpt
        assert prevBardExpt
        assert !nextBardExpt.description
        assert !nextBardExpt.getDeposited()
        assert "2013-03-01" == nextBardExpt.getUpdated()

        assert 1944 == projectStep.bardProjId
        assert 5148 == projectStep.stepId
        assert "Linked by Compound set (Swamidass)" == projectStep.edgeName
        assert 1 == projectStep.annotations.size()
        assert !projectStep.getResourcePath()
        assert "primary assay" == projectStep.getPrevStageRef()
        assert "confirmatory assay" == projectStep.getNextStageRef()
    }

    void "test serialization to ProjectSteps"() {
        when:
        final List<ProjectStep> projectSteps = objectMapper.readValue(PROJECT_STEPS, List.class)
        then:
        assert projectSteps
        assert 2 == projectSteps.size()
    }
}

