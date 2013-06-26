package bard.db.registration

import bard.db.ContextService
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.project.ProjectContext
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ContextService)
@Build([Assay, Experiment, Project, AssayContext, ProjectContext, ExperimentContext])
@Mock([Assay, Experiment, Project, AssayContext,ProjectContext, ExperimentContext])
@Mixin(GrailsUnitTestMixin)
class ContextServiceUnitSpec extends Specification {

    def 'test createExperimentContext'() {
        setup:
        AbstractContextOwner owner = Experiment.build()

        when:
        AbstractContext context = service.createExperimentContext(owner, "name", "section");

        then:
        context != null
        owner.contexts.contains(context)
        context.contextGroup == "section"
        context.contextName == "name"
    }

    def 'test createAssayContext'() {
        setup:
        AbstractContextOwner owner = Assay.build()

        when:
        AbstractContext context = service.createAssayContext(owner, "name", "section");

        then:
        context != null
        owner.contexts.contains(context)
        context.contextGroup == "section"
        context.contextName == "name"
    }

    def 'test createProjectContext'() {
        setup:
        AbstractContextOwner owner = Project.build()

        when:
        AbstractContext context = service.createProjectContext(owner, "name", "section");

        then:
        context != null
        owner.contexts.contains(context)
        context.contextGroup == "section"
        context.contextName == "name"
    }
    def 'test deleteAssayContext'() {
        setup:
        AbstractContext context = AssayContext.build()
        AbstractContextOwner owner = context.owner

        when:
        service.deleteAssayContext(owner,context)

        then:
        !owner.contexts.contains(context)
    }
    def 'test deleteProjectContext'() {
        setup:
        AbstractContext context = ProjectContext.build()
        AbstractContextOwner owner = context.owner

        when:
        service.deleteProjectContext(owner,context)

        then:
        !owner.contexts.contains(context)
    }
    def 'test deleteExperimentContext'() {
        setup:
        AbstractContext context = ExperimentContext.build()
        AbstractContextOwner owner = context.owner

        when:
        service.deleteExperimentContext(owner,context)

        then:
        !owner.contexts.contains(context)
    }
}