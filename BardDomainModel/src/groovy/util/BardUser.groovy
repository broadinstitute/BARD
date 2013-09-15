package util

import bard.db.people.Person
import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import org.broadinstitute.cbip.crowd.CbipUser
import org.broadinstitute.cbip.crowd.Email
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 9/15/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
class BardUser extends CbipUser {
    Role owningRole //the role that owns the object associated with this user

    BardUser() {

    }

    public BardUser(CbipUser cbipUser) {
        this.email = cbipUser.email;
        this.fullName = cbipUser.fullName
        this.authorities = cbipUser.authorities;
        this.isActive = cbipUser.isActive;
        this.username = cbipUser.username;
    }
    public BardUser(Person person){
        this.email = new Email(email:person.emailAddress)
        this.fullName = person.fullName
        this.authorities = person.roles
        this.isActive = person.isAccountLocked()?false:true
        this.username = person.userName
        this.owningRole=person.newObjectRole?:new Role(this.username)
    }
}
