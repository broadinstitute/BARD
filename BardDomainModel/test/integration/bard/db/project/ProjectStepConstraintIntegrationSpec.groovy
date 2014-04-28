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

import bard.db.BardIntegrationSpec
import bard.db.experiment.Experiment
import bard.db.people.Role
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.project.ProjectStep.EDGE_NAME_MAX_SIZE
import static bard.db.project.ProjectStep.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectStepConstraintIntegrationSpec extends BardIntegrationSpec {

    ProjectStep domainInstance
    def fixtureLoader

    @Before
    void doSetup() {
        def fixture = fixtureLoader.build {
            role1(Role, authority:"ROLE_1",displayName:'ROLE_1')
            project(Project, ownerRole:role1)
            nextExperiment(Experiment, ownerRole:role1)
            nextProjectExperiment(ProjectSingleExperiment, project: project, experiment: nextExperiment)
            previousExperiment(Experiment, ownerRole:role1)
            previousProjectExperiment(ProjectSingleExperiment, project: project, experiment: previousExperiment)
        }
        def props = [nextProjectExperiment: fixture.nextProjectExperiment, previousProjectExperiment: fixture.previousProjectExperiment]
        domainInstance = ProjectStep.buildWithoutSave(props)

    }

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance.save(flush: true)
        }
    }

    void "test nextProjectExperiment constraints #desc"() {

        final String field = 'nextProjectExperiment'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                          | valueUnderTest                | valid | errorCode
        'null not valid'              | { null }                      | false | 'nullable'
        'valid nextProjectExperiment' | { ProjectExperiment.build() } | true  | null

    }

    void "test previousProjectExperiment constraints #desc"() {

        final String field = 'previousProjectExperiment'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                              | valueUnderTest                | valid | errorCode
        'null not valid'                  | { null }                      | false | 'nullable'
        'valid previousProjectExperiment' | { ProjectExperiment.build() } | true  | null

    }

    void "test edgeName constraints #desc edgeName: '#valueUnderTest'"() {

        final String field = 'edgeName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                       | valid | errorCode
        'too long'         | createString(EDGE_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                   | false | 'blank'
        'blank valid'      | '  '                                 | false | 'blank'

        'exactly at limit' | createString(EDGE_NAME_MAX_SIZE)     | true  | null
        'null valid'       | null                                 | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                     | false | 'blank'
        'blank valid'      | '  '                                   | false | 'blank'

        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test dateCreated constraints #desc dateCreated: '#valueUnderTest'"() {
        final String field = 'dateCreated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

}
