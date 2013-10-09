package bard.db.command

import bard.db.people.Role
import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

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

        final Collection<Role> authorities = SpringSecurityUtils.getPrincipalAuthorities()

        //if logged in user is an admin , then add all teams
        if (isCurrentUserBARDAdmin()) {
            authorities.addAll(Role.list())
        }
        for (Role role : authorities) {
            if (role.authority?.startsWith("ROLE_TEAM_")) {
                roleSet.add(role)
            }
        }
        List<Role> roles = new ArrayList<Role>(roleSet)

        //sort by display name
        return roles.sort { it.displayName }
    }

    static boolean isCurrentUserBARDAdmin() {
        for (Role role : SpringSecurityUtils.getPrincipalAuthorities()) {
            if (role.authority == BARD_ADMIN_ROLE_AUTHORITY) {
                return true
            }
        }
        return false
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
