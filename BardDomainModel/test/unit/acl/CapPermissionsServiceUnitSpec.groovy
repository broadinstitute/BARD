/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        0 * service.springSecurityService.getPrincipal() >> {new BardUser(username: username)}

        assert assay


    }
}
class MockedCapPermissionCategory {

    static void addPermission(Assay domainObjectInstance, Role role, Permission permission) {

    }

}
