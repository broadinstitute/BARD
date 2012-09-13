package bardqueryapi

import bard.core.StructureSearchParams
import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import org.apache.commons.lang3.StringUtils

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
    def searchCompoundsByIDs(SearchCommand searchCommand) {
        if (searchCommand.searchString) {
            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            this.queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            try {
                //strip out all spaces
                final List<Long> cids = searchStringToIdList(searchCommand.searchString)

                Map compoundAdapterMap = this.queryService.findCompoundsByCIDs(cids, searchFilters)
                List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
                render(template: 'compounds', model: [
                        compoundAdapters: compoundAdapters,
                        facets: compoundAdapterMap.facets,
                        nhits: compoundAdapterMap.nHits,
                        searchString: "${searchCommand.searchString}",
                        appliedFilters: getAppliedFilters(searchFilters, compoundAdapterMap.facets)
                ]
                )
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required'
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String is required")
        }

    }
    /**
     * Given a list of assays ids, invoke this action
     */
    def searchAssaysByIDs(SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            try {
                final List<Long> adids = searchStringToIdList(searchCommand.searchString)
                final Map assayAdapterMap = this.queryService.findAssaysByADIDs(adids, searchFilters)

                render(template: 'assays', model: [
                        assayAdapters: assayAdapterMap.assayAdapters,
                        facets: assayAdapterMap.facets,
                        nhits: assayAdapterMap.nHits,
                        searchString: "${searchCommand.searchString}",
                        appliedFilters: getAppliedFilters(searchFilters, assayAdapterMap.facets)])
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required'
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String is required")
        }
    }
    /**
     * Given a list of Project ids, invoke this action
     */
    def searchProjectsByIDs(SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            try {
                final List<Long> projectIds = searchStringToIdList(searchCommand.searchString)
                Map projectAdapterMap = this.queryService.findProjectsByPIDs(projectIds, searchFilters)
                render(template: 'projects', model: [
                        projectAdapters: projectAdapterMap.projectAdapters,
                        facets: projectAdapterMap.facets,
                        nhits: projectAdapterMap.nHits,
                        searchString: "${searchCommand.searchString}",
                        appliedFilters: getAppliedFilters(searchFilters, projectAdapterMap.facets)])
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                        "Project search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required'
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String is required")

        }


    }

    //=================== Structure Searches (Exact, Similarity, SubStructure, Exact and SupeStructure Searches ===================
    /**
     *
     * Do structure searches
     */
    def searchStructures(SearchCommand searchCommand) {
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            this.queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            try {

                final String[] searchStringSplit = searchCommand.searchString.split(":")
                if (searchStringSplit.length == 2) {
                    final String searchTypeString = searchStringSplit[0]
                    final String smiles = searchStringSplit[1]
                    //we make the first character capitalized to match the ENUM
                    final StructureSearchParams.Type searchType = searchTypeString?.toLowerCase()?.capitalize() as StructureSearchParams.Type
                    Map compoundAdapterMap = queryService.structureSearch(smiles, searchType, searchFilters)
                    List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
                    render(template: 'compounds', model: [
                            compoundAdapters: compoundAdapters,
                            facets: compoundAdapterMap.facets,
                            nhits: compoundAdapterMap.nHits,
                            searchString: "${searchCommand.searchString}",
                            appliedFilters: getAppliedFilters(searchFilters, compoundAdapterMap.facets)
                    ]
                    )
                }
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                        "Structure search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required must be of the form StructureSearchType:Smiles'
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String is required")
        }
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
            log.error(exp)
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
            log.error(exp)
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
            log.error(exp)
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
            final List<Map<String, String>> assayNames = this.queryService.autoComplete(params?.term)
            // return assayNames as JSON
            render(contentType: "text/json") {
                assayNames
//                for (String assayName : assayNames) {
//                    element assayName
//                }
//                if (!assayNames) {
//                    element ""
//                }
            }
        }
        catch (Exception exp) {
            log.error(exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    "AutoComplete encoutered and error :\n${exp.message}")
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
            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
            final String searchString = searchCommand.searchString.trim()

            try {
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip

                final Map assaysByTextSearchResultsMap = queryService.findAssaysByTextSearch(searchString, top, skip, searchFilters)
                render(template: 'assays', model: [
                        assayAdapters: assaysByTextSearchResultsMap.assayAdapters,
                        facets: assaysByTextSearchResultsMap.facets,
                        nhits: assaysByTextSearchResultsMap.nHits,
                        searchString: "${searchString}",
                        appliedFilters: getAppliedFilters(searchFilters, assaysByTextSearchResultsMap.facets)])
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        } else {
            log.error("Search String required")
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String required")
        }
    }

    def handleCompoundSearches(final bardqueryapi.QueryService queryService, final SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {

            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
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
                                appliedFilters: getAppliedFilters(searchFilters, compoundsByTextSearchResultsMap.facets)]
                )
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        } else {
            log.error('Search String required for Compound Searches')
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String required cor Compound Searches")
        }

    }

    def handleProjectSearches(final bardqueryapi.QueryService queryService, final SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            normalizeSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.getAppliedFilters()
            if (!searchFilters) {//user SearchCommand
                searchFilters = []
            }
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
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
                        appliedFilters: getAppliedFilters(searchFilters, projectsByTextSearch.facets)])
            }
            catch (Exception exp) {
                log.error(exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Project search has encountered an error:\n${exp.message}")
            }
        } else {
            log.error("Search String required for Project searches");
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String required")
        }

    }
    /**
     *
     * @param relativePath for example /search/compounds
     */
    public Map<String, Integer> handleSearchParams() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.offset = params.offset ? params.int('offset') : 0

        return [top: new Integer(params?.max), skip: new Integer(params?.offset)]
    }
    /**
     * Strip out all empty spaces, split on ',' and return as a list of longs
     */
    public List<Long> searchStringToIdList(String searchString) {
        List<Long> ids = searchString.replaceAll("\\s+", "").split(",") as List<Long>
        return ids
    }

    /**
     * we need to remove duplicates, these duplicates occur we read the search string
     * both from the hidden field in the apply filters form
     * and from the param object during paging
     * this would go away if NCGC supports filters for all searches, then we would
     * not need to add the search string to the paging object
     */
    protected void normalizeSearchString(SearchCommand searchCommand) {
        Set<String> searchCommandSplit = searchCommand.searchString.trim().split(",") as Set<String>
        searchCommand.searchString = searchCommandSplit.join(",")
        params.searchString = searchCommand.searchString

    }

    Map getAppliedFilters(List<SearchFilter> searchFilters, Collection<Value> facets) {
        //Includes all the applied search-filters (selected previously) that were also returned with the new filtering faceting.
        List<SearchFilter> appliedFiltersAlreadyInFacets = searchFilters.findAll { SearchFilter filter ->
            Value parent = facets.find {Value parent -> parent.id == filter.filterName}
            return parent?.children.find { Value child -> child.id == filter.filterValue}
        }

        //Groups all the applied search-filters in facets into a parent-facet/children-facets map. We use this group to display the applied search filters WITHIN the facet groups
        //If the facet-group exists but the applied-filter's corresponding facet didn't come back after the filtering, we still want to display the filter in its appropriate (facet) group, if we can.
        Map appliedFiltersNotInFacetsGrouped = ((searchFilters ?: []) - appliedFiltersAlreadyInFacets) ?
            (searchFilters - appliedFiltersAlreadyInFacets).groupBy { SearchFilter filter -> filter.filterName} : [:]

        //Includes all the applied filters we know would not have any facet group since no facet in this group came back after the filtering was applied.
        //We need to group these filters, rebuild their groups (parent) and display them next to the facets
        List<SearchFilter> appliedFiltersDisplayedOutsideFacets = ((searchFilters ?: []) - appliedFiltersAlreadyInFacets)?.findAll { SearchFilter filter ->
            //filter.filterName is not in any of the parents' ids
            return !(facets.find { Value parent -> parent.id == filter.filterName})
        }

        //Group all the applied filters so we can use the keys as group (parent) names.
        Map appliedFiltersDisplayedOutsideFacetsGrouped = appliedFiltersDisplayedOutsideFacets ?
            appliedFiltersDisplayedOutsideFacets.groupBy { SearchFilter filter -> filter.filterName} : [:]

        return [searchFilters: searchFilters, appliedFiltersDisplayedOutsideFacetsGrouped: appliedFiltersDisplayedOutsideFacetsGrouped, appliedFiltersNotInFacetsGrouped: appliedFiltersNotInFacetsGrouped]
    }
}
