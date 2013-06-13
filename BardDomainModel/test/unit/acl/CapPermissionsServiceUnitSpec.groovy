package acl

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import bard.db.registration.Assay
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.springframework.security.acls.model.Permission
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Build([Assay, Role, Person, PersonRole])
@TestFor(CapPermissionService)
@Unroll
class CapPermissionsServiceUnitSpec extends Specification {

    def setup() {
        AclUtilService aclUtilService = Mock(AclUtilService)
        SpringSecurityService springSecurityService = Mock(SpringSecurityService)
        service.aclUtilService = aclUtilService
        service.springSecurityService = springSecurityService
    }

    def cleanup() {
    }

    void "addPermission - three args method"() {
        given:
        Assay assay = Assay.build()
        assay.capPermissionService = service
        Role role = Role.build()
        Permission permission = Mock(Permission)
        //addPermission(domainObjectInstance, String username, int permission) {
        when:
        service.addPermission(assay, role, permission)
        then:
        service.aclUtilService.addPermission(assay, role.authority,permission)
        assert assay


    }
//    @Ignore //TODO: Not yet ready
//    void "addPermission - two args method"() {
//        given:
//        Person.metaClass.findByUserName.create = { String userName ->
//            new Role(authority: "ROLE_Y")
//            //Some logic here
//        }
//        String user = "user"
//        Person person = Person.build(userName: user)
//        Role role = Role.build()
//        PersonRole.create(person, role, "me", false)
//        Assay assay = Assay.build()
//        //TODO: add the role here
//        service.springSecurityService = Mock(SpringSecurityService)
//        service.aclPermissionFactory = Mock(PermissionFactory)
//
//        int permission = 1
//        when:
//        service.addPermission(assay, permission)
//        then:
//        service.springSecurityService.principal?.username >> {user}
//        service.aclUtilService.addPermission(assay, role,1)
//        assert assay
//
//
//    }
}
//@Build([Person, Role, PersonRole])
//@Mock([Person, Role, PersonRole])
//@TestFor(BardAuthorizationProviderService)
//@Unroll
//class BardAuthorizationProviderServiceSpec extends Specification {