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

package bard.db.registration

import bard.db.experiment.Experiment
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.validation.ValidationException
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 */


@TestFor(MergeAssayDefinitionController)
@Build([Assay, Experiment])
@Mock([Assay, Experiment])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MergeAssayDefinitionControllerUnitSpec extends Specification {
    @Shared
    Assay assay1
    @Shared
    Assay assay2

    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        controller.mergeAssayDefinitionService = Mock(MergeAssayDefinitionService)
        Experiment experiment1 = Experiment.build()
        Experiment experiment2 = Experiment.build()
        assay1 = Assay.build(assayName: 'Test1', experiments: [experiment1])
        assay2 = Assay.build(assayName: 'Test2', experiments: [experiment2])
    }

    void 'test index'() {
        when:
        controller.index()
        then:
        assert response.status == HttpServletResponse.SC_FOUND
        assert response.redirectedUrl == "/mergeAssayDefinition/show"
    }

    void 'test show'() {
        when:
        controller.show()
        then:
        assert response.status == HttpServletResponse.SC_OK
    }

   /* void 'test confirmMerge exceptions - #desc'() {
        given:
        ConfirmMergeAssayCommand confirmMergeAssayCommand =
            new ConfirmMergeAssayCommand(idType: idType, targetAssayId: targetAssayId, sourceAssayIds: sourceAssayId.toString())
        when:
        controller.confirmMerge(confirmMergeAssayCommand)
        then:
        assert response.status == expectedHttpResponse
        where:
        desc                             | expectedHttpResponse               | targetAssayId | sourceAssayId | idType
        "No Assay Id Type"               | HttpServletResponse.SC_BAD_REQUEST | assay1.id     | assay2.id     | null
        "No Assay To Merge"              | HttpServletResponse.SC_BAD_REQUEST | assay1.id     | null          | IdType.AID
        "No Assay To Merge Into"         | HttpServletResponse.SC_BAD_REQUEST | null          | assay2.id     | IdType.ADID
        "Assay Type AID does not exist"  | HttpServletResponse.SC_BAD_REQUEST | assay1.id     | assay2.id     | IdType.AID
        "Assay Type ADID does not exist" | HttpServletResponse.SC_BAD_REQUEST | assay1.id     | assay2.id     | IdType.ADID

    }

    void 'test confirmMerge success - '() {
        given:
        ConfirmMergeAssayCommand confirmMergeAssayCommand =
            new ConfirmMergeAssayCommand(idType: IdType.ADID, targetAssayId: assay1.id, sourceAssayIds: assay2.id.toString())
        when:
        controller.confirmMerge(confirmMergeAssayCommand)
        then:
        controller.mergeAssayDefinitionService.convertStringToIdList(_) >> { [assay2.id] }
        controller.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { assay1 }
        controller.mergeAssayDefinitionService.convertAssaysToMerge(_, _, _) >> { [assay2.id] }
        assert response.status == HttpServletResponse.SC_OK
    }

    void "test mergeAssays exceptions -validation exception"() {
        given:
        MergeAssayCommand mergeAssayCommand = new MergeAssayCommand(targetAssayId: assay1.id, sourceAssayIds: [assay2.id])
        when:
        controller.mergeAssays(mergeAssayCommand)
        then:
        controller.mergeAssayDefinitionService.mergeAllAssays(_, _) >> { new ValidationException("msg") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void "test mergeAssays exceptions - runtime exception"() {
        given:
        MergeAssayCommand mergeAssayCommand = new MergeAssayCommand(targetAssayId: assay1.id, sourceAssayIds: [assay2.id])
        when:
        controller.mergeAssays(mergeAssayCommand)
        then:
        controller.mergeAssayDefinitionService.mergeAllAssays(_, _) >> { new RuntimeException("msg") }
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void "test mergeAssays OK"() {
        given:
        MergeAssayCommand mergeAssayCommand = new MergeAssayCommand(targetAssayId: assay1.id, sourceAssayIds: [assay2.id])
        when:
        controller.mergeAssays(mergeAssayCommand)
        then:
        controller.mergeAssayDefinitionService.mergeAllAssays(_, _) >> { return assay1 }
        assert response.status == HttpServletResponse.SC_OK
    } */
}
