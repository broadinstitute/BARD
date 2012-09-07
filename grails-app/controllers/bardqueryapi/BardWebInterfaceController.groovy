package bardqueryapi

import bard.core.StructureSearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter

import javax.servlet.http.HttpServletResponse

/**
 *
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(SearchHelper)
class BardWebInterfaceController {

    def shoppingCartService
    QueryService queryService

    List<SearchFilter> filters = []


    def index() {
        homePage()
    }

    def homePage() {
        render(view: "homePage")
    }
    //================ Search By IDs ================================

    /**
     *
     * Given a list of Compound ids, invoke this method
     */
    def searchCompoundsByIDs() {
        final String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                //strip out all spaces
                final List<Long> cids = searchStringToIdList(searchString)
                Map compoundAdapterMap = this.queryService.findCompoundsByCIDs(cids)
                List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
                render(template: 'compounds', model: [
                        compoundAdapters: compoundAdapters,
                        facets: compoundAdapterMap.facets,
                        nhits: compoundAdapterMap.nHits,
                        searchString: "${searchString}"
                ]
                )
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")


    }
    /**
     * Given a list of assays ids, invoke this action
     */
    def searchAssaysByIDs() {

        final String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                final List<Long> adids = searchStringToIdList(searchString)
                final Map assayAdapterMap = this.queryService.findAssaysByADIDs(adids)

                render(template: 'assays', model: [
                        assayAdapters: assayAdapterMap.assayAdapters,
                        facets: assayAdapterMap.facets,
                        nhits: assayAdapterMap.nHits,
                        searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }
    /**
     * Given a list of Project ids, invoke this action
     */
    def searchProjectsByIDs() {
        final String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                final List<Long> projectIds = searchStringToIdList(searchString)
                Map projectAdapterMap = this.queryService.findProjectsByPIDs(projectIds)
                render(template: 'projects', model: [
                        projectAdapters: projectAdapterMap.projectAdapters,
                        facets: projectAdapterMap.facets,
                        nhits: projectAdapterMap.nHits,
                        searchString: "${searchString}"])

                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                        "Project search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")


    }

    //=================== Structure Searches (Exact, Similarity, SubStructure, Exact and SupeStructure Searches ===================
    /**
     * TODO: Add filters
     * Do structure searches
     */
    def searchStructures() {
        String searchString = params.searchString?.trim()
        if (searchString) {

            try {

                final String[] searchStringSplit = searchString.split(":")
                if (searchStringSplit.length == 2) {
                    final String searchTypeString = searchStringSplit[0]
                    final String smiles = searchStringSplit[1]
                    //we make the first character capitalized to match the ENUM
                    final StructureSearchParams.Type searchType = searchTypeString?.toLowerCase()?.capitalize() as StructureSearchParams.Type
                    Map compoundAdapterMap = queryService.structureSearch(smiles, searchType)
                    List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
                    render(template: 'compounds', model: [
                            compoundAdapters: compoundAdapters,
                            facets: compoundAdapterMap.facets,
                            nhits: compoundAdapterMap.nHits,
                            searchString: "${searchString}"
                    ]
                    )
                    return
                }
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                        "Structure search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required must be of the form StructureSearchType:Smiles'
        redirect(action: "homePage")
    }
    //============================================ Show Pages ===================================================================

    /**
     *
     * @param cid - Given a single CID
     *
     */
    def showCompound(Integer cid) {
        try {
            Integer compoundId = cid ?: params.id as Integer//if '' param is provided, use that; otherwise, try the default id one

            CompoundAdapter compoundAdapter = null;
            if (compoundId) {
                compoundAdapter = this.queryService.showCompound(compoundId)
            }

            if (compoundAdapter) {
                render(view: "showCompound", model: [compound: compoundAdapter])
            }
            else {
                render "Could not find compound"
            }
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    "Structure search has encountered an error:\n${exp.message}")
        }
    }
    /**
     *
     * @param assayProtocolId
     */
    def showAssay(Integer assayProtocolId) {
        Integer assayId = assayProtocolId ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one
        try {
            if (assayId) {
                AssayAdapter assayAdapter = this.queryService.showAssay(assayId)
                render(view: "showAssay", model: [assayAdapter: assayAdapter])
            }
            else {
                render "Assay Protocol ID parameter required"
            }
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    "Structure search has encountered an error:\n${exp.message}")
        }
    }

    def showProject(Integer projectId) {
        Integer projId = projectId ?: params.id as Integer//if 'project' param is provided, use that; otherwise, try the default id one
        try {
            if (projId) {
                ProjectAdapter projectAdapter = this.queryService.showProject(projId)
                render(view: "showProject", model: [projectAdapter: projectAdapter])
            }
            else {
                render "Project ID parameter required"
            }
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    "Structure search has encountered an error:\n${exp.message}")
        }
    }

    //================= Free Text Searches ======================================================
    /**
     *
     * Find Compounds annotated with Search String
     */
    def searchCompounds() {
        String searchString = params.searchString?.trim()
        List<SearchFilter> searchFilters = []
        handleCompoundSearches(this.queryService, searchString, searchFilters)
    }
    /**
     *
     * Find Assays annotated with Search String
     */
    def searchAssays() {
        String searchString = params.searchString?.trim()
        List<SearchFilter> searchFilters = []
        handleAssaySearches(this.queryService, searchString, searchFilters)
    }
    /**
     *
     * Find Projects annotated with Search String
     */
    def searchProjects() {
        String searchString = params.searchString?.trim()
        List<SearchFilter> searchFilters = []
        handleProjectSearches(this.queryService, searchString, searchFilters)
    }

    def applyFilters(SearchCommand searchCommand) {

        if (searchCommand.hasErrors()) {
            flash.message = searchCommand.errors
        }
        else {
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            String searchString = params.searchString?.trim()
            if (params.formName == FacetFormType.AssayFacetForm.toString()) {
                handleAssaySearches(this.queryService, searchString, searchFilters);
                return
            }
            else if (params.formName == FacetFormType.ProjectFacetForm.toString()) {
                handleProjectSearches(this.queryService, searchString, searchFilters);
                return
            }
            else if (params.formName == FacetFormType.CompoundFacetForm.toString()) {
                handleCompoundSearches(this.queryService, searchString, searchFilters);
                return
            }
        }
        return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Select at least one facet")
    }

    /**
     * TODO: Should redirect to NCGC
     * Autocomplete
     */
    def autoCompleteAssayNames() {
        try {
            final List<String> assayNames = this.queryService.autoComplete(params?.term)
            render(contentType: "text/json") {
                for (String assayName : assayNames) {
                    element assayName
                }
                if (!assayNames) {
                    element ""
                }
            }
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    "Structure search has encountered an error:\n${exp.message}")
        }
    }
}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
class SearchHelper {

