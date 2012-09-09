package bardqueryapi

import bard.core.StructureSearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import org.apache.commons.lang3.StringUtils

import javax.servlet.http.HttpServletResponse
import bard.core.Value

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
    def searchCompoundsByIDs(SearchCommand searchCommand) {
        List<SearchFilter> searchFilters = []
        if (searchCommand.formName) {//user SearchCommand
            searchFilters = searchCommand.getAppliedFilters()
        } else {
            searchFilters = params.filters
            if (!searchFilters) {
                searchFilters = []
            }

        }
        if (searchCommand.searchString) {
            final String searchString = searchCommand.searchString.trim()
            try {
                //strip out all spaces
                final List<Long> cids = searchStringToIdList(searchString)

                Map compoundAdapterMap = this.queryService.findCompoundsByCIDs(cids, searchFilters)
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
    def searchAssaysByIDs(SearchCommand searchCommand) {
        List<SearchFilter> searchFilters = []
        if (searchCommand.formName) {//user SearchCommand
            searchFilters = searchCommand.getAppliedFilters()
        } else {
            searchFilters = params.filters
            if (!searchFilters) {
                searchFilters = []
            }

        }
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            String searchString = searchCommand.searchString.trim()

            try {
                final List<Long> adids = searchStringToIdList(searchString)
                final Map assayAdapterMap = this.queryService.findAssaysByADIDs(adids, searchFilters)

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
    def searchProjectsByIDs(SearchCommand searchCommand) {
        List<SearchFilter> searchFilters = []
        if (searchCommand.formName) {//user SearchCommand
            searchFilters = searchCommand.getAppliedFilters()
        } else {
            searchFilters = params.filters
            if (!searchFilters) {
                searchFilters = []
            }

        }
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            String searchString = searchCommand.searchString.trim()

            try {
                final List<Long> projectIds = searchStringToIdList(searchString)
                Map projectAdapterMap = this.queryService.findProjectsByPIDs(projectIds, filters)
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
     *
     * Do structure searches
     */
    def searchStructures(SearchCommand searchCommand) {
        List<SearchFilter> searchFilters = []
        if (searchCommand.formName) {//user SearchCommand
            searchFilters = searchCommand.getAppliedFilters()
        } else {
            searchFilters = params.filters
            if (!searchFilters) {
                searchFilters = []
            }

        }
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            String searchString = searchCommand.searchString.trim()

            try {

                final String[] searchStringSplit = searchString.split(":")
                if (searchStringSplit.length == 2) {
                    final String searchTypeString = searchStringSplit[0]
                    final String smiles = searchStringSplit[1]
                    //we make the first character capitalized to match the ENUM
                    final StructureSearchParams.Type searchType = searchTypeString?.toLowerCase()?.capitalize() as StructureSearchParams.Type
                    Map compoundAdapterMap = queryService.structureSearch(smiles, searchType, filters)
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
    def searchCompounds(SearchCommand searchCommand) {

        handleCompoundSearches(this.queryService, searchCommand)

    }
    /**
     *
     * Find Assays annotated with Search String
     */
    def searchAssays(SearchCommand searchCommand) {

        handleAssaySearches(this.queryService, searchCommand)
    }
    /**
     *
     * Find Projects annotated with Search String
     */
    def searchProjects(SearchCommand searchCommand) {
        handleProjectSearches(this.queryService, searchCommand)
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
    final static String GO_BIOLOGICAL_PROCESS_TERM = "gobp_term"

    boolean isGoBiologicalTerm(String searchString) {
        return searchString.toLowerCase().trim().startsWith(GO_BIOLOGICAL_PROCESS_TERM)
    }

    def handleAssaySearches(final bardqueryapi.QueryService queryService, final SearchCommand searchCommand) {
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            final List<SearchFilter> searchFilters = findFiltersInSearchBox(searchCommand)
            final String searchString = searchCommand.searchString.trim()

            try {
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip

                final Map assaysByTextSearchResultsMap = queryService.findAssaysByTextSearch(searchString, top, skip, searchFilters)
//                //Includes all the applied search-filters (selected previously) that were also returned with the new filtering faceting.
//                List<SearchFilter> appliedFiltersAlreadyInFacets = searchFilters.findAll { SearchFilter filter ->
//                    Value parent = assaysByTextSearchResultsMap.facets.find {Value parent -> parent.id == filter.filterName}
//                    return parent?.children.find { Value child -> child.id == filter.filterValue}
//                }
//                //Groups all the applied search-filters in facets into a parent-facet/children-facets map. We use this group to display the applied search filters WITHIN the facet groups
//                //If the facet-group exists but the applied-filter's corresponding facet didn't come back after the filtering, we still want to display the filter in its appropriate (facet) group, if we can.
//                Map appliedFiltersNotInFacetsGrouped = (searchFilters - appliedFiltersAlreadyInFacets) ?
//                    (searchFilters - appliedFiltersAlreadyInFacets).groupBy { SearchFilter filter -> filter.filterName} : [:]
//
//                //Includes all the applied filters we know would not have any facet group since no facet in this group came back after the filtering was applied.
//                //We need to group these filters, rebuild their groups (parent) and display them next to the facets
//                List<SearchFilter> appliedFiltersDisplayedOutsideFacets = (searchFilters - appliedFiltersAlreadyInFacets)?.findAll { SearchFilter filter ->
//                    //filter.filterName is not in any of the parents' ids
//                    return !(assaysByTextSearchResultsMap.facets.find { Value parent -> parent.id == filter.filterName})
//                }
//
//                //Group all the applied filters so we can use the keys as group (parent) names.
//                Map appliedFiltersDisplayedOutsideFacetsGrouped = appliedFiltersDisplayedOutsideFacets ?
//                    appliedFiltersDisplayedOutsideFacets.groupBy { SearchFilter filter -> filter.filterName} : [:]
//
                render(template: 'assays', model: [
                        assayAdapters: assaysByTextSearchResultsMap.assayAdapters,
                        facets: assaysByTextSearchResultsMap.facets,
                        nhits: assaysByTextSearchResultsMap.nHits,
                        searchString: "${searchString}",
                        searchFilters: searchFilters])
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

    def handleCompoundSearches(final bardqueryapi.QueryService queryService, final SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {


            final List<SearchFilter> searchFilters = findFiltersInSearchBox(searchCommand)

            try {
                final String searchString = searchCommand.searchString.trim()
                final Map<String, Integer> searchParams = handleSearchParams()
                final int top = searchParams.top
                final int skip = searchParams.skip
                final Map compoundsByTextSearchResultsMap = queryService.findCompoundsByTextSearch(searchString, top, skip, searchFilters)
                render(template: 'compounds',
                        model: [
                                compoundAdapters: compoundsByTextSearchResultsMap.compoundAdapters,
                                facets: compoundsByTextSearchResultsMap.facets,
                                nhits: compoundsByTextSearchResultsMap.nHits,
                                searchString: "${searchString}",
                                searchFilters: searchFilters]
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

    def handleProjectSearches(final bardqueryapi.QueryService queryService, final SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {

            final List<SearchFilter> searchFilters = findFiltersInSearchBox(searchCommand)
            try {
                final String searchString = searchCommand.searchString.trim()
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip
                final Map projectsByTextSearch = queryService.findProjectsByTextSearch(searchString, top, skip, searchFilters)
                render(template: 'projects', model: [
                        projectAdapters: projectsByTextSearch.projectAdapters,
                        facets: projectsByTextSearch.facets,
                        nhits: projectsByTextSearch.nHits,
                        searchString: "${searchString}",
                        searchFilters: searchFilters])
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
    public List<Long> searchStringToIdList(String searchString) {
        List<Long> ids = searchString.replaceAll("\\s+", "").split(",") as List<Long>
        return ids
    }

    /**
     *
     * @param searchCommand
     * @return list of filters from search String
     */
    public List<SearchFilter> findFiltersInSearchBox(SearchCommand searchCommand) {

        List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
        if (!searchFilters) {//user SearchCommand
            searchFilters = []
        }

        final String searchString = searchCommand.searchString
        //now parse the GO TERMS out of it
        if (isGoBiologicalTerm(searchString)) {
            String[] split = searchString.split(":")
            String searchValue = split[1].trim()
            SearchFilter searchFilter = new SearchFilter(filterName: GO_BIOLOGICAL_PROCESS_TERM, filterValue: searchValue.trim())
            searchCommand.searchString = searchValue.trim()
            searchFilters.add(searchFilter)
        }
        return searchFilters
    }
}
