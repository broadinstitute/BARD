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

package bard.db.people

import bard.db.BardIntegrationSpec
import spock.lang.Unroll

import static bard.db.people.Person.NAME_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/11/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class PersonConstraintIntegrationSpec extends BardIntegrationSpec {
    Person domainInstance


    void "test name constraints #desc name: '#valueUnderTest'"() {
        given:
        domainInstance = Person.buildWithoutSave()
        final String field = 'userName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                  | valid | errorCode
        'null '            | null                            | false | 'nullable'
        'blank value'      | ''                              | false | 'blank'
        'blank value'      | '  '                            | false | 'blank'
        'too long'         | createString(NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(NAME_MAX_SIZE)     | true  | null
    }

    void "test fullName constraints #desc fullName: '#valueUnderTest'"() {
        given:
        domainInstance = Person.buildWithoutSave()
        final String field = 'fullName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                  | valid | errorCode
        'null '            | null                            | true | null
        'blank value'      | ''                              | false | 'blank'
        'blank value'      | '  '                            | false | 'blank'
        'too long'         | createString(NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(NAME_MAX_SIZE)     | true  | null
    }

    void "test getRoles"() {
        given:
        Person person = Person.build()
        Role role = Role.build()
        PersonRole.create(person, role, "me", false)
        when:
        Set<Role> roles = person.getRoles()

        then:
        assert roles
        assert roles.size() == 1
        assert roles.iterator().next() == role

    }
}
