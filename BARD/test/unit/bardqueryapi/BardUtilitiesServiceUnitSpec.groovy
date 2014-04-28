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

package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import spock.lang.Unroll
import grails.plugins.springsecurity.SpringSecurityService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(BardUtilitiesService)
class BardUtilitiesServiceUnitSpec extends Specification {
    SpringSecurityService springSecurityService = Mock(SpringSecurityService)


    void "test getUsername"() {
        given:
        String currentUser = "TEST"
        service.springSecurityService = springSecurityService
        when:
        String username = service.getUsername()
        then:
        springSecurityService.getPrincipal() >> {return new MockPrincipal(currentUser)}
        assert username == currentUser
    }

    void "test getUsername no principal"() {
        given:
        service.springSecurityService = springSecurityService
        when:
        String username = service.getUsername()
        then:
        springSecurityService.getPrincipal() >> {null}
        assert !username
    }

    void "test getUsername No User"() {
        given:
        service.springSecurityService = springSecurityService
        when:
        String username = service.getUsername()
        then:
        springSecurityService.getPrincipal() >> {"dummy"}
        assert !username
    }
}
class MockPrincipal {
    String username

    MockPrincipal(String username) {
        this.username = username
    }

    boolean hasProperty() {return true}
}
