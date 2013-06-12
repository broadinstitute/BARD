package bard.acl

import bard.db.people.Person
import bard.db.people.Role
import bard.db.project.UserFixableException
import grails.plugins.springsecurity.SpringSecurityService
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.model.Permission

class AddPermissionsService {
    PermissionFactory aclPermissionFactory
    AclUtilService aclUtilService
    SpringSecurityService springSecurityService
    void addPermission(domainObjectInstance) {
        //get the new Object role for the current user
        //TODO: Waiting for service that creates the newUserObject in person table
        /*String userName = springSecurityService.principal?.username
        Role role = Person.findRoleByUserName(userName) //TODO: fix query when phil checks in acl stuff
        addPermission(domainObjectInstance, role,BasePermission.ADMINISTRATION)*/
    }
    void addPermission(domainObjectInstance,Role role,  Permission permission) {
        aclUtilService.addPermission(domainObjectInstance, role.authority,permission)
    }
}
