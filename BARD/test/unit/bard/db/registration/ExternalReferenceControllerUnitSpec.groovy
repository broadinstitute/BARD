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
import bard.db.project.Project
import spock.lang.Shared
import spock.lang.Specification
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
    @Shared
    ExternalReference externalReference1
    @Shared
    ExternalReference externalReference2

    @Before
    void setup() {
        this.experiment = Experiment.build()
        this.project = Project.build()
        this.externalSystem = ExternalSystem.build()
        this.externalReference1 = ExternalReference.build(experiment: experiment)
        this.externalReference2 = ExternalReference.build(project: project)
    }

    void "test create ExternalReference with experiment"() {
        given:

        when:
        def response = controller.create(Experiment.class.simpleName, this.experiment.id, null)

        then:
        assert response.externalReferenceInstance?.experiment == this.experiment
        assert response.isEdit == false
    }

    void "test update ExternalReference with experiment"() {
        given:

        when:
        def response = controller.create(Experiment.class.simpleName, this.experiment.id, externalReference1.id)

        then:
        assert response.externalReferenceInstance?.experiment == this.experiment
        assert response.isEdit == true
    }


    void "test create ExternalReference with project"() {
        given:

        when:
        def response = controller.create(Project.class.simpleName, this.project.id, null)

        then:
        assert response.externalReferenceInstance?.project == this.project
        assert response.isEdit == false
    }

    void "test update ExternalReference with project"() {
        given:

        when:
        def response = controller.create(Project.class.simpleName, this.project.id, externalReference2.id)

        then:
        assert response.externalReferenceInstance?.project == this.project
        assert response.isEdit == true
    }

    void "test create with errors"() {
        given:

        when:
        controller.create("NoClass", 0L, null)

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

    void "test edit ExternalReference with experiment #testDescription"() {
        given:
        def es = ExternalSystem.build()
        def externalReference = ExternalReference.build(experiment: experiment, externalSystem: es, extAssayRef: "aid=888")
        ExternalReferenceCommand externalReferenceCommand = new ExternalReferenceCommand(id: externalReference.id, experiment: exp, externalSystem: extSys, extAssayRef: extAssayRef)

        when:
        controller.edit(externalReferenceCommand)

        then:
        assert flash.message == expectedFlashMessage
        assert view == "/externalReference/create"

        where:
        testDescription | exp | proj | extSys | extAssayRef | expectedFlashMessage
        "with experiment" | this.experiment | null         | this.externalSystem | "aid=1"        | "Successfully updated the external-reference"
        "with errors"     | this.experiment            | null | this.externalSystem | ""             | "Failed to update the external-reference"
    }

    void "test edit ExternalReference with project #testDescription"() {
        given:
        def es = ExternalSystem.build()
        def externalReference = ExternalReference.build(project: project, externalSystem: es, extAssayRef: "aid=999")
        ExternalReferenceCommand externalReferenceCommand = new ExternalReferenceCommand(id: externalReference.id, project: proj, externalSystem: extSys, extAssayRef: extAssayRef)

        when:
        controller.edit(externalReferenceCommand)

        then:
        assert flash.message == expectedFlashMessage
        assert view == "/externalReference/create"

        where:
        testDescription | exp | proj | extSys | extAssayRef | expectedFlashMessage
        "with project"    | null            | this.project | this.externalSystem | "ProjectUID=1" | "Successfully updated the external-reference"
        "with errors"     | null            | this.project | this.externalSystem | ""             | "Failed to update the external-reference"
    }

    void "test delete ExternalReference with project #testDescription"() {
        given:
        def es = ExternalSystem.build(systemName: "TestSystem")
        def externalReferenceA = ExternalReference.build(project: project, externalSystem: es, extAssayRef: "aid=999")
        def externalReferenceB = ExternalReference.build(project: project, externalSystem: es, extAssayRef: "aid=111")


        when:
        controller.delete(Project.class.simpleName, project.id, externalReferenceA.id)

        then:
        assert flash.success == expectedFlashMessage
        assert flash.error == errorMessage
        assert response.redirectedUrl == "/project/show/${project.id}"

        where:
        testDescription   |  expectedFlashMessage                                           | errorMessage
        "with project"    |  "Successfully deleted external-reference: TestSystem aid=999" |  null
    }

    void "test delete ExternalReference with experiment #testDescription"() {
        given:
        def es = ExternalSystem.build(systemName: "TestSystem")
        def externalReferenceA = ExternalReference.build(experiment: experiment, externalSystem: es, extAssayRef: "aid=999")
        def externalReferenceB = ExternalReference.build(experiment: experiment, externalSystem: es, extAssayRef: "aid=111")


        when:
        controller.delete(Experiment.class.simpleName, experiment.id, externalReferenceA.id)

        then:
        assert flash.success == expectedFlashMessage
        assert flash.error == errorMessage
        assert response.redirectedUrl == "/experiment/show/${experiment.id}"

        where:
        testDescription   | expectedFlashMessage                                          | errorMessage
        "with experiment"    | "Successfully deleted external-reference: TestSystem aid=999" |  null
    }
}
