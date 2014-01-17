package bard.auth

import bard.db.people.Person
import grails.util.Environment
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService
import org.springframework.dao.DataAccessException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser
import util.Email

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 1/8/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
class BardUserDetailsService implements GrailsUserDetailsService {
    InMemMapAuthenticationProviderService inMemMapAuthenticationProviderService

    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
        return loadUserByUsername(username)
    }

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        BardUser user;

        if(inMemMapAuthenticationProviderService != null) {
            try {
                user = inMemMapAuthenticationProviderService.findByUserName(username)
            } catch (UsernameNotFoundException ex) {
                // if no user found keep going
            }
        }

        if(user == null) {
            Person person = Person.findByUserNameIlike(username);
            if(person != null) {
                user = new BardUser(username: person.userName, fullName: person.fullName, email: new Email(person.emailAddress), isActive: true,
                        authorities: person.roles);
            }
        }

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with username ${username}");
        }

        return user;
    }
}
