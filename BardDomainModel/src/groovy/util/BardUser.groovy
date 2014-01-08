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
