package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    RESTCompoundService restCompoundService
    RESTProjectService restProjectService
    RESTAssayService restAssayService
    RESTExperimentService restExperimentService
    QueryHelperService queryHelperService
    QueryServiceWrapper queryServiceWrapper

    void setup() {
        restCompoundService = Mock(RESTCompoundService.class)
        restProjectService = Mock(RESTProjectService.class)
        restAssayService = Mock(RESTAssayService.class)
        restExperimentService = Mock(RESTExperimentService.class)
        queryServiceWrapper = Mock(QueryServiceWrapper.class)
        queryHelperService = Mock(QueryHelperService.class)
        service.queryHelperService = queryHelperService
        service.queryServiceWrapper = queryServiceWrapper

    }

    void tearDown() {
        // Tear down logic here
    }

    //  /**
//     */
//    void "test handleAutoComplete #label"() {
//
//        when:
//        final List<String> response = service.autoComplete()
//
//        then:
//        assert response == expectedResponse
//
//        where:
//        label                       | term  | jsonResponse                        | expectedResponse
//        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
//        "Empty String"              | ""    | new JSONObject()                    | []
//    }
    /**
     */
    void "test Show Compound #label"() {

        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = service.showCompound(compoundId)
        then: "The CompoundDocument is called"
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        this.queryServiceWrapper.getRestCompoundService().get(_) >> {compound}
        if (compound) {
            assert compoundAdapter
            assert compoundAdapter.compound
        } else {
            assert !compoundAdapter
        }

        where:
        label                       | compoundId       | compound
        "Return a Compound Adapter" | new Integer(872) | new Compound(name: "C1")
        "Unknown Compound"          | new Integer(872) | null
        "Null CompoundId"           | null             | null
    }



    void "test Show Project"() {
        when: "Client enters a project ID and the showProject method is called"
        ProjectAdapter foundProjectAdpater = service.showProject(projectId)
        then: "The Project document is displayed"
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
        restProjectService.get(_) >> {project}
        if (project) {
            assert foundProjectAdpater
            assert foundProjectAdpater.project
        } else {
            assert !foundProjectAdpater
        }

        where:
        label                      | projectId        | project
        "Return a Project Adapter" | new Integer(872) | new Project(name: "C1")
        "Unknown Project"          | new Integer(872) | null
        "Null projectId"           | null             | null
    }

    void "test Show Assay"() {
        when: "Client enters a assay ID and the showAssay method is called"
        AssayAdapter foundAssayAdapter = service.showAssay(assayId)
        then: "The Assay document is displayed"
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        restAssayService.get(_) >> {assay}
        if (assay) {
            assert foundAssayAdapter
            assert foundAssayAdapter.assay
        } else {
            assert !foundAssayAdapter
        }
        where:
        label                     | assayId          | assay
        "Return an Assay Adapter" | new Integer(872) | new Assay(name: "C1")
        "Unknown Assay"           | new Integer(872) | null
        "Null assayId"            | null             | null

    }

    void "test findCompoundsByCIDs #label"() {
        when:
        Map responseMap = service.findCompoundsByCIDs(cids)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        expectedNumberOfCalls * restCompoundService.get(_) >> {compound}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == expectedNumberOfHits
        where:
        label                    | cids                           | compound                                             | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Compound Ids"  | [new Long(872), new Long(111)] | [new Compound(name: "C1"), new Compound(name: "C2")] | 1                     | 2
        "Unknown Compound Id"    | [new Long(802)]                | null                                                 | 1                     | 0
        "Single Compound Id"     | [new Long(872)]                | [new Compound(name: "C1")]                           | 1                     | 1
        "Empty Compound Id list" | []                             | null                                                 | 0                     | 0

    }

    void "test findAssaysByPIDs #label"() {
        when:
        Map responseMap = service.findAssaysByADIDs(assayIds)
        then:
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        queryHelperService.assaysToAdapters(_) >> {assayAdapters}
        expectedNumberOfCalls * restAssayService.get(_) >> {assay}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == expectedNumberOfHits
        where:
        label                 | assayIds                       | assay                                          | expectedNumberOfCalls | expectedNumberOfHits | assayAdapters
        "Multiple Assay Ids"  | [new Long(872), new Long(111)] | [new Assay(name: "C1"), new Assay(name: "C2")] | 1                     | 2                    | [new AssayAdapter(new Assay(name: "C1")), new AssayAdapter(new Assay(name: "C2"))]
        "Unknown Assay Id"    | [new Long(802)]                | null                                           | 1                     | 0                    | null
        "Single Assay Id"     | [new Long(872)]                | [new Assay(name: "C1")]                        | 1                     | 1                    | [new AssayAdapter(new Assay(name: "C1"))]
        "Empty Assay Id list" | []                             | null                                           | 0                     | 0                    | null

    }

    void "test findProjectsByPIDs #label"() {
        when:
        Map responseMap = service.findProjectsByPIDs(projectIds)
        then:
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
        queryHelperService.projectsToAdapters(_) >> {projectAdapters}
        expectedNumberOfCalls * restProjectService.get(_) >> {project}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == expectedNumberOfHits
        where:
        label                   | projectIds                     | project                                            | expectedNumberOfCalls | expectedNumberOfHits | projectAdapters
        "Multiple Project Ids"  | [new Long(872), new Long(111)] | [new Project(name: "C1"), new Project(name: "C2")] | 1                     | 2                    | [new ProjectAdapter(new Project(name: "C1")), new ProjectAdapter(new Project(name: "C2"))]
        "Unknown Project Id"    | [new Long(802)]                | null                                               | 1                     | 0                    | null
        "Single Project Id"     | [new Long(872)]                | [new Project(name: "C1")]                          | 1                     | 1                    | [new ProjectAdapter(new Project(name: "C1"))]
        "Empty Project Id list" | []                             | null                                               | 0                     | 0                    | null

    }

    void "test Structure Search #label"() {
        given:
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        when:
        service.structureSearch(smiles, structureSearchParamsType)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }

        restCompoundService.structureSearch(_) >> {iter}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }
}
