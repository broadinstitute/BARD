package bardqueryapi

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import spock.lang.Ignore

@Unroll
class ProjectExperimentRenderServiceIntegrationSpec extends IntegrationSpec {

    ProjectExperimentRenderService projectExperimentRenderService

    @Ignore
    void "test constructGraph"() {

        when:
        final Map result = projectExperimentRenderService.constructGraph(pid)
        then:
        assert result

        assert result.connectedNodes
        assert result.edges
        assert result.isolatedNodes.size() >= 0

        where:
        label                 | pid
        "With few nodes"      | 1944
        "With a lot of nodes" | 1963
    }
    /**
     * This PID should have isolated nodes, but the REST API does not yet support Isolated nodes
     */
    @Ignore
    void "test constructGraphWithSomeIsolatedNodes"() {

        when:
        final Map result = projectExperimentRenderService.constructGraph(pid)
        then:
        assert result

        assert result.connectedNodes
        assert result.edges
        assert result.isolatedNodes.size() >= 0

        where:
        label                 | pid
        "With a lot of nodes" | 1759
    }

}
