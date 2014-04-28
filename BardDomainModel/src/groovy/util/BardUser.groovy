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

package util

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/15/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
class BardUser implements UserDetails {

    Email email
    Collection<GrantedAuthority> authorities;
    String fullName;
    boolean isActive;
    String username;
    String password;

    BardUser() {

    }

    boolean isAccountNonExpired() {
        return isActive;
    }

    boolean isAccountNonLocked() {
        return isActive;
    }

    boolean isCredentialsNonExpired() {
        return isActive;
    }

    boolean isEnabled() {
        return isActive;
    }

    public BardUser(def user) {
        if(user instanceof Map) {
            this.metaClass.setProperties(this, user)
            return
        }

        // use duck typing to work with CbipUser instances as well as Person instances
        // not entirely sure what/how this is used.  May want to figure that out at some point.
        if(user.metaClass.hasProperty(user, "email")) {
            this.email = new Email(email: user.email.email);
        } else if(user.metaClass.hasProperty(user, "emailAddress")) {
            this.email = new Email(email: user.emailAddress)
        }

        this.fullName = user.fullName

        if(user.metaClass.hasProperty(user, "authorities")) {
            this.authorities = user.authorities;
        } else if(user.metaClass.hasProperty(user, "roles")) {
            this.authorities = user.roles;
        }

        if(user.metaClass.hasProperty(user, "isActive")) {
            this.isActive = user.isActive;
        } else if(user.metaClass.respondsTo(user, "isAccountLocked")) {
            this.isActive = user.isAccountLocked()?false:true
        }

        if(user.metaClass.hasProperty("username")) {
            this.username = user.username;
        } else if(user.metaClass.hasProperty("userName")) {
            this.username = person.userName
        }
    }
}
