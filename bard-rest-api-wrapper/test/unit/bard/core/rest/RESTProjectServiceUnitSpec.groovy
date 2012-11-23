package bard.core.rest

import bard.core.DataSource
import bard.core.Project
import com.fasterxml.jackson.databind.ObjectMapper
import jdo.JSONNodeTestHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RESTProjectServiceUnitSpec extends Specification {
    RESTAssayService restAssayService
    RESTProjectService restProjectService

    @Shared ObjectMapper mapper = new ObjectMapper();
    @Shared String PROJECT_EXPANDED_SEARCH_RESULTS = JSONNodeTestHelper.PROJECT_EXPANDED_SEARCH_RESULTS
    @Shared String PROBES_NODE = JSONNodeTestHelper.PROBES
    @Shared String CAP_ANNOTATIONS = JSONNodeTestHelper.CAP_ANNOTATIONS

    @Shared String PROJECT_NODE = '''
 {
           "proj_id": "179",
           "name": "Probe Development Summary for Inhibitors of Bloom's syndrome helicase (BLM)",
           "highlight": " develop resistance to therapy through enhanced activity of DNA repair"
       }
        '''

    void setup() {
        this.restProjectService = new RESTProjectService("base")
        this.restAssayService = new RESTAssayService("base")
    }

    void tearDown() {
        // Tear down logic here
    }

    void "getEntitySearch #label"() {
        when:
        final Project resultProject = this.restProjectService.getEntitySearch(project, node)

        then:
        assert resultProject
        assert resultProject.getId() == 179
        where:
        label                 | node                          | project
        "Project is not null" | mapper.readTree(PROJECT_NODE) | new Project()
        "Project is null"     | mapper.readTree(PROJECT_NODE) | null
    }

    void "getEntity #label"() {
        when:
        final Project resultProject = this.restProjectService.getEntity(project, node)

        then:
        assert resultProject
        assert resultProject.getId() == 17
        where:
        label                 | node                                             | project
        "Project is not null" | mapper.readTree(PROJECT_EXPANDED_SEARCH_RESULTS) | new Project()
        "Project is null"     | mapper.readTree(PROJECT_EXPANDED_SEARCH_RESULTS) | null
    }

    void "addProjectIdNode #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProjectIdNode(project, node)
        then:
        assert project
        where:
        label                          | node
        "'projectId' key in JSON Node" | mapper.readTree("{ \"projectId\": 17}")
    }

    void "addProjectIdNode with Exception #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProjectIdNode(project, node)
        then:
        thrown(IllegalArgumentException)
        where:
        label                             | node
        "No 'projectId' key in JSON Node" | mapper.readTree("{ \"NotprojectId\": 17}")
    }

    void "addProjIdNode #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProjIdNode(project, node)
        then:
        assert project
        where:
        label                       | node
        "'projId' key in JSON Node" | mapper.readTree("{ \"proj_id\": 17}")
    }

    void "addProjIdNode with Exception #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProjIdNode(project, node)
        then:
        thrown(IllegalArgumentException)
        where:
        label                          | node
        "No 'projId' key in JSON Node" | mapper.readTree("{ \"NoprojId\": 17}")
    }

    void "addExperimentCountNode #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addExperimentCountNode(project, node)
        then:
        assert project.getValues().isEmpty() == expectedResult
        where:
        label                                   | node                                          | expectedResult
        "'experimentCount' key in JSON Node"    | mapper.readTree("{ \"experimentCount\": 17}") | false
        "No 'experimentCount' key in JSON Node" | mapper.readTree("{ \"someName\": 17}")        | true
    }

    void "addProjectHighlightNode #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProjectHighlightNode(project, node)
        then:
        assert project.getValues().isEmpty() == expectedResult
        where:
        label                             | node                                          | expectedResult
        "'highlight' key in JSON Node"    | mapper.readTree("{ \"highlight\": \"high\"}") | false
        "No 'highlight' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}")  | true
    }

    void "addCAPannotations(final Project project, final JsonNode node) #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addCAPannotations(project, node)
        then:
        assert project.getValues().isEmpty() == expectedResult
        where:
        label                                 | node                                         | expectedResult
        "'ak_dict_label' key in JSON Node"    | mapper.readTree(CAP_ANNOTATIONS)             | false
        "No 'ak_dict_label' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}") | true

    }

    void "addSingleArrayNodeAnnotation"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addSingleArrayNodeAnnotation(project, null, null, null)
        then:
        assert project.getValues().isEmpty()
    }

    void "addSingleAnnotation #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addSingleAnnotation(project, new DataSource("name"), key, value)
        then:
        assert project.getValues().isEmpty() == expectedResult
        where:
        label                    | key   | value   | expectedResult
        "Has Key and Value"      | "key" | "value" | false
        "Has Key but no Value"   | "key" | null    | true
        "Has Value but no Key"   | ""    | "value" | true
        "But Value and Key Null" | ""    | ""      | true

    }

    void "addProbes #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProbes(project, node)
        then:
        assert project.getProbes().size() == expectedResult
        where:
        label                          | node                                         | expectedResult
        "'probes' key in JSON Node"    | mapper.readTree(PROBES_NODE)                 | 2
        "No 'probes' key in JSON Node" | mapper.readTree("{ \"someName\": \"high\"}") | 0
    }

    void "addProjectNameNode #label"() {
        given:
        final Project project = new Project()
        when:
        this.restProjectService.addProjectNameNode(project, node)
        then:
        assert project.getName() == expectedName
        where:
        label                        | node                                        | expectedName
        "'name' key in JSON Node"    | mapper.readTree("{ \"name\": \"name\"}")    | "name"
        "No 'name' key in JSON Node" | mapper.readTree("{ \"someName\":\"name\"}") | null
    }

}

