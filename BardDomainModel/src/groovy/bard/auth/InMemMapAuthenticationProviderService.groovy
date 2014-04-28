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

package bard.auth

import bard.db.people.Role
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import util.BardUser
import util.Email

class InMemMapAuthenticationProviderService implements AuthenticationProvider, GrailsApplicationAware, InitializingBean {
    /**
     * the Config object representing the  CbipCrowd config
     */
    protected ConfigObject config

    private Map<String, UserDetails> mockUserMap = [:]

    private def initializeUserMap() {
        List users = []

        // try using the original name because we have lots of old config files that
        // refer to local users with this name.
        if(this.config.CbipCrowd?.mockUsers)  {
            this.config.CbipCrowd?.mockUsers.each {user ->
                users << user.value
            }
        }

        if(this.config.localUsers) {
            users.addAll(this.config.localUsers)
        }

        users.each {userProps ->

            assert userProps.username, "we're really expecting a username here! Please review the localUsers section of Config.groovy"
            assert userProps.password, "we're really expecting a password here! Please review the localUsers section of Config.groovy"
            assert userProps.roles, "we're really expecting a roles list here! Please review the localUsers section of Config.groovy"

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
    }

    @Override
    void afterPropertiesSet() throws Exception {
        initializeUserMap()
    }
}
