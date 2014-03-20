package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.project.Project
import org.junit.*
import grails.test.mixin.*
import spock.lang.Shared
import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll
import grails.test.mixin.TestMixin
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.buildtestdata.mixin.Build
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.junit.Before

import javax.servlet.http.HttpServletResponse


@Build([ExternalReference, Experiment, Project, ExternalSystem])
@Mock(ExternalReference)
@TestFor(ExternalReferenceController)
@TestMixin(DomainClassUnitTestMixin)
@Unroll
class ExternalReferenceControllerUnitSpec extends Specification {

    @Shared
    Experiment experiment
    @Shared
    Project project
    @Shared
    ExternalSystem externalSystem

    @Before
    void setup() {
        this.experiment = Experiment.build()
        this.project = Project.build()
        this.externalSystem = ExternalSystem.build()
    }

    void "test create experiment"() {
        given:

        when:
        def response = controller.create(Experiment.class.simpleName, this.experiment.id)

        then:
        assert response.externalReferenceInstance?.experiment == this.experiment
    }

    void "test create project"() {
        given:

        when:
        def response = controller.create(Project.class.simpleName, this.project.id)

        then:
        assert response.externalReferenceInstance?.project == this.project
    }

    void "test create with errors"() {
        given:

        when:
        controller.create("NoClass", 0L)

        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
        assert response.text == "A project or an experiment is required"
    }

    void "test save #testDescription"() {
        given:
        ExternalReferenceCommand externalReferenceCommand = new ExternalReferenceCommand(experiment: exp, project: proj, externalSystem: extSys, extAssayRef: extAssayRef)

        when:
        controller.save(externalReferenceCommand)

        then:
        assert flash.message == expectedFlashMessage
        assert view == "/externalReference/create"

        where:
        testDescription | exp | proj | extSys | extAssayRef | expectedFlashMessage
        "with experiment" | this.experiment | null         | this.externalSystem | "aid=1"        | "Successfully created a new external-reference"
        "with project"    | null            | this.project | this.externalSystem | "ProjectUID=1" | "Successfully created a new external-reference"
        "with errors"     | null            | this.project | this.externalSystem | ""             | "Failed to create a new external-reference"
    }
}
