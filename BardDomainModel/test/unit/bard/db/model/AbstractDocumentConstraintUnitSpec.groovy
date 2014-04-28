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

package bard.db.model

import bard.db.enums.DocumentType
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.model.AbstractDocument.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/5/12
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractDocumentConstraintUnitSpec extends Specification {

    def domainInstance

    abstract void doSetup()

    void "test documentName constraints #desc documentName: "() {

        final String field = 'documentName'

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

        'too long'         | createString(DOCUMENT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(DOCUMENT_NAME_MAX_SIZE)     | true  | null
    }

    void "test documentType constraints #desc documentType: '#valueUnderTest'"() {

        final String field = 'documentType'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                          | valid | errorCode
        'valid value' | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_PROTOCOL     | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_COMMENTS     | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_PUBLICATION  | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | true  | null
        'valid value' | DocumentType.DOCUMENT_TYPE_OTHER        | true  | null
    }

    void "test documentContent constraints #desc documentContent: "() {

        final String field = 'documentContent'

        when:
        domainInstance.documentType = documentType
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                                      | documentType                            | valueUnderTest         | valid | errorCode
        'blank not valid'                         | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | ''                     | false | 'blank'
        'blank not valid'                         | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | '   '                  | false | 'blank'
        'url expected for publication not valid'  | DocumentType.DOCUMENT_TYPE_PUBLICATION  | 'foo'                  | false | 'document.invalid.url.message'
        'url expected for external url not valid' | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | 'foo'                  | false | 'document.invalid.url.message'

        'url expected for publication valid'      | DocumentType.DOCUMENT_TYPE_PUBLICATION  | 'http://foo.bar'       | true  | null
        'url expected for external url valid'     | DocumentType.DOCUMENT_TYPE_EXTERNAL_URL | 'http://foo.bar'       | true  | null
        'null valid'                              | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | null                   | true  | null
        'greater than varchar limit'              | DocumentType.DOCUMENT_TYPE_DESCRIPTION  | createString(4000 + 1) | true  | null

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
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

}
