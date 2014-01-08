package bard.auth

import bard.db.people.Role
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser
import util.Email

class InMemMapAuthenticationProviderService implements AuthenticationProvider, GrailsApplicationAware {
    /**
     * the Config object representing the  CbipCrowd config
     */
    protected ConfigObject config

    private Map<String, UserDetails> mockUserMap = [:]

    private def initializeUserMap() {
        this.config.CbipCrowd?.mockUsers?.each {user ->
            Map userProps = user.value

            assert userProps.username, "we're really expecting a username here! Please review the CbipCrowd section of Config.groovy"
            assert userProps.password, "we're really expecting a password here! Please review the CbipCrowd section of Config.groovy"
            assert userProps.roles, "we're really expecting a roles list here! Please review the CbipCrowd section of Config.groovy"

            final List<GrantedAuthority> grantedAuthorities = userProps.roles.collect {role -> new Role(authority: role, displayName: role)}
            mockUserMap.put(userProps.username, new BardUser(username: userProps.username,
                    password: userProps.password,
                    fullName: userProps.username,
                    email: new Email(userProps.email),
                    isActive: true,
                    authorities: grantedAuthorities
            ))
        }
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * Looks up the user in the in memory map and verifies the password
     * @param authentication with username and password
     * @return the authenticated user Authentication object or null if the username wasn't found
     *
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        final String username = authentication.getName();
        final BardUser user = findByUserName(username);
        if (user && user.username) {
            if (user.password == authentication.credentials.toString()) {
                // if user.authorities is empty, Spring raises an exception. Must use a different constructor.
                if (user.authorities) {
                    return new UsernamePasswordAuthenticationToken(user, user.password, user.authorities);
                } else {
                    return new UsernamePasswordAuthenticationToken(user, user.password);
                }
            }
            else {
                throw new BadCredentialsException("Bad username or password.");
            }
        }
        return null // returning null to allow another authenticationProvider a chance
    }

    public BardUser findByUserName(String userName) throws UsernameNotFoundException {
        def bardUser = this.mockUserMap.get(userName)
        if (bardUser) {
            return bardUser
        }
        throw new UsernameNotFoundException("Mock username ${userName} not found");
    }

    /**
     * have the grailsApplication set by the spring context and initialize the
     * @param grailsApplication
     */
    @Override
    void setGrailsApplication(org.codehaus.groovy.grails.commons.GrailsApplication grailsApplication) {
        this.config = grailsApplication.config
        initializeUserMap()
    }

}
