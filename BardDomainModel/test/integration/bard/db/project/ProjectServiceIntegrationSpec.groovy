package bard.db.project

import bard.db.BardIntegrationSpec
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
            project1(Project, name: 'project1')
            for (int i in 1..2) {
                "extRef${i}"(ExternalReference, extAssayRef: "aid=-${i}", project: project1)
            }

            project2(Project, name: 'project2')
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