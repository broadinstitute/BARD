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

package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

@Build(Element)
@Unroll
class ElementConstraintUnitSpec extends AbstractElementConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = Element.buildWithoutSave()
    }

    void "test addChildMethod constraints #desc addChildMethod: '#valueUnderTest'"() {
        final String field = 'addChildMethod'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the it can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest             | valid | errorCode
        'null not value' | null                       | false | 'nullable'
        'valid value'    | AddChildMethod.NO          | true  | null
        'valid value'    | AddChildMethod.DIRECT      | true  | null
        'valid value'    | AddChildMethod.RDM_REQUEST | true  | null
    }

    void "test expectedValueType constraints #desc expectedValueType: '#valueUnderTest'"() {
        final String field = 'expectedValueType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.externalURL = externalUrl
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, errorField, valid, errorCode)

        and: 'verify the it can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc                                        | valueUnderTest                      | externalUrl   | valid | errorField          | errorCode
        'null not value'                            | null                                | ''            | false | 'expectedValueType' | 'nullable'
        'valid value'                               | ExpectedValueType.NONE              | ''            | true  | 'expectedValueType' | null
        'valid value'                               | ExpectedValueType.ELEMENT           | ''            | true  | 'expectedValueType' | null
        'valid value'                               | ExpectedValueType.EXTERNAL_ONTOLOGY | 'externalUrl' | true  | 'externalURL'       | null
        'external ontology with empty external url' | ExpectedValueType.EXTERNAL_ONTOLOGY | ''            | false | 'externalURL'       | 'When ExpectedValue is set to ExternalOntology, externalURL can not be empty'
        'valid value'                               | ExpectedValueType.FREE_TEXT         | ''            | true  | 'expectedValueType' | null
        'valid value'                               | ExpectedValueType.NUMERIC           | ''            | true  | 'expectedValueType' | null
    }


}
