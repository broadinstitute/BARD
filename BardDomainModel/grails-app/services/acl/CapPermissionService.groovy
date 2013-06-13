package acl

import bard.db.people.Person
import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.Permission

class CapPermissionService {

    AclUtilService aclUtilService
    SpringSecurityService springSecurityService

    void addPermission(domainObjectInstance) {
        String userName = springSecurityService.principal?.username
        Person person = Person.findByUserName(userName)
        final Role newObjectRole = person.newObjectRole
        //we assume that the newObjectRole should never be null. There will be a check constraint to insure that
        addPermission(domainObjectInstance, newObjectRole, BasePermission.ADMINISTRATION)
    }

    protected void addPermission(domainObjectInstance, Role role, Permission permission) {
        aclUtilService.addPermission(domainObjectInstance, role.authority, permission)
    }
}
