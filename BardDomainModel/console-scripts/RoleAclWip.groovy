import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.grails.plugins.springsecurity.service.acl.AclService
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.domain.BasePermission

/**
ctx.expressionHandler.permissionEvaluator
ctx.permissionEvaluator

def aclClass = new org.codehaus.groovy.grails.plugins.springsecurity.acl.AclClass(className: 'bard.db.people.Role')
aclClass.save(flush:true)
 */


SpringSecurityService springSecurityService = ctx.springSecurityService
SpringSecurityUtils.doWithAuth('durkin.dan@gmail.com'){
    springSecurityService.getAuthentication()
    AclUtilService aclUtilService = ctx.aclUtilService
    aclUtilService.addPermission(Role, 18L, springSecurityService.getAuthentication(),BasePermission.ADMINISTRATION  )
}



if (username) {
    allRolesAndUserName.addAll(SpringSecurityUtils.getPrincipalAuthorities()*.getAuthority())
    allRolesAndUserName.add(username)

    return findAllByRolesAndClass(allRolesAndUserName, domainClass)
} else {
    return []
}