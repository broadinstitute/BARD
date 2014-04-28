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
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.registration.ExternalReference.EXT_ASSAY_REF_MAX_SIZE
import static bard.db.registration.ExternalReference.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/9/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([ExternalReference, ExternalSystem, Experiment, Project])
@Mock([ExternalReference, ExternalSystem, Experiment, Project])
@Unroll
class ExternalReferenceConstraintUnitSpec extends Specification {

    def domainInstance

    @Before
    void doSetup() {
        domainInstance = ExternalReference.buildWithoutSave(experiment: Experiment.build())
    }

    void "test externalSystem constraints #desc externalSystem: #valueUnderTest"() {

        final String field = 'externalSystem'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                   | valueUnderTest             | valid | errorCode
        'null not valid'       | { null }                   | false | 'nullable'
        'valid externalSystem' | { ExternalSystem.build() } | true  | null
    }

    void "test experiment OR project constraint #desc project: #valueUnderTest"() {
        given:
        domainInstance.experiment = null
        domainInstance.project = null

        when:
        domainInstance.experiment = experiment.call()
        domainInstance.project = project.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, 'experiment', valid, errorCode)
        assertFieldValidationExpectations(domainInstance, 'project', valid, errorCode)

        where:
        desc                      | experiment             | project             | valid | errorCode
        'both null, not valid'    | { null }               | { null }            | false | 'validator.invalid'
        'both non-null not valid' | { Experiment.build() } | { Project.build() } | false | 'validator.invalid'

        'only experiment, valid'  | { Experiment.build() } | { null }            | true  | null
        'only project, valid'     | { null }               | { Project.build() } | true  | null
    }

    void "test extAssayRef constraints #desc extAssayRef: #valueUnderTest"() {

        final String field = 'extAssayRef'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                           | valid | errorCode
        'null not valid'   | null                                     | false | 'nullable'
        'blank not valid'  | ''                                       | false | 'blank'
        'blank not valid'  | '   '                                    | false | 'blank'

        'too long'         | createString(EXT_ASSAY_REF_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(EXT_ASSAY_REF_MAX_SIZE)     | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
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

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}
