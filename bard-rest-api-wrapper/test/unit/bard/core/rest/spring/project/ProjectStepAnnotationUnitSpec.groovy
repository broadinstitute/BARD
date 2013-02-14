package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectStepAnnotationUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()


    public static final String PROJECT_STEP_ANNOTATION = '''
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
    '''



    void "test serialization to ProjectStepAnnotation"() {
        when:
        final ProjectStepAnnotation projectStepAnnotation = objectMapper.readValue(PROJECT_STEP_ANNOTATION, ProjectStepAnnotation.class)
        then:
        assert 3803 ==projectStepAnnotation.entityId
        assert "project-step"==projectStepAnnotation.entity
        assert "cap-context" == projectStepAnnotation.source
        assert 4056==projectStepAnnotation.id
        assert "561"==projectStepAnnotation.display
        assert "Compound Overlap"==projectStepAnnotation.contextRef
        assert "1242"== projectStepAnnotation.key
        assert !projectStepAnnotation.value
        assert !projectStepAnnotation.extValueId
        assert !projectStepAnnotation.url
        assert 0==projectStepAnnotation.displayOrder
        assert "1703"==projectStepAnnotation.related
     }

}

