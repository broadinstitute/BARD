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
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll
import util.BardUser

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Build([Assay, Role, Person, PersonRole])
@TestFor(CapPermissionService)
@Unroll
class CapPermissionsServiceUnitSpec extends Specification {
    SpringSecurityService springSecurityService

    def setup() {
        AclUtilService aclUtilService = Mock(AclUtilService)
        springSecurityService = Mock(SpringSecurityService)
        service.aclUtilService = aclUtilService
        service.springSecurityService = springSecurityService
    }

    def cleanup() {
    }

    void "addPermission - three args method"() {
        given:
        Assay assay = new Assay();//Assay.build(capPermissionService: service)
        Role role = Role.build()
        Permission permission = Mock(Permission)
        when:
        service.addPermission(assay, role, permission)
        then:
        assert assay


    }


    void "addPermission - single args method"() {
        given:
        String username = "user"
        Permission permission = Mock(Permission)
        Role role = Role.build(authority: "ROLE_TEAM_Y", displayName: "ROLE Y")
        Assay assay = new Assay(ownerRole: role)
        service.springSecurityService = Mock(SpringSecurityService)
        use(MockedCapPermissionCategory) {
            service.addPermission(assay, role, permission)
        }
        when:
        service.addPermission(assay)
        then:
        0 * service.springSecurityService.getPrincipal() >> {new BardUser(username: username, owningRole:role)}

        assert assay


    }
}
class MockedCapPermissionCategory {

    static void addPermission(Assay domainObjectInstance, Role role, Permission permission) {

    }

}
