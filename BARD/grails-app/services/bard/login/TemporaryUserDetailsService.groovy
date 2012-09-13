package bard.login


import org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.springframework.security.core.userdetails.UserDetails

/**
 * Very temporary implementation of tha userDetails service that just echos the userName
 */
class TemporaryUserDetailsService extends GormUserDetailsService {

    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles) {
        def user = new GrailsUser(username, 'password', true, true, true, true, org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService.NO_ROLES, "0")
        return user
    }
}
