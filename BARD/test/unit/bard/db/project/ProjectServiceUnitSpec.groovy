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

package bard.db.project

import acl.CapPermissionService
import bard.db.enums.Status
import bard.db.experiment.PanelExperiment
import bard.db.people.Role
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.junit.Before
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Project, Role, PanelExperiment, ProjectPanelExperiment])
@Mock([Project, Role])
@TestMixin(ServiceUnitTestMixin)
@TestFor(ProjectService)
@Unroll
public class ProjectServiceUnitSpec extends Specification {

    CapPermissionService capPermissionService

    @Before
    void setup() {
        this.capPermissionService = Mock(CapPermissionService)
        service.capPermissionService = capPermissionService
    }

    void "test update project name"() {
        given:
        final Project project = Project.build(name: 'projectName10', description: "desc789")
        final String newName = "desc9099"
        when:
        final Project updatedProject = service.updateProjectName(project.id, newName)
        then:
        assert newName == updatedProject.name
    }

    void "test update owner role"() {
        given:
        final Project project = Project.build(name: 'projectName10', description: "desc789", ownerRole: Role.build(authority: "ROLE_TEAM_YY"))
        Role newRole = Role.build(authority: "ROLE_TEAM_ZY")
        when:
        final Project updatedProject = service.updateOwnerRole(project.id, newRole)
        then:
        assert newRole == updatedProject.ownerRole
    }

    void "test update project description"() {
        given:
        final Project project = Project.build(name: 'projectName10', description: "desc789")
        final String newDescription = "desc9099"
        when:
        final Project updatedProject = service.updateProjectDescription(project.id, newDescription)
        then:
        assert newDescription == updatedProject.description
    }

    void "test update project status #desc"() {
        given:
        final Project project = Project.build(name: 'projectName10', projectStatus: oldStatus)
        Project.metaClass.isDirty = { String field -> false }
        when:
        final Project updatedProject = service.updateProjectStatus(project.id, newStatus)
        then:
        assert newStatus == updatedProject.projectStatus

        where:
        desc                               | oldStatus          | newStatus
        "Change from draft to Approved"    | Status.DRAFT       | Status.APPROVED
        "Change from draft to Provisional" | Status.DRAFT       | Status.PROVISIONAL
        "Change from draft to Provisional" | Status.PROVISIONAL | Status.APPROVED

    }

    void "test isPanelExperimentAssociatedWithProject true"() {
        given:
        final Project project = Project.build(name: 'projectName10', projectStatus: Status.DRAFT)
        final PanelExperiment panelExperiment = PanelExperiment.build()
        final ProjectExperiment = ProjectPanelExperiment.build(project: project, panelExperiment: panelExperiment)

        when:
        Boolean res = service.isPanelExperimentAssociatedWithProject(panelExperiment, project)

        then:
        assert res == true
    }

    void "test isPanelExperimentAssociatedWithProject false"() {
        given:
        final Project project = Project.build(name: 'projectName10', projectStatus: Status.DRAFT)
        final PanelExperiment panelExperiment = PanelExperiment.build()

        when:
        Boolean res = service.isPanelExperimentAssociatedWithProject(panelExperiment, project)

        then:
        assert res == false
    }
}