    def handleAssaySearches(final bardqueryapi.QueryService queryService, final String searchString, final List<SearchFilter> searchFilters) {
        if (searchString) {
            try {
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip

                final Map assaysByTextSearchResultsMap = queryService.findAssaysByTextSearch(searchString, top, skip, searchFilters)
                render(template: 'assays', model: [
                        assayAdapters: assaysByTextSearchResultsMap.assayAdapters,
                        facets: assaysByTextSearchResultsMap.facets,
                        nhits: assaysByTextSearchResultsMap.nHits,
                        searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        }
        return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Search String required")

    }

    def handleCompoundSearches(final bardqueryapi.QueryService queryService, final String searchString, final List<SearchFilter> searchFilters) {
        if (searchString) {
            try {
                final Map<String, Integer> searchParams = handleSearchParams()
                final int top = searchParams.top
                final int skip = searchParams.skip
                final Map compoundsByTextSearchResultsMap = queryService.findCompoundsByTextSearch(searchString, top, skip, searchFilters)
                render(template: 'compounds',
                        model: [
                                compoundAdapters: compoundsByTextSearchResultsMap.compoundAdapters,
                                facets: compoundsByTextSearchResultsMap.facets,
                                nhits: compoundsByTextSearchResultsMap.nHits,
                                searchString: "${searchString}"
                        ]
                )
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        }
        return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Search String required")

    }

    def handleProjectSearches(final bardqueryapi.QueryService queryService, final String searchString, final List<SearchFilter> searchFilters) {
        if (searchString) {
            try {
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip
                final Map projectsByTextSearch = queryService.findProjectsByTextSearch(searchString, top, skip, searchFilters)
                render(template: 'projects', model: [
                        projectAdapters: projectsByTextSearch.projectAdapters,
                        facets: projectsByTextSearch.facets,
                        nhits: projectsByTextSearch.nHits,
                        searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Project search has encountered an error:\n${exp.message}")
            }
        }
        return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Search String required")

    }
    /**
     *
     * @param relativePath for example /search/compounds
     */
    public Map<String, Integer> handleSearchParams() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.offset = params.offset ? params.int('offset') : 0
        Map<String, Integer> map = [top: new Integer(params.max), skip: new Integer(params.offset)]
        return map
    }
    /**
     * Strip out all empty spaces, split on ',' and return as a list of longs
     */
    public List<Long>  searchStringToIdList(String searchString){
        List<Long> ids = searchString.replaceAll("\\s+", "").split(",") as List<Long>
        return ids
    }
}
