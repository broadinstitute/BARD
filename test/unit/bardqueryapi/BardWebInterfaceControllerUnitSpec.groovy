package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import wslite.json.JSONArray

import javax.servlet.http.HttpServletResponse

import bard.core.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
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

    void "test free text search assays #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchAssays()
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _) >> {assayAdapterMap}
        and:
        response.redirectedUrl == expectedRedirectURL
        flash.message == flashMessage
        response.status == statusCode
        where:
        label                 | searchString | expectedRedirectURL          | flashMessage                | statusCode                                   | assayAdapterMap
        "Empty Search String" | ""           | '/bardWebInterface/homePage' | 'Search String is required' | HttpServletResponse.SC_MOVED_TEMPORARILY     | null
        "Throws Exception"    | "1234,5678"  | null                         | null                        | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"             | "1234,5678"  | null                         | null                        | HttpServletResponse.SC_OK                    | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test free text search projects #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchProjects()
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _) >> {projectAdapterMap}
        and:
        response.redirectedUrl == expectedRedirectURL
        flash.message == flashMessage
        response.status == statusCode
        where:
        label                 | searchString | expectedRedirectURL          | flashMessage                | statusCode                                   | projectAdapterMap
        "Empty Search String" | ""           | '/bardWebInterface/homePage' | 'Search String is required' | HttpServletResponse.SC_MOVED_TEMPORARILY     | null
        "Throws Exception"    | "1234,5678"  | null                         | null                        | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"             | "1234,5678"  | null                         | null                        | HttpServletResponse.SC_OK                    | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test free text search compounds #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchCompounds()
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _) >> {compoundAdapterMap}
        and:
        response.redirectedUrl == expectedRedirectURL
        flash.message == flashMessage
        response.status == statusCode
        where:
        label                 | searchString | expectedRedirectURL          | flashMessage                | statusCode                                   | compoundAdapterMap
        "Empty Search String" | ""           | '/bardWebInterface/homePage' | 'Search String is required' | HttpServletResponse.SC_MOVED_TEMPORARILY     | null
        "Throws Exception"    | "1234,5678"  | null                         | null                        | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"             | "1234,5678"  | null                         | null                        | HttpServletResponse.SC_OK                    | [compoundAdapters: [buildCompoundAdapter(1234, [], "CC"), buildCompoundAdapter(4567, [], "CCC")], facets: [], nHits: 2]

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

//    void "test search #label"() {
//        when:
//        request.method = 'GET'
//        controller.params.searchString = searchTerm
//        controller.search()
//
//        then:
//        queryService.search(searchTerm) >> { resultJson }
//
//        assert "/bardWebInterface/homePage" == view
//        assert model.totalCompounds == expectedTotalCompounds
//        assert model.assays.size == expectedAssays
//        assert model.compounds.size == expectedCompounds
//        assert model.experiments == []
//        assert model.projects == []
//
//        where:
//        label                                | searchTerm | resultJson                                                                                                          | expectedTotalCompounds | expectedAssays | expectedCompounds
//        "nothing was found"                  | '644'      | [totalCompounds: 0, assays: [], compounds: [], experiments: [], projects: []]                                       | 0                      | 0              | 0
//        "An Assay and a compound were found" | '644'      | [totalCompounds: 1, assays: [esAssay], compounds: ['CC'], xcompounds: [esxCompound], experiments: [], projects: []] | 1                      | 1              | 1
//    }


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
