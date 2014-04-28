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

import org.apache.commons.lang.StringUtils

class Person {
    public static final int NAME_MAX_SIZE = 255
    public static final int MODIFIED_BY_MAX_SIZE = 40

    String userName
    String emailAddress
    String fullName
    boolean accountExpired
    boolean accountLocked
    boolean accountEnabled
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static mapping = {
        table('PERSON')
        id(column: 'PERSON_ID', generator: "sequence", params: [sequence: 'PERSON_ID_SEQ'])
        version(false)
        userName(column: 'USERNAME')
    }
    static transients = ["rolesAsList", "displayName"]

    static constraints = {
        userName(blank: false, maxSize: NAME_MAX_SIZE)
        emailAddress(nullable: true, maxSize: NAME_MAX_SIZE, validator: {
            if(it != null && it.toLowerCase() != it.toLowerCase()) {
                return 'email.not.lowercase'
            }
        })
        fullName(nullable: true, blank: false, maxSize: NAME_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    String getDisplayName(){
        if(fullName)
            return fullName.contains('@') ? StringUtils.substringBefore(fullName,'@') : fullName
        else
            return !emailAddress ? userName : StringUtils.substringBefore(emailAddress, '@')
    }

    Set<Role> getRoles() {

        PersonRole.withTransaction {  //see http://jira.grails.org/browse/GRAILS-8450
            return PersonRole.findAllByPerson(this).collect { it.role } as Set
        }
    }

    String getRolesAsList() {
        List<String> displayNames = []
        for (Role role : getRoles()) {
            displayNames.add(role.displayName)
        }
        return displayNames.join(",")
    }
}
