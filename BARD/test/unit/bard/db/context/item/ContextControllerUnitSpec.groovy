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

package bard.db.context.item

import bard.db.ContextService
import bard.db.enums.ContextType
import bard.db.experiment.ExperimentContext
import bard.db.project.ProjectContext
import bard.db.registration.AssayContext
import bard.taglib.InPlaceEditTagLib
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.security.access.AccessDeniedException
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ContextController)
@Build([ProjectContext, AssayContext, ExperimentContext])
@Mock([ProjectContext, AssayContext, ExperimentContext])
@Unroll
class ContextControllerUnitSpec extends Specification {

    def setup() {
        controller.contextService = Mock(ContextService)
    }

    void 'test createCard Bad Request for #contextClass.simpleName'() {
        given:
        final String contextClassName = contextClass.simpleName

        when:
        controller.createCard(contextClassName, null, "My Card", ContextType.BIOLOGY.id)

        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "OwnerId is required"
        assert !model

        where:
        contextClass << [ProjectContext, AssayContext, ExperimentContext]
    }


    void 'test unAuthorized createCard for #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()
        final String contextClassName = contextClass.simpleName

        when:
        controller.createCard(contextClassName, context.owner.id, "My Card", ContextType.BIOLOGY.id)
        then:
        controller.contextService."create${contextClassName}"(_, _, _, _) >> {
            throw new AccessDeniedException("some message")
        }

        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

        where:
        contextClass << [ProjectContext, AssayContext, ExperimentContext]

    }


    void 'test successful createCard for #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()
        final Long ownerId = context.owner.id

        when:
        controller.createCard(contextClass.simpleName, ownerId, "My Card", ContextType.BIOLOGY.id)

        then:
        response.status == HttpServletResponse.SC_FOUND
        response.redirectedUrl == "/${urlRoot}/show/${ownerId}"

        where:
        contextClass      | urlRoot
        ProjectContext    | 'project'
        AssayContext      | 'assayDefinition'
        ExperimentContext | 'experiment'
    }


    void 'test unAuthorized deleteEmptyCard #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()

        when:
        controller.deleteEmptyCard(contextClass.simpleName, context.id, 'Unclassified')

        then:
        controller.contextService."delete${contextClass.simpleName}"(_, _, _) >> {
            throw new AccessDeniedException("some message")
        }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"
        assert !model

        where:
        contextClass << [ProjectContext, AssayContext, ExperimentContext]

    }

    void 'test successful deleteEmptyCard for #contextClass.simpleName'() {
        given:
        final def context = contextClass.build()

        when:
        controller.deleteEmptyCard(contextClass.simpleName, context.id, 'Unclassified')

        then:
        response.status == HttpServletResponse.SC_FOUND
        response.redirectUrl == "/${urlRoot}/show/${context.owner.id}"

        where:
        contextClass      | urlRoot
        ProjectContext    | 'project'
        AssayContext      | 'assayDefinition'
        ExperimentContext | 'experiment'
    }
}
