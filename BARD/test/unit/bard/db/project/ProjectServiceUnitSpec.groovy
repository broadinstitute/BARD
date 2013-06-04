package bard.db.project

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ProjectStatus
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import registration.AssayService
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project])
@Mock([Project])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ProjectService)
public class ProjectServiceUnitSpec extends Specification {

    void "test update project name"(){
        given:
        final Project project = Project.build(name: 'projectName10', description: "desc789")
        final String newName = "desc9099"
        when:
        final Project updatedProject = service.updateProjectName(project.id, newName)
        then:
        assert newName == updatedProject.name
    }
    void "test update project description"(){
        given:
        final Project project = Project.build(name: 'projectName10', description: "desc789")
        final String newDescription = "desc9099"
        when:
        final Project updatedProject = service.updateProjectDescription(project.id, newDescription)
        then:
        assert newDescription == updatedProject.description
    }
    void "test update project status"() {
        given:
        final Project project = Project.build(name: 'projectName10', projectStatus: ProjectStatus.DRAFT)
        when:
        final Project updatedProject = service.updateProjectStatus(project.id, ProjectStatus.APPROVED)
        then:
        assert ProjectStatus.APPROVED == updatedProject.projectStatus
    }
}
