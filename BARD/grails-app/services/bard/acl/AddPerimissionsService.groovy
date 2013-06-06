package bard.acl

import grails.plugins.springsecurity.SpringSecurityService
import org.grails.plugins.springsecurity.service.acl.AclService
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.model.Permission

class AddPerimissionsService {

    static transactional = false

    def aclPermissionFactory
    AclService aclService
    AclUtilService aclUtilService
    SpringSecurityService springSecurityService
    //role here could be Team or Role
    void addPermission(domainObject, role, int permission) {
        addPermission(domainObject, role,
                aclPermissionFactory.buildFromMask(permission))
    }
    //role here could be Team or Role
    void addPermission(domainObject, role,
                       Permission permission) {
        aclUtilService.addPermission(domainObject,role.authority, permission)
    }


}
