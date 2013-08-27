package querycart

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.IQueryService
import bardqueryapi.QueryService
import bard.core.adapter.ProjectAdapter

import spock.lang.Shared
import bard.core.rest.spring.project.Project
/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartProjectService)
@Unroll
class CartProjectServiceUnitSpec extends Specification {

    IQueryService queryService
    @Shared ProjectAdapter projectAdapter = new ProjectAdapter(new Project(name: "name1", bardProjectId: 1L))

    void setup() {
        queryService = Mock(QueryService)
        service.queryService = this.queryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test createCartProjectFromPID #label"() {
        when:
        CartProject returnedCartProject = service.createCartProjectFromPID(pid)

        then:
        1 * queryService.findProjectsByPIDs([pid]) >> {[projectAdapters: projectAdapters]}
        assert returnedCartProject?.name == expectedCartProjectName
        assert returnedCartProject?.externalId == expectedCartProjectId

        where:
        label                     | pid  | projectAdapters       | expectedCartProjectName | expectedCartProjectId
        "found a projectAdapter"  | 123L | [this.projectAdapter] | 'name1'                 | 1
        "no projectAdapter found" | 123L | []                    | null                    | null
    }
}
