/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

