package acl

import bard.db.people.Person
import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclClass
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclEntry
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclObjectIdentity
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

class CapPermissionService {

    AclUtilService aclUtilService
    SpringSecurityService springSecurityService

    void addPermission(domainObjectInstance) {
        String userName = springSecurityService.principal?.username
        Person person = Person.findByUserName(userName)

        //TODO: until we write the script to populate the new object role in the service table
        //we would use a default role so that all of our tests can pass
        //Take this out as soon as we complete https://www.pivotaltracker.com/story/show/51238251
        Role newObjectRole = person?.newObjectRole ?: new Role(authority: 'ROLE_TEAM_UNASSIGNED')
        //we assume that the newObjectRole should never be null. There will be a check constraint to insure that
        addPermission(domainObjectInstance, newObjectRole, BasePermission.ADMINISTRATION)
    }

    void addPermission(domainObjectInstance, Role role, Permission permission) {
        aclUtilService.addPermission(domainObjectInstance, role.authority, permission)
    }

    void updateACLTablesAfterMigrations(String userName, String newObjectRole, domainObjectInstance){
        //find the person
        Person person = Person.findByUserName(userName)
        Role  role = Role.findByAuthority(newObjectRole.toUpperCase())
        if(role){
            role = new Role(authority:newObjectRole.toUpperCase()).save(flush: true)
        }
        if(!person){

            person = new Person(userName:userName,newObjectRole: role).save(flush: true)
        }
        AclClass aclClass = AclClass.findByClassName(domainObjectInstance.getClass().getName())

        AclObjectIdentity aclObjectIdentity = AclObjectIdentity.findByAclClassAndObjectId(aclClass,domainObjectInstance.id)
        AclEntry aclEntry = AclEntry.findByAclObjectIdentity(aclObjectIdentity)
        aclEntry.sid = role.authority
        aclEntry.save(flush: true)
    }
}
