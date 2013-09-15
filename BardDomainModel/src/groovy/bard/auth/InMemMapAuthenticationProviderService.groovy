package bard.auth

import bard.db.people.Role
import org.broadinstitute.cbip.crowd.AbstractCrowdAuthenticationProviderService
import org.broadinstitute.cbip.crowd.Email
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser

class InMemMapAuthenticationProviderService extends AbstractCrowdAuthenticationProviderService {
    private Map<String, UserDetails> mockUserMap = [:]

    private def initializeUserMap() {
        this.config.CbipCrowd?.mockUsers?.each {user ->
            Map userProps = user.value

            assert userProps.username, "we're really expecting a username here! Please review the CbipCrowd section of Config.groovy"
            assert userProps.password, "we're really expecting a password here! Please review the CbipCrowd section of Config.groovy"
            assert userProps.roles, "we're really expecting a roles list here! Please review the CbipCrowd section of Config.groovy"

            final List<GrantedAuthority> grantedAuthorities = userProps.roles.collect {role -> new Role(authority: role)}
            mockUserMap.put(userProps.username, new BardUser(username: userProps.username,
                    password: userProps.password,
                    fullName: userProps.username,
                    email: new Email(userProps.email),
                    isActive: true,
                    authorities: grantedAuthorities,
                    owningRole: userProps.owningRole?new Role(authority: userProps.owningRole):new Role(authority: userProps.username)
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

    @Override
    public BardUser findByUserName(String userName) throws UsernameNotFoundException {
        def bardUser = this.mockUserMap.get(userName)
        if (bardUser) {
            return bardUser
        }
        throw new UsernameNotFoundException("Mock username ${userName} not found");
    }

    @Override
    List<BardUser> findUsersInGroup(String group) {
        GrantedAuthority groupWithPrefixedRole = toApplicationRole(group)
        return this.mockUserMap.findAll {String username, BardUser user ->
            // user can have multiple roles, so if any match the group return the user
            user.authorities.find {authority -> authority == groupWithPrefixedRole }
        }
        .values() as List

    }

    @Override
    List<GrantedAuthority> getNamesOfGroupsForUser(String userName) {
        return this.mockUserMap.get(userName)?.authorities ?: []
    }

    @Override
    List<BardUser> getUsersOfGroup(String groupName) {
        return findUsersInGroup(groupName)
    }

    @Override
    boolean isUserDirectGroupMember(String username, String groupName) {
        GrantedAuthority groupWithPrefixedRole = toApplicationRole(groupName)
        return findByUserName(username)?.authorities?.find {authority -> authority == groupWithPrefixedRole }
    }

    /**
     * have the grailsApplication set by the spring context and initialize the
     * @param grailsApplication
     */
    @Override
    void setGrailsApplication(org.codehaus.groovy.grails.commons.GrailsApplication grailsApplication) {
        super.setGrailsApplication(grailsApplication)
        initializeUserMap()
    }
}
