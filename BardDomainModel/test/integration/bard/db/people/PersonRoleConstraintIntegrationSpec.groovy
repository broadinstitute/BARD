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

/**
 *
 */
@Unroll
class PersonRoleConstraintIntegrationSpec extends BardIntegrationSpec {
    PersonRole domainInstance



    void "test create and associate person"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person = Person.build()
        when:
        PersonRole personRole = PersonRole.create(person, role, modifiedBy)
        then:
        assert personRole
        assert personRole.person == person
        assert personRole.role == role
        assert personRole.modifiedBy == modifiedBy
    }

    void "test get"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person = Person.build()
        PersonRole.create(person, role, modifiedBy)
        when:
        PersonRole personRole = PersonRole.get(person.id, role.id)
        then:
        assert personRole
        assert personRole.person == person
        assert personRole.role == role
        assert personRole.modifiedBy == modifiedBy

    }

    void "test remove"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person = Person.build()
        PersonRole.create(person, role, modifiedBy)
        when:
        boolean personRole = PersonRole.remove(person, role, true)
        then:
        assert personRole
    }

    void "test removeAll by Person"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person1 = Person.build(userName:"userName1")
        Person person2 = Person.build(userName:"userName2")
        PersonRole.create(person1, role, modifiedBy)
        PersonRole.create(person2, role, modifiedBy)
        when:
        PersonRole.removeAll(person1)
        then:
        assert !PersonRole.get(person1.id, role.id)
        assert PersonRole.get(person2.id, role.id)
    }

    void "test removeAll by Role"() {
        given:
        String modifiedBy = "me"
        Role role1 = Role.build(authority: "authority1")
        Role role2 = Role.build(authority: "authority2")
        Person person1 = Person.build(userName:"userName1")
        Person person2 = Person.build(userName:"userName2")
        PersonRole.create(person1, role1, modifiedBy)
        PersonRole.create(person2, role2, modifiedBy)
        when:
        PersonRole.removeAll(role1)
        then:
        assert !PersonRole.get(person1.id, role1.id)
        assert PersonRole.get(person2.id, role2.id)
    }


}
