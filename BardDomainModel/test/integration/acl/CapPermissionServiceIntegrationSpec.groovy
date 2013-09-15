package acl

import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.registration.Assay
import grails.plugin.spock.IntegrationSpec
import grails.plugins.springsecurity.SpringSecurityService
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/10/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class CapPermissionServiceIntegrationSpec extends IntegrationSpec {

    CapPermissionService capPermissionService
    SpringSecurityService springSecurityService


    void cleanup() {
        // remove authenticated user from context to clean up
        SecurityContextHolder.clearContext();
    }


    void "test getOwner #desc"() {
        given: 'a logged in user creates an assay'
        springSecurityService.reauthenticate(username)

        Assay assay = Assay.build()
        assay.save(flush: true)

        when:
        String actualOwner = capPermissionService.getOwner(assay)

        then:
        actualOwner == expectedOwner

        where:
        desc                             | username              | expectedOwner
        'owner is integration test user' | 'integrationTestUser' | 'integrationTestUser'
        'owner is teamMember1'           | 'teamA_1'             | 'ROLE_TEAM_A'
        'owner is teamMember2'           | 'teamA_2'             | 'ROLE_TEAM_A'
    }



    void "test find domain instances when : #desc"() {
        final List builtInstances = []

        given:
        SecurityContextHolder.clearContext();

        if (username) {
            springSecurityService.reauthenticate(username)
            numberOfEntities.times { builtInstances.add(buildClosure.call()) }
        }

        when:
        List foundInstances = capPermissionService.findAllObjectsForRoles(domainClass)

        then:
        assert 1 == 1
        assert foundInstances.size() == numberOfEntities
        assert foundInstances.containsAll(builtInstances)

        where:
        desc                                    | numberOfEntities | username  | domainClass | buildClosure
        'no user no assays'                     | 0                | null      | Assay       | {}
        'authenticated user with 0 assays'      | 0                | 'teamA_1' | Assay       | {}
        'authenticated user with 1 assay'       | 1                | 'teamA_1' | Assay       | { Assay.build() }
        'authenticated user with 2 assays'      | 2                | 'teamA_1' | Assay       | { Assay.build() }
        'no user no                projects'    | 0                | null      | Project     | {}
        'authenticated user with 0 projects'    | 0                | 'teamA_1' | Project     | {}
        'authenticated user with 1 projects'    | 1                | 'teamA_1' | Project     | { Project.build() }
        'authenticated user with 2 projects'    | 2                | 'teamA_1' | Project     | { Project.build() }
        'no user no                experiments' | 0                | null      | Experiment  | {}
        'authenticated user with 0 experiments' | 0                | 'teamA_1' | Experiment  | {}
        'authenticated user with 1 experiments' | 1                | 'teamA_1' | Experiment  | { Experiment.build() }
        'authenticated user with 2 experiments' | 2                | 'teamA_1' | Experiment  | { Experiment.build() }
    }

    void "test find domain instances for teams when : #desc"() {
        final List builtInstances = []

        given: 'a team member with Person.newObjecRole set to a team'
        SecurityContextHolder.clearContext();
        springSecurityService.reauthenticate(teamMember1)


        when: '1 team member creates entities'
        num.times { builtInstances.add(buildClosure.call()) }

        assert springSecurityService.principal.username == teamMember1
        final List foundInstancesForTeamMember1 = capPermissionService.findAllObjectsForRoles(domainClass)

        then: 'the instances are found for that user'
        assert foundInstancesForTeamMember1.containsAll(builtInstances)
        assert foundInstancesForTeamMember1.size() == num

        when: 'another team member authenticates'
        springSecurityService.reauthenticate(teamMember2)
        assert springSecurityService.principal.username == teamMember2
        final List foundInstancesForTeamMember2 = capPermissionService.findAllObjectsForRoles(domainClass)

        then: 'the other team member should also see the entities'
        assert foundInstancesForTeamMember2.containsAll(builtInstances)
        assert foundInstancesForTeamMember2.size() == num

        where:
        desc                                    | num | teamMember1 | teamMember2 | domainClass | buildClosure
        'authenticated user with 1 assay'       | 1   | 'teamA_1'   | 'teamA_2'   | Assay       | { Assay.build() }
        'authenticated user with 2 assays'      | 2   | 'teamA_1'   | 'teamA_2'   | Assay       | { Assay.build() }
        'authenticated user with 1 projects'    | 1   | 'teamA_1'   | 'teamA_2'   | Project     | { Project.build() }
        'authenticated user with 2 projects'    | 2   | 'teamA_1'   | 'teamA_2'   | Project     | { Project.build() }
        'authenticated user with 1 experiments' | 1   | 'teamA_1'   | 'teamA_2'   | Experiment  | { Experiment.build() }
        'authenticated user with 2 experiments' | 2   | 'teamA_1'   | 'teamA_2'   | Experiment  | { Experiment.build() }
    }
}
