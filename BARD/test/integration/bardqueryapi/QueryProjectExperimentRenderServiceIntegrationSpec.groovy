package bardqueryapi

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import spock.lang.Ignore

@Unroll
class QueryProjectExperimentRenderServiceIntegrationSpec extends IntegrationSpec {

    QueryProjectExperimentRenderService queryProjectExperimentRenderService

    void "test constructGraph #label"() {

        when:
        final Map result = queryProjectExperimentRenderService.constructGraph(pid)
        then:
        assert result

        assert result.connectedNodes
        assert result.edges
        assert result.isolatedNodes.size() >= 0

        where:
        label                   | pid
        "With few nodes"        | 3
        "With a lot of nodes"   | 2
        "With our flag project" | 75
    }
    /**
     * This PID should have isolated nodes, but the REST API does not yet support Isolated nodes
     */
    @Ignore
    void "test constructGraphWithSomeIsolatedNodes #label"() {

        when:
        final Map result = queryProjectExperimentRenderService.constructGraph(pid)
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
