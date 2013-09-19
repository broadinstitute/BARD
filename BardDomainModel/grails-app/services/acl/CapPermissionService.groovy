package acl

import bard.acl.CapPermissionInterface
import bard.db.people.Person
import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclClass
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclEntry
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclObjectIdentity
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.acls.model.Permission
import org.springframework.security.acls.model.Sid
import org.springframework.security.acls.model.AccessControlEntry

class CapPermissionService  implements CapPermissionInterface{

    AclUtilService aclUtilService
    SpringSecurityService springSecurityService

    void addPermission(domainObjectInstance) {
        String userName = springSecurityService.principal?.username
        Person person = Person.findByUserName(userName)


        //we would use a default role so that all of our tests can pass
        //Take this out as soon as we complete https://www.pivotaltracker.com/story/show/51238251
        Role newObjectRole = person?.newObjectRole ?: new Role(authority: 'ROLE_TEAM_UNASSIGNED')
        //we assume that the newObjectRole should never be null. There will be a check constraint to insure that
        addPermission(domainObjectInstance, newObjectRole, BasePermission.ADMINISTRATION)
    }

    void addPermission(domainObjectInstance, Role role, Permission permission) {
        aclUtilService.addPermission(domainObjectInstance, role.authority, permission)
    }

    void removePermission(domainObjectInstance) {
        //find the recipient

        final AclObjectIdentity aclObjectIdentity = AclObjectIdentity.findByObjectId(domainObjectInstance.id)
        if (aclObjectIdentity) {
            AclEntry aclEntry = AclEntry.findByAclObjectIdentity(aclObjectIdentity)
            final String recipient = aclEntry?.sid?.sid
            if (recipient) {
                aclUtilService.deletePermission(domainObjectInstance, recipient, BasePermission.ADMINISTRATION)
            }
        }
    }
	
    /**
     * @param domainObjectInstance a domainInstance we track ACL permissions on
     * @return a String representing the Role name or username that owns this domainInstance
     */
    String getOwner(domainObjectInstance) {
        String owner = "none"

        final Class<?> clazz = domainObjectInstance.getClass()
        AclClass aclClass = AclClass.findByClassName(clazz.getName())
        final AclObjectIdentity aclObjectIdentity = AclObjectIdentity.findByObjectIdAndAclClass(domainObjectInstance.id, aclClass)
        if (aclObjectIdentity) {
            AclEntry aclEntry = AclEntry.findByAclObjectIdentity(aclObjectIdentity)
            AclSid aclSid = aclEntry?.sid
            if (aclSid) {
                if (!aclSid.principal) {
                    Role role = Role.findByAuthority(aclSid.sid)
                    if (role) {
                        owner = role?.displayName
                    } else {
                        owner = aclSid.sid
                    }

                } else {
                    owner = aclSid.sid
                }
            }
        }
        return owner
    }
}
