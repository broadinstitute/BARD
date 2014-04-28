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

import bard.db.enums.TeamRole
import org.apache.commons.lang.builder.HashCodeBuilder

class PersonRole implements Serializable {

    public static final int TEAM_ROLE_MAX_SIZE = 40

    Person person
    Role role
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy
    TeamRole teamRole = TeamRole.MEMBER
//    String teamRole = "Member"
    boolean equals(other) {
        if (!(other instanceof PersonRole)) {
            return false
        }

        other.person?.id == person?.id &&
                other.role?.id == role?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (person) builder.append(person.id)
        if (role) builder.append(role.id)
        builder.toHashCode()
    }

    static PersonRole get(long personId, long roleId) {
        find 'from PersonRole where person.id=:personId and role.id=:roleId',
                [personId: personId, roleId: roleId]
    }

    static PersonRole create(Person person, Role role, String modifiedBy,boolean flush = false) {
        new PersonRole(person: person, role: role, modifiedBy: modifiedBy,dateCreated: new Date(), lastUpdated: new Date()).save(flush: flush, insert: true)
    }

    static boolean remove(Person person, Role role, boolean flush = false) {
        PersonRole instance = PersonRole.findByPersonAndRole(person, role)
        if (!instance) {
            return false
        }

        instance.delete(flush: flush)
        true
    }

    static void removeAll(Person person) {
        executeUpdate 'DELETE FROM PersonRole WHERE person=:person', [person: person]
    }

    static void removeAll(Role role) {
        executeUpdate 'DELETE FROM PersonRole WHERE role=:role', [role: role]
    }


    static mapping = {
        table('PERSON_ROLE')
        id(column: 'PERSON_ROLE_ID', generator: "sequence", params: [sequence: 'PERSON_ROLE_ID_SEQ'])
//        teamRole(type: TeamRole)
        //id composite: ['role', 'person']

    }

    static constraints = {
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: Person.MODIFIED_BY_MAX_SIZE)
        teamRole(nullable: true, blank: false, maxSize: TEAM_ROLE_MAX_SIZE)
    }
}
