package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll
import wslite.json.JSONArray

import javax.servlet.http.HttpServletResponse

import bard.core.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
@Unroll
class BardWebInterfaceControllerUnitSpec extends Specification {

    QueryService queryService
    ShoppingCartService shoppingCartService


    void setup() {
        controller.metaClass.mixin(SearchHelper)
        queryService = Mock(QueryService)
        controller.queryService = this.queryService
        shoppingCartService = Mock(ShoppingCartService)
        controller.shoppingCartService = this.shoppingCartService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test handle Assay Searches #label"() {
        given:
        final List<SearchFilter> filters = [new SearchFilter(filterName: "a", filterValue: "b")]

        when:
        params.skip = "0"
        params.top = "10"
        controller.handleAssaySearches(this.queryService, searchString, filters)
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _, _) >> {assayAdapterMap}
        assert response.status == statusCode

        where:
        label                                  | searchString  | statusCode                                   | assayAdapterMap
        "Empty Search String - Bad Request"    | ""            | HttpServletResponse.SC_BAD_REQUEST           | null
        "Search String- Internal Server Error" | "Some String" | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"                              | "1234,5678"   | HttpServletResponse.SC_OK                    | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]


    }

    void "test handle Project Searches #label"() {
        final List<SearchFilter> filters = [new SearchFilter(filterName: "a", filterValue: "b")]

        when:
        params.skip = "0"
        params.top = "10"
        controller.handleProjectSearches(this.queryService, searchString, filters)
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _, _) >> {projectAdapterMap}
        assert response.status == statusCode

        where:
        label                                  | searchString  | statusCode                                   | projectAdapterMap
        "Empty Search String"                  | ""            | HttpServletResponse.SC_BAD_REQUEST           | null
        "Search String- Internal Server Error" | "Some String" | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"                              | "1234,5678"   | HttpServletResponse.SC_OK                    | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test handle Compound Searches #label"() {
        final List<SearchFilter> filters = [new SearchFilter(filterName: "a", filterValue: "b")]

        when:
        params.skip = "0"
        params.top = "10"
        controller.handleCompoundSearches(this.queryService, searchString, filters)
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _, _) >> {compoundAdapterMap}
        assert response.status == statusCode

        where:
        label                                  | searchString  | statusCode                                   | compoundAdapterMap
        "Empty Search String"                  | ""            | HttpServletResponse.SC_BAD_REQUEST           | null
        "Search String- Internal Server Error" | "Some String" | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"                              | "1234,5678"   | HttpServletResponse.SC_OK                    | [compoundAdapters: [buildCompoundAdapter(1234, [], "CC"), buildCompoundAdapter(4567, [], "CCC")], facets: [], nHits: 2]
    }

    void "test apply Filters to projects #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.ProjectFacetForm.toString()
        Map paramMap = [formName: FacetFormType.ProjectFacetForm.toString(), searchString: searchString, filters: [new SearchFilter(filterName: "a", filterValue: "b")]]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        controller.applyFilters(searchCommand)
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _, _) >> {projectAdapterMap}
        assert response.status == statusCode
        where:
        label                 | searchString | statusCode                         | projectAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]
    }

    void "test apply Filters to compounds #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.CompoundFacetForm.toString(), searchString: searchString, filters: [new SearchFilter(filterName: "a", filterValue: "b")]]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        controller.applyFilters(searchCommand)
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _, _) >> {compoundAdapterMap}
        assert response.status == statusCode
        where:
        label                 | searchString | statusCode                         | compoundAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [compoundAdapters: [buildCompoundAdapter(1234, [], "CC"), buildCompoundAdapter(4567, [], "CCC")], facets: [], nHits: 2]
    }

    void "test apply Filters to assays #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.AssayFacetForm.toString()
        Map paramMap = [formName: FacetFormType.AssayFacetForm.toString(), searchString: searchString, filters: [new SearchFilter(filterName: "a", filterValue: "b")]]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        controller.applyFilters(searchCommand)
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _, _) >> {assayAdapterMap}
        assert response.status == statusCode
        where:
        label                 | searchString | statusCode                         | assayAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]
    }

    void "test handle search Params #label"() {
        given:
        params.searchString = searchString
        if (offset) {
            params.offset = offset
        }
        if (max) {
            params.max = max
        }
        when:
        Map map = controller.handleSearchParams()
        then:
        map.skip == expectedSkip
        map.top == expectedTop
        where:
        label                 | searchString | offset | max  | expectedSkip | expectedTop
        "Empty Search String" | ""           | "0"    | "10" | 0            | 10
        "No offset Param"     | "1234,5678"  | ""     | "10" | 0            | 10
        "No max Param"        | "1234,5678"  | "10"   | ""   | 10           | 10
        "All Params"          | "1234,5678"  | "10"   | "10" | 10           | 10

    }

    void "test search String To Id List #label"() {
        when:
        List<Long> results = controller.searchStringToIdList(searchString)
        then:
        results.size() == expectedResults.size()
        for (int index = 0; index < expectedResults.size(); index++) {
            assert results.get(index) as Long == expectedResults.get(index);
        }
        where:
        label                                            | searchString              | expectedResults
        "Single id"                                      | "1234"                    | [new Long(1234)]
        "Multiple ids, comma separated with some spaces" | "1234, 5678, 898,    445" | [new Long(1234), new Long(5678), new Long(898), new Long(445)]
        "Two ids with spaces"                            | "1234,        5678"       | [new Long(1234), new Long(5678)]

    }

    void "test free text search assays #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchAssays()
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _, _) >> {assayAdapterMap}
        and:
        response.status == statusCode
        where:
        label                 | searchString | statusCode                         | assayAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test free text search projects #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchProjects()
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _, _) >> {projectAdapterMap}
        and:
        response.status == statusCode
        where:
        label                 | searchString | statusCode                         | projectAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test free text search compounds #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchCompounds()
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _, _) >> {compoundAdapterMap}
        and:
        response.status == statusCode
        where:
        label                 | searchString | statusCode                         | compoundAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [compoundAdapters: [buildCompoundAdapter(1234, [], "CC"), buildCompoundAdapter(4567, [], "CCC")], facets: [], nHits: 2]

    }

    void "test structure search #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchStructures()
        then:
        _ * this.queryService.structureSearch(_, _) >> {compoundAdapterMap}
        and:
        response.redirectedUrl == expectedRedirectURL
        flash.message == flashMessage
        response.status == statusCode
        where:
        label                 | searchString | expectedRedirectURL          | flashMessage                                                               | statusCode                               | compoundAdapterMap
        "Empty Search String" | ""           | '/bardWebInterface/homePage' | 'Search String is required must be of the form StructureSearchType:Smiles' | HttpServletResponse.SC_MOVED_TEMPORARILY | null
        "Throws Exception"    | "1234,5678"  | '/bardWebInterface/homePage' | 'Search String is required must be of the form StructureSearchType:Smiles' | HttpServletResponse.SC_MOVED_TEMPORARILY | null
        "Success"             | "Exact:CCC"  | null                         | null                                                                       | HttpServletResponse.SC_OK                | [compoundAdapters: [buildCompoundAdapter(1234, [], "CC"), buildCompoundAdapter(4567, [], "CCC")], facets: [], nHits: 2]

    }

    void "test index action"() {
        when:
        request.method = 'GET'
        controller.index()
        then:
        "/bardWebInterface/homePage" == view

    }

    void "test homePage action"() {
        when:
        request.method = 'GET'
        controller.homePage()
        then:
        "/bardWebInterface/homePage" == view
    }

    void "test searchProjectsByIDs action"() {
        given:
        controller.params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchProjectsByIDs()
        then:
        queryService.findProjectsByPIDs(_) >> {projectAdapterMap}
        and:
        if (responseTextLength > 0) {
            assert response.text
        } else {
            assert !response.text
        }
        where:
        label                                 | searchString | projectAdapterMap                                                                                                       | responseTextLength
        "Search Projects By Id"               | "1234, 4567" | [projectAdapters: [buildProjectAdapter(1234, "project1"), buildProjectAdapter(1234, "project2")], facets: [], nHits: 2] | 20
        "Search Projects By non existing Ids" | "12,67"      | null                                                                                                                    | 0


    }

    void "test searchAssaysByIDs action #label"() {
        given:
        controller.params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchAssaysByIDs()
        then:
        queryService.findAssaysByADIDs(_) >> {assayAdapterMap}
        and:
        if (responseTextLength > 0) {
            assert response.text
        } else {
            assert !response.text
        }
        where:
        label                               | searchString | assayAdapterMap                                                                                               | responseTextLength
        "Search Assays By Ids"              | "1234, 4567" | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2] | 20
        "Search Assays By Non existing Ids" | "12, 47"     | null                                                                                                          | 0


    }

    void "test searchCompoundsByIDs action"() {
        given:
        controller.params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchCompoundsByIDs()
        then:
        queryService.findCompoundsByCIDs(_) >> {compoundAdapterMap}
        and:
        if (responseTextLength > 0) {
            assert response.text
        } else {
            assert !response.text
        }
        where:
        label                               | searchString | compoundAdapterMap                                                                                                      | responseTextLength
        "Search Compounds By Id"            | "1234, 4567" | [compoundAdapters: [buildCompoundAdapter(1234, [], "CC"), buildCompoundAdapter(4567, [], "CCC")], facets: [], nHits: 2] | 20
        "Search Compounds Non existing Ids" | "12, 45"     | null                                                                                                                    | 0


    }

    void "Demonstrate that there are carts assays"() {
        when:
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(assayTitle: "foo")

        then:
        assertNotNull cartAssay
    }



    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.showCompound(cid)
        then:
        queryService.showCompound(_) >> { compoundAdapter }
        expectedView == view

        if (cid && compoundAdapter) {
            assert model.compound
            expectedCID == model.compound.pubChemCID
            expectedSIDs == model.compound.pubChemSIDs
        } else {
            assert response.text == 'Could not find compound'
        }

        where:
        label                                             | cid              | compoundAdapter                            | expectedCID | expectedSIDs | expectedView
        "Render show compound page"                       | new Integer(872) | buildCompoundAdapter(872, [1, 2, 3], "CC") | 872         | [1, 2, 3]    | "/bardWebInterface/showCompound"
        "Render not found Compound string, null Adapter"  | null             | null                                       | null        | null         | null
        "Render not found Compound with Compound Adpater" | null             | buildCompoundAdapter(872, [1, 2, 3], "CC") | null        | null         | null
        "Compound does not exist"                         | new Integer(-1)  | null                                       | null        | null         | null

    }


    void "test showAssay #label"() {

        when:
        request.method = 'GET'
        controller.showAssay(adid)

        then:
        queryService.showAssay(_) >> { assayAdapter }

        expectedAssayView == view
        if (adid && assayAdapter) {
            assert model.assayAdapter
            adid == model.assayAdapter.assay.id
            name == model.assayAdapter.name
        }
        else {
            assert response.text == 'Assay Protocol ID parameter required'
        }
        where:
        label                    | adid                | name   | assayAdapter                      | expectedAssayView
        "Return an assayAdapter" | new Integer(485349) | "Test" | buildAssayAdapter(485349, "Test") | "/bardWebInterface/showAssay"
        "Assay ADID is null"     | null                | "Test" | buildAssayAdapter(485349, "Test") | null
        "Assay Adapter is null"  | null                | "Test" | null                              | null

    }

    void "test showProject #label"() {

        when:
        request.method = 'GET'
        controller.showProject(pid)

        then:
        queryService.showProject(_) >> {projectAdapter }

        expectedProjectView == view
        if (pid && projectAdapter) {
            assert model.projectAdapter
            pid == model.projectAdapter.project.id
            name == model.projectAdapter.name
        }
        else {
            assert response.text == 'Project ID parameter required'
        }
        where:
        label                     | pid                 | name   | projectAdapter                      | expectedProjectView
        "Return a Project"        | new Integer(485349) | "Test" | buildProjectAdapter(485349, "Test") | "/bardWebInterface/showProject"
        "Project PID is null"     | null                | "Test" | buildProjectAdapter(485349, "Test") | null
        "Project Adapter is null" | null                | "Test" | null                                | null
    }


    void "test autocomplete #label"() {
        when:
        request.method = 'GET'
        controller.params.term = searchString
        controller.autoCompleteAssayNames()
        then:
        queryService.autoComplete(_) >> { expectedList }
        controller.response.json.toString() == new JSONArray(expectedList.toString()).toString()

        where:
        label                | searchString | expectedList
        "Return two strings" | "Bro"        | ["Broad Institute MLPCN Platelet Activation"]

    }

    CompoundAdapter buildCompoundAdapter(final Long cid, final List<Long> sids, final String smiles) {
        final Compound compound = new Compound()
        final DataSource source = new DataSource("stuff", "v1")
        compound.setId(cid);
        for (Long sid : sids) {
            compound.add(new LongValue(source, Compound.PubChemSIDValue, sid));
        }
        // redundant
        compound.add(new LongValue(source, Compound.PubChemCIDValue, cid));
        return new CompoundAdapter(compound)
    }

    AssayAdapter buildAssayAdapter(final Long adid, final String name) {
        final Assay assay = new Assay(name)
        assay.setId(adid)
        return new AssayAdapter(assay)
    }

    ProjectAdapter buildProjectAdapter(final Long pid, final String name) {
        final Project project = new Project(name)
        project.setId(pid)
        return new ProjectAdapter(project)
    }
}
