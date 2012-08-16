package bardqueryapi

import grails.plugin.spock.IntegrationSpec

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
class QueryProjectApiServiceIntegrationSpec extends IntegrationSpec {
    QueryProjectApiService queryProjectApiService

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testFindProjects() {

        when:
        final def projects = queryProjectApiService.findProjects()
        then:
        assert projects
        projects.each {project ->
            println project
        }
    }

    void testFindProject() {
        given:
        final String projectUrl = "/projects/1772"
        when:
        final def project = queryProjectApiService.findProject(projectUrl)
        then:
        assert project
        println project
    }

    void testFindProteinTargetsByProject() {
        given:
        final String projectUrl = "/projects/1772"
        when:
        final def project = queryProjectApiService.findProteinTargetsByProject(projectUrl)
        then:
        assert project
        println project
    }

    void testFindProbesByProject() {
        given:
        final String projectUrl = "/projects/1772"
        when:
        final def probes = queryProjectApiService.findProbesByProject(projectUrl)
        then:
        assert probes
        probes.each {probe->
            println probe
        }
    }
    void testFindAssaysByProject(){
        given:
        final String projectUrl = "/projects/1772"
        when:
        final def assays = queryProjectApiService.findAssaysByProject(projectUrl)
        then:
        assert assays
        assays.each {assay->
            println assay
        }
    }

}
