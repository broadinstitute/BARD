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

package bard.db.command

import bard.db.people.Role
import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.GrantedAuthority

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 3/27/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class BardCommand {
    static final String BARD_ADMIN_ROLE_AUTHORITY = "ROLE_BARD_ADMINISTRATOR"

    def attemptFindById(Class domain, Long id) {
        def instance
        if (id) {
            instance = domain.findById(id)
        }
        if (!instance) {
            ValidationErrors localErrors = new ValidationErrors(this)
            localErrors.reject('default.not.found.message', [domain, id] as Object[], 'not found')
            addToErrors(localErrors)
        }
        return instance
    }

    boolean attemptSave(Object domain) {
        if (!domain?.save(flush: true)) {
            domain?.errors?.fieldErrors?.each { error ->
                if (properties.containsKey(error.field)) {  // if the command object has a property with the same name as the field copy the fieldError
                    getErrors().rejectValue(error.field, error.code)
                } else { // otherwise register a global error
                    getErrors().reject(error.code)
                }
            }
            domain?.errors?.globalErrors?.each { error ->
                getErrors().reject(error.code)
            }
            return false
        }
        return true
    }

    protected addToErrors(ValidationErrors localErrors) {
        if (errors) {
            errors.addAllErrors(localErrors)
        } else {
            setErrors(localErrors)
        }
    }

    static List<Role> userRoles() {
        final Set<Role> roleSet = [] as Set<Role>

        final Collection<GrantedAuthority> grantedAuthorities = SpringSecurityUtils.getPrincipalAuthorities()
        //final List<Role> authorities = []

        //if logged in user is an admin , then add all teams
        if (isCurrentUserBARDAdmin()) {
            //Only add the roles with ids
            roleSet.addAll(Role.getTeamRoles())
            //add BARD Admin
            final Role bard_admin = Role.findByAuthority(BARD_ADMIN_ROLE_AUTHORITY)

            roleSet.add(bard_admin)
        }
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if ((grantedAuthority.authority?.startsWith("ROLE_TEAM_"))) {
                Role role = (Role) grantedAuthority
                roleSet.add(role)
            }
        }
        List<Role> roles = new ArrayList<Role>(roleSet)

        //sort by display name
        return roles.sort { it.displayName }
    }

    static boolean isCurrentUserBARDAdmin() {

        return SpringSecurityUtils.ifAnyGranted(BARD_ADMIN_ROLE_AUTHORITY)
    }
    /**
     *
     * Is this role in the list of the current users logged in Roles
     * @param ownerRole
     * @return
     */
    static boolean isRoleInUsersRoleList(Role ownerRole) {

        if (isCurrentUserBARDAdmin()) { //Admins should be able to create any entities
            return true
        }
        for (Role role : SpringSecurityUtils.getPrincipalAuthorities()) {
            if (role.authority == ownerRole.authority) {
                return true
            }
        }
        return false

    }

}
