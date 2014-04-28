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

package bard.db.experiment

import bard.db.BardIntegrationSpec
import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.experiment.ExperimentMeasure.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/9/12
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ExperimentMeasureConstraintIntegrationSpec extends BardIntegrationSpec {

    ExperimentMeasure domainInstance

    @Before
    void doSetup() {
        domainInstance = ExperimentMeasure.buildWithoutSave(resultType: Element.build())
//        domainInstance.measure.resultType.save()
//        domainInstance.measure.save(flush: true)
    }

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance.save(flush: true)
        }
    }

    void "test statsModifier constraints #desc statsModifier: '#valueUnderTest'"() {

        final String field = 'statsModifier'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                  | valueUnderTest      | valid | errorCode
        'null valid'          | { null }            | true  | null
        'valid statsModifier' | { Element.build() } | true  | null

    }


    void "test resultType constraints #desc resultType: '#valueUnderTest'"() {

        final String field = 'resultType'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest      | valid | errorCode
        'null not valid'   | { null }            | false | 'nullable'
        'valid resultType' | { Element.build() } | true  | null

    }

    void "test parent constraints #desc parent: '#valueUnderTest'"() {

        final String field = 'parent'
        final String parentChildRelationShip = HierarchyType.SUPPORTED_BY
        when:
        domainInstance[(field)] = valueUnderTest?.call()
        if (valueUnderTest != null) {
            domainInstance['parentChildRelationship'] = parentChildRelationShip
        }
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc           | valueUnderTest                | valid | errorCode
        'null valid'   | null                          | true  | null
        'valid parent' | { ExperimentMeasure.build() } | true  | null
    }

    void "test parent child link"() {
        when:
        ExperimentMeasure child = ExperimentMeasure.build(parent: domainInstance, parentChildRelationship: HierarchyType.SUPPORTED_BY)

        then:
        child.id != null
        child.childMeasures.size() == 0
        domainInstance.childMeasures.size() == 1
        domainInstance.childMeasures.first() == child
    }

    void "test parentChildRelationship constraints #desc parentChildRelationship: '#valueUnderTest'"() {

        final String field = 'parentChildRelationship'
        given:
        if (valueUnderTest) {
            domainInstance['parent'] = ExperimentMeasure.build()
        }
        when: 'a value is set for the field under test'

        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest                | valid | errorCode
        'null valid'  | null                          | true  | null
        'valid value' | HierarchyType.CALCULATED_FROM | true  | null
        'valid value' | HierarchyType.SUPPORTED_BY    | true  | null
    }

    void "test experiment constraints #desc experiment: '#valueUnderTest'"() {

        final String field = 'experiment'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest         | valid | errorCode
        'null not valid'   | { null }               | false | 'nullable'
        'valid experiment' | { Experiment.build() } | true  | null

    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}
