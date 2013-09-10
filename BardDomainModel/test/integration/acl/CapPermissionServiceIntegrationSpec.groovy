package acl

import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.registration.Assay
import grails.plugin.spock.IntegrationSpec
import grails.plugins.springsecurity.SpringSecurityService
import groovy.transform.TypeChecked
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.IgnoreRest
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
        desc                                    | numberOfEntities | username              | domainClass | buildClosure
        'no user no assays'                     | 0                | null                  | Assay       | {}
        'authenticated user with 0 assays'      | 0                | 'integrationTestUser' | Assay       | {}
        'authenticated user with 1 assay'       | 1                | 'integrationTestUser' | Assay       | { Assay.build() }
        'authenticated user with 2 assays'      | 2                | 'integrationTestUser' | Assay       | { Assay.build() }
        'no user no                projects'    | 0                | null                  | Project     | {}
        'authenticated user with 0 projects'    | 0                | 'integrationTestUser' | Project     | {}
        'authenticated user with 1 projects'    | 1                | 'integrationTestUser' | Project     | { Project.build() }
        'authenticated user with 2 projects'    | 2                | 'integrationTestUser' | Project     | { Project.build() }
        'no user no                experiments' | 0                | null                  | Experiment  | {}
        'authenticated user with 0 experiments' | 0                | 'integrationTestUser' | Experiment  | {}
        'authenticated user with 1 experiments' | 1                | 'integrationTestUser' | Experiment  | { Experiment.build() }
        'authenticated user with 2 experiments' | 2                | 'integrationTestUser' | Experiment  | { Experiment.build() }
    }
}
