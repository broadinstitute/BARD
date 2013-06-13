package acl

import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

class CapPermissionService {

    AclUtilService aclUtilService
    SpringSecurityService springSecurityService

    void addPermission(domainObjectInstance) {
        //get the new Object role for the current user
        //TODO: Waiting for service that creates the newUserObject in person table
        /*
        * String userName = springSecurityService.principal?.username
        * Role role = findRoleByUserName(userName)
        * addPermission(domainObjectInstance, role,BasePermission.ADMINISTRATION)
        */
        addPermission(domainObjectInstance, new Role(authority: "ROLE_BROAD_INSTITUTE"), BasePermission.ADMINISTRATION)
    }

    protected void addPermission(domainObjectInstance, Role role, Permission permission) {
        aclUtilService.addPermission(domainObjectInstance, role.authority, permission)
    }
}
