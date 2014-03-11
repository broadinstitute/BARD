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