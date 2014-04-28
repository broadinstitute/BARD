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

package bard.db.project

import bard.db.BardIntegrationSpec
import bard.db.people.Role
import bard.db.registration.ExternalReference
import project.ProjectService
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ProjectServiceIntegrationSpec extends BardIntegrationSpec {

    ProjectService projectService
    def fixtureLoader

    void "test findProjectByPubChemAid with fixtures #label"() {

        given:
        def fixture = fixtureLoader.build {
            role1(Role, authority:"ROLE_1",displayName:'ROLE_1')
            project1(Project, name: 'project1', ownerRole:role1)
            for (int i in 1..2) {
                "extRef${i}"(ExternalReference, extAssayRef: "aid=-${i}", project: project1)
            }

            project2(Project, name: 'project2',ownerRole:role1)
            extRef3(ExternalReference, extAssayRef: 'aid=-1', project: project2)
        }

        when:
        List<Project> foundProjects = projectService.findProjectByPubChemAid(aid)

        then:
        assert foundProjects*.name.sort() == expectedProjectNames

        where:
        label                                          | aid       | expectedProjectNames
        'find an PID with two AIDs associated with it' | -2        | ['project1']
        'find a non-exiting aid'                       | 123456789 | []
        'find an exiting aid associated with two PIDs' | -1        | ['project1', 'project2']
    }
}
