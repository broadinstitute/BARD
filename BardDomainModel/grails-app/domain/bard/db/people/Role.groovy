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

import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.security.core.GrantedAuthority

class Role implements GrantedAuthority, Comparable<Role> {
    static final int DISPLAY_NAME_SIZE = 255
    static final int AUTHORITY_NAME_SIZE = 40

    String authority
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy
    String displayName

    boolean equals(Object other) {
        if (other instanceof Role) {
            Role that = (Role) other
            return that?.authority == this?.authority
        }
        return false
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        return builder.append(this.authority).toHashCode()
    }

    int compareTo(Role that) {
        return this.authority.compareTo(that.authority)
    }

    static List<Role> getTeamRoles() {
        List<Role> selectedRoles = []
        for (Role role : Role.list()) {
            if (role.authority.startsWith("ROLE_TEAM_")) {
                selectedRoles.add(role)
            }
        }
        return selectedRoles
    }

    static transients = ['teamRoles']
    static constraints = {
        authority(nullable: false, blank: false, unique: true, maxSize: AUTHORITY_NAME_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: Person.MODIFIED_BY_MAX_SIZE)
        displayName(nullable: true, blank: false, maxSize: DISPLAY_NAME_SIZE)

    }
    static mapping = {
        table('ROLE')
        id(column: 'ROLE_ID', generator: "sequence", params: [sequence: 'ROLE_ID_SEQ'])
        displayName(column: 'DISPLAY_NAME')
        cache true
    }


}
