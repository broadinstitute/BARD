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

package bard.taglib

import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.registration.Assay
import bard.db.registration.Panel
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(ModifiedByTagLib)
@Build([Assay, Project, Experiment, Panel])
@Unroll
class ModifiedByTagLibUnitSpec extends Specification {
                                                     //bardNews, Role, TeamRole
    void "test renderModifiedByEnsureNoEmail for #entityClass.simpleName #desc"() {

        def entity = entityClass.build()
        entity.modifiedBy = modifieBy
        expect:
        1 == 1
        applyTemplate('<g:renderModifiedByEnsureNoEmail entity="${entity}" />', [entity: entity]) == expectedModifiedBy

        where:
        desc               | entityClass | modifieBy     | expectedModifiedBy
        'null ok'          | Assay       | null          | ""
        'blank ok'         | Assay       | ""            | ""
        'blank ok'         | Assay       | " "           | " "
        'non-blank ok'     | Assay       | "foo"         | "foo"
        'portion before @' | Assay       | "foo@foo.com" | "foo"

        'null ok'          | Experiment  | null          | ""
        'blank ok'         | Experiment  | ""            | ""
        'blank ok'         | Experiment  | " "           | " "
        'non-blank ok'     | Experiment  | "foo"         | "foo"
        'portion before @' | Experiment  | "foo@foo.com" | "foo"

        'null ok'          | Panel       | null          | ""
        'blank ok'         | Panel       | ""            | ""
        'blank ok'         | Panel       | " "           | " "
        'non-blank ok'     | Panel       | "foo"         | "foo"
        'portion before @' | Panel       | "foo@foo.com" | "foo"

        'null ok'          | Project     | null          | ""
        'blank ok'         | Project     | ""            | ""
        'blank ok'         | Project     | " "           | " "
        'non-blank ok'     | Project     | "foo"         | "foo"
        'portion before @' | Project     | "foo@foo.com" | "foo"
    }
}
