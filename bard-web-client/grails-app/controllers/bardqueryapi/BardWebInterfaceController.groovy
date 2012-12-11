package bardqueryapi

import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.rest.spring.util.StructureSearchParams
import grails.plugins.springsecurity.Secured
import molspreadsheet.MolecularSpreadSheetService
import org.apache.commons.lang.StringUtils

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
@Secured(['isFullyAuthenticated()'])
class BardWebInterfaceController {
    def shoppingCartService
    IQueryService queryService
    MolecularSpreadSheetService molecularSpreadSheetService
    MobileService mobileService
    List<SearchFilter> filters = []
    final static String PROBE_ETAG_NAME = 'MLP Probes'

    //An AfterInterceptor to handle mobile-view routing.
    def afterInterceptor = [action: this.&handleMobile]

    protected handleMobile(model, modelAndView) {
        if (modelAndView && mobileService.detect(request)) {
            String newView = '/mobile' + modelAndView.viewName
            if (mobileService.gspExists(newView)) {
                modelAndView.viewName = newView
            }
            else {
                modelAndView.viewName = "/mobile/bardWebInterface/missingPageError"
            }
        }
    }

    Boolean isMobile() {
        return mobileService.detect(request)
    }

    def index() {
    }

    def search() {
        flash.searchString = params.searchString
        redirect(action: 'searchResults')
    }

    def searchResults() {
    }

    def showExperiment() {
        render(view: 'showExperimentResult', model: [experimentId: params.id, searchString: params.searchString])
    }

    def showExperimentResult(Long id) {
        try {
            if (id) {
                Map<String, Integer> searchParams = handleSearchParams()
                final Integer top = searchParams.top
                final Integer skip = searchParams.skip
                final Map experimentDataMap = molecularSpreadSheetService.findExperimentDataById(id, top, skip)
                if (experimentDataMap) {
                    render(template: 'experimentResult', model: [experimentDataMap: experimentDataMap, searchString: params.searchString])
                } else {
                    flash.message = "Experiment ID ${id} not found"
                    log.error(flash.message)
                    return response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "${flash.message}")
                }
            } else {
                flash.message = 'ID is a required Field'
                log.error(flash.message)
                return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "${flash.message}")
            }
        } catch (Exception ee) {

            flash.message = "Problem finding Experiment ${id}"
            log.error(flash.message, ee)
            return response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "${flash.message}")
        }

    }



    def promiscuity(Long cid) {
        if (cid) {
            //Get the Promiscuity score for this CID
            try {
                Map results = queryService.findPromiscuityScoreForCID(cid)
                if (results.status == 200) {
                    final PromiscuityScore promiscuityScore = results.promiscuityScore
                    render(template: 'promiscuity', model: [scaffolds: promiscuityScore.scaffolds])
                }
                else { //status code of NOT OK returned. Usually CID has no promiscuity score
                    log.error(results.message)
                    return response.sendError(results.status,
                            "${results.message}")
                }
            } catch (Exception ee) { //error is thrown
                String errorMessage = "Could not get promiscuity score for ${cid}"
                log.error(errorMessage)
                return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "${errorMessage}")
            }
        } else {
            flash.message = "A valid CID is required for promiscuity score"
            log.error(flash.message)
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "${flash.message}")

        }
    }
    //================ Search By IDs ================================

    /**
     *
     * Given a list of Compound ids, invoke this method
     */
    def searchCompoundsByIDs(SearchCommand searchCommand) {
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            String originalSearchString = searchCommand.searchString
            final String[] searchStringSplit = searchCommand.searchString.split(":")
            if (searchStringSplit.length == 2) {  //if the search string is of the form ADID:1234,3456...
                final String searchTypeString = searchStringSplit[0]
                //TODO: assert that the string is CID

                String ids = searchStringSplit[1]
                //TODO: assert that this string is not empty or null
                //assign the list of ids only to the command object
                searchCommand.searchString = ids
            }

            //we want to remove the duplicates from the search string
            // removeDuplicatesFromSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
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
                        searchString: "${originalSearchString}",
                        appliedFilters: getAppliedFilters(searchFilters, compoundAdapterMap.facets)
                ]
                )
            }
            catch (Exception exp) {
                log.error("Error searching for compounds by IDs", exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required'
            log.error(flash.message)
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String is required")
        }

    }
    /**
     * Given a list of assays ids, invoke this action
     */
    def searchAssaysByIDs(SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            String originalSearchString = searchCommand.searchString
            final String[] searchStringSplit = searchCommand.searchString.split(":")
            if (searchStringSplit.length == 2) {  //if the search string is of the form ADID:1234,3456...
                final String searchTypeString = searchStringSplit[0]
                //TODO: assert that the string is ADID

                String ids = searchStringSplit[1]
                //TODO: assert that this string is not empty or null
                //assign the list of ids only to the command object
                searchCommand.searchString = ids
            }

            //we want to remove the duplicates from the search string
            //removeDuplicatesFromSearchString(searchCommand)
            //after removing duplicates, reassign
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            try {
                final List<Long> adids = searchStringToIdList(searchCommand.searchString)
                final Map assayAdapterMap = this.queryService.findAssaysByADIDs(adids, searchFilters)

                render(template: 'assays', model: [
                        assayAdapters: assayAdapterMap.assayAdapters,
                        facets: assayAdapterMap.facets,
                        nhits: assayAdapterMap.nHits,
                        searchString: "${originalSearchString}",
                        appliedFilters: getAppliedFilters(searchFilters, assayAdapterMap.facets)])
            }
            catch (Exception exp) {
                log.error("Error searching for assays by IDs", exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required'
            log.error(flash.message)
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String is required")
        }
    }
    /**
     * Given a list of ProjectSearchResult ids, invoke this action
     */
    def searchProjectsByIDs(SearchCommand searchCommand) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            String originalSearchString = searchCommand.searchString
            final String[] searchStringSplit = searchCommand.searchString.split(":")
            if (searchStringSplit.length == 2) {  //if the search string is of the form PID:1234,3456...
                final String searchTypeString = searchStringSplit[0]
                //TODO: assert that the string is PID

                String ids = searchStringSplit[1]
                //TODO: assert that this string is not empty or null
                //assign the list of ids only to the command object
                searchCommand.searchString = ids
            }

            //we want to remove the duplicates from the search string
            //removeDuplicatesFromSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            try {
                final List<Long> projectIds = searchStringToIdList(searchCommand.searchString)
                Map projectAdapterMap = this.queryService.findProjectsByPIDs(projectIds, searchFilters)
                render(template: 'projects', model: [
                        projectAdapters: projectAdapterMap.projectAdapters,
                        facets: projectAdapterMap.facets,
                        nhits: projectAdapterMap.nHits,
                        searchString: "${originalSearchString}",
                        appliedFilters: getAppliedFilters(searchFilters, projectAdapterMap.facets)])
            }
            catch (Exception exp) {
                log.error('Error while searching project by ids : ' + searchCommand.searchString, exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                        "ProjectSearchResult search has encountered an error:\n${exp.message}")
            }
        } else {
            flash.message = 'Search String is required for project search by Ids'
            log.error(flash.message)
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
        try {
            if (StringUtils.isNotBlank(searchCommand.searchString)) {
                Map map = handleStructureSearch(queryService, searchCommand)
                if (map) {
                    render(template: 'compounds', model: map)
                    return
                }
            }
            flash.message = "Search String is required, must be of the form StructureSearchType:Smiles"
            log.error(flash.message)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "${flash.message}")
        }
        catch (Exception exp) {

            final String errorMessage = "Structure search has encountered an error:\n${exp.message}"
            log.error(errorMessage, exp)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }
    /**
     *
     * @param cid - Given a single CID
     *
     */
    def findSubstanceIds(Long cid) {
        try {
            Long compoundId = cid ?: params.id as Long//if '' param is provided, use that; otherwise, try the default id one

            final List<Long> sids = this.queryService.findSubstancesByCid(compoundId)
            render(template: "substanceIds", model: [sids: sids])

        }
        catch (Exception exp) {
            final String errorMessage = "Show compound page has encountered an error while trying to fetch SIDs:\n${exp.message}"
            log.error(errorMessage, exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
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

            final CompoundAdapter compoundAdapter = this.queryService.showCompound(compoundId)
            if (compoundAdapter) {
                render(view: "showCompound", model: [compound: compoundAdapter, searchString: params.searchString])
            }
            else {
                final String message = "Could not find Compound Id ${cid}"
                flash.message = message
                log.error(message)
                return response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        message)
            }
        }
        catch (Exception exp) {
            final String errorMessage = "Show compound page has encountered an error:\n${exp.message}"
            log.error(errorMessage, exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }
    /**
     *
     * @param assayProtocolId
     */
    def showAssay(Integer assayProtocolId) {
        Integer assayId = assayProtocolId ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one
        try {
            Map assayMap = this.queryService.showAssay(assayId)
            AssayAdapter assayAdapter = assayMap.assayAdapter
            if (assayAdapter) {
                render(view: "showAssay", model: [
                        assayAdapter: assayAdapter,
                        experiments: assayMap.experiments,
                        projects: assayMap.projects, searchString: params.searchString
                ]
                )
            }
            else {
                final String message = "Could not find Assay Id ${assayId}"
                flash.message = message
                log.error(message)
                return response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        message)
            }
        }
        catch (Exception exp) {
            final String errorMessage = "Search For Assay Id ${assayId}:\n${exp.message}"
            log.error(errorMessage, exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(), errorMessage)
        }
    }

    def showProject(Integer projectId) {
        Integer projId = projectId ?: params.id as Integer//if 'project' param is provided, use that; otherwise, try the default id one
        try {
            if (projId) {
                Map projectMap = this.queryService.showProject(projId)
                ProjectAdapter projectAdapter = projectMap.projectAdapter
                render(view: "showProject", model: [projectAdapter: projectAdapter, experiments: projectMap.experiments, assays: projectMap.assays,
                        searchString: params.searchString])
            }
            else {
                final String message = "Could not find Project Id ${projectId}"
                flash.message = message
                log.error(message)
                return response.sendError(HttpServletResponse.SC_NOT_FOUND, message)

            }
        }
        catch (Exception exp) {
            final String errorMessage = "Search For Project By Id ${projectId}:\n${exp.message}"
            log.error(errorMessage, exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(), errorMessage)
        }
    }

    //================= Free Text Searches ======================================================
    /**
     *
     * Find Compounds annotated with Search String
     */
    def searchCompounds(SearchCommand searchCommand) {

        handleCompoundSearches(this.queryService, searchCommand, isMobile())

    }
    /**
     *
     * Find Assays annotated with Search String
     */
    def searchAssays(SearchCommand searchCommand) {

        handleAssaySearches(this.queryService, searchCommand, isMobile())
    }
    /**
     *
     * Find Projects annotated with Search String
     */
    def searchProjects(SearchCommand searchCommand) {
        handleProjectSearches(this.queryService, searchCommand, isMobile())
    }

    /**
     * Autocomplete
     */
    def autoCompleteAssayNames() {
        try {
            final List<Map<String, String>> assayNames = this.queryService.autoComplete(params.term)
            // return assayNames as JSON
            render(contentType: "text/json") {
                assayNames
            }
        }
        catch (Exception exp) {
            final String errorMessage = "AutoComplete encoutered an error :\n${exp.message}"
            log.error(errorMessage, exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }

    def turnoffMobileExperience() {
        session.putValue('mobileExperienceDisabled', true)
        redirect(action: 'index')
    }

    def showProbeList() {
        CompoundResult compoundResult = queryService.compoundRestService.findCompoundsByETag(PROBE_ETAG_NAME)
        List<CompoundAdapter> compoundAdapters = queryService.queryHelperService.compoundsToAdapters(compoundResult)
        render(template: "/mobile/bardWebInterface/compounds",
                model: [
                        compoundAdapters: compoundAdapters,
                        facets: [],
                        nhits: compoundAdapters.size(),
                        searchString: flash.searchString,
                        appliedFilters: [:]]
        )
    }
}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
class SearchHelper {

//    org.springframework.context.ApplicationContext ctx = org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getAttribute(org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.APPLICATION_CONTEXT);
//    MobileService mobileService = ctx.getBean('mobileService')

    Map handleStructureSearch(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand) {
        Map structureSearchResultsMap = [:]
        Map<String, Integer> searchParams = handleSearchParams()
        final Integer top = searchParams.top
        final Integer skip = searchParams.skip

        removeDuplicatesFromSearchString(searchCommand)
        final String[] searchStringSplit = searchCommand.searchString.split(":")
        if (searchStringSplit.length == 2) {
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            final String searchTypeString = searchStringSplit[0]
            final String smiles = searchStringSplit[1]
            //we make the first character capitalized to match the ENUM
            final StructureSearchParams.Type searchType = searchTypeString.toLowerCase().capitalize() as StructureSearchParams.Type
            Map compoundAdapterMap = queryService.structureSearch(smiles, searchType, searchFilters, top, skip)
            List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
            structureSearchResultsMap = [
                    compoundAdapters: compoundAdapters,
                    facets: compoundAdapterMap.facets,
                    nhits: compoundAdapterMap.nHits,
                    searchString: "${searchCommand.searchString}",
                    appliedFilters: getAppliedFilters(searchFilters, compoundAdapterMap.facets)
            ]
        }
        return structureSearchResultsMap
    }

    def handleAssaySearches(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand, Boolean isMobile = false) {
        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            removeDuplicatesFromSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
            final String searchString = searchCommand.searchString.trim()

            try {
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip

                final Map assaysByTextSearchResultsMap = queryService.findAssaysByTextSearch(searchString, top, skip, searchFilters)
                String template = isMobile ? "/mobile/bardWebInterface/assays" : "assays"
                render(template: template, model: [
                        assayAdapters: assaysByTextSearchResultsMap.assayAdapters,
                        facets: assaysByTextSearchResultsMap.facets,
                        nhits: assaysByTextSearchResultsMap.nHits,
                        searchString: "${searchString}",
                        appliedFilters: getAppliedFilters(searchFilters, assaysByTextSearchResultsMap.facets)])
            }
            catch (Exception exp) {
                log.error("Error performing assay search", exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        } else {
            log.error("Search String required")
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Search String required")
        }
    }

    def handleCompoundSearches(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand, Boolean isMobile = false) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {

            removeDuplicatesFromSearchString(searchCommand)
            List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
            try {
                final String searchString = searchCommand.searchString.trim()
                final Map<String, Integer> searchParams = handleSearchParams()
                final int top = searchParams.top
                final int skip = searchParams.skip
                final Map compoundsByTextSearchResultsMap = queryService.findCompoundsByTextSearch(searchString, top, skip, searchFilters)
                String template = isMobile ? "/mobile/bardWebInterface/compounds" : "compounds"
                render(template: template,
                        model: [
                                compoundAdapters: compoundsByTextSearchResultsMap.compoundAdapters,
                                facets: compoundsByTextSearchResultsMap.facets,
                                nhits: compoundsByTextSearchResultsMap.nHits,
                                searchString: "${searchString}",
                                appliedFilters: getAppliedFilters(searchFilters, compoundsByTextSearchResultsMap.facets)]
                )
            }
            catch (Exception exp) {
                log.error("Error performing compound search", exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        } else {
            final String errorMessage = 'Search String required for Compound Searches'
            log.error(errorMessage)
            return response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    errorMessage)
        }

    }

    def handleProjectSearches(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand, Boolean isMobile = false) {

        if (StringUtils.isNotBlank(searchCommand.searchString)) {
            removeDuplicatesFromSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
            try {
                final String searchString = searchCommand.searchString.trim()
                Map<String, Integer> searchParams = handleSearchParams()
                int top = searchParams.top
                int skip = searchParams.skip
                final Map projectsByTextSearch = queryService.findProjectsByTextSearch(searchString, top, skip, searchFilters)
                String template = isMobile ? "/mobile/bardWebInterface/projects" : "projects"
                render(template: template, model: [
                        projectAdapters: projectsByTextSearch.projectAdapters,
                        facets: projectsByTextSearch.facets,
                        nhits: projectsByTextSearch.nHits,
                        searchString: "${searchString}",
                        appliedFilters: getAppliedFilters(searchFilters, projectsByTextSearch.facets)])
            }
            catch (Exception exp) {
                log.error("Error performing project search", exp)
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "ProjectSearchResult search has encountered an error:\n${exp.message}")
            }
        } else {
            log.error("Search String required for ProjectSearchResult searches");
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

        return [top: new Integer(params.max), skip: new Integer(params.offset)]
    }
    /**
     * Strip out all empty spaces, split on ',' and return as a list of longs
     */
    public List<Long> searchStringToIdList(String searchString) {
        //we also want to remove duplicates
        final Set<Long> ids = new HashSet<Long>()
        //first split using spaces
        List<String> splitSpaces = searchString.split("\\s+") as List<String>
        for (String id : splitSpaces) {
            String trimmedString = id.trim()
            ids.addAll(trimmedString.split(",") as Set<Long>)
        }
        return ids as List<Long>
    }

    /**
     * we need to remove duplicates, these duplicates occur we read the search string
     * both from the hidden field in the apply filters form
     * and from the param object during paging
     * this would go away if NCGC supports filters for all searches, then we would
     * not need to add the search string to the paging object
     */
    protected void removeDuplicatesFromSearchString(SearchCommand searchCommand) {
        List<String> searchCommandSplit = []
        if (!searchCommand.searchString.matches(/[^"]*"[^"]+"/)) {
            // This is a little complicated.
            // If the search string contains a quote-delimited string anywhere in it, take the search string as it is.
            // Otherwise, split it on commas, then remove the duplicates.
            // We could do this with a Set instead of performing unique() on the list, but then the order gets a little scrambled.
            // Regex: Zero or more things that aren't ", then ", then one or more things that aren't " (so empty strings are not allowed), then "
            searchCommandSplit.addAll(searchCommand.searchString.trim().split(",") as List<String>)
            searchCommand.searchString = searchCommandSplit.unique().join(",")
        }
        params.searchString = searchCommand.searchString

    }

    Map getAppliedFilters(List<SearchFilter> searchFilters, Collection<Value> facets) {
        //Includes all the applied search-filters (selected previously) that were also returned with the new filtering faceting.
        //1. Check if the facet group-names (a.k.a., parent.id) match
        //2. Check if the facet/filter name (a.k.a., child.id) match
        List<SearchFilter> appliedFiltersAlreadyInFacets = getAppliedFiltersAlreadyInFacets(searchFilters, facets)

        //Groups all the applied search-filters in facets into a parent-facet/children-facets map. We use this group to display the applied search filters WITHIN the facet groups
        //If the facet-group exists but the applied-filter's corresponding facet didn't come back after the filtering, we still want to display the filter in its appropriate (facet) group, if we can.
//        Map appliedFiltersNotInFacetsGrouped = ((searchFilters ?: []) - appliedFiltersAlreadyInFacets) ?
//            (searchFilters - appliedFiltersAlreadyInFacets).groupBy { SearchFilter filter -> filter.filterName.trim()} : [:]
        List<SearchFilter> searchFiltersNotYetApplied = (searchFilters ?: []) - appliedFiltersAlreadyInFacets

        Map<String, List<SearchFilter>> appliedFiltersNotInFacetsGrouped = groupSearchFilters(searchFiltersNotYetApplied)

        //Includes all the applied filters we know would not have any facet group since no facet in this group came back after the filtering was applied.
        //We need to group these filters, rebuild their groups (parent) and display them next to the facets
        List<SearchFilter> appliedFiltersDisplayedOutsideFacets = getAppliedFiltersDisplayedOutsideFacets(searchFiltersNotYetApplied, facets)

        //Group all the applied filters so we can use the keys as group (parent) names.
        Map<String, List<SearchFilter>> appliedFiltersDisplayedOutsideFacetsGrouped = groupSearchFilters(appliedFiltersDisplayedOutsideFacets)

        return [
                searchFilters: searchFilters,
                appliedFiltersDisplayedOutsideFacetsGrouped: appliedFiltersDisplayedOutsideFacetsGrouped,
                appliedFiltersNotInFacetsGrouped: appliedFiltersNotInFacetsGrouped
        ]
    }

    //Compares based on child (facet) name between SearchFilter and facet
    protected List<SearchFilter> getAppliedFiltersAlreadyInFacets(List<SearchFilter> searchFilters, Collection<Value> facets) {
        List<SearchFilter> appliedFiltersAlreadyInFacets = searchFilters.findAll {
            SearchFilter filter ->
            Value parent = facets.find {Value parent ->
                parent.id.trim().equalsIgnoreCase(filter.filterName.trim())
            }
            return parent?.children?.find {
                Value child ->
                child.id.trim().equalsIgnoreCase(filter.filterValue.trim().replace('"', ''))
            }
        }

        return appliedFiltersAlreadyInFacets
    }

    protected Map<String, List<SearchFilter>> groupSearchFilters(List<SearchFilter> searchFiltersNotYetApplied) {
        Map<String, List<SearchFilter>> appliedFiltersNotInFacetsGrouped = [:]
        if (searchFiltersNotYetApplied) {
            appliedFiltersNotInFacetsGrouped = searchFiltersNotYetApplied.groupBy { SearchFilter filter -> filter.filterName.trim()}
        }

        return appliedFiltersNotInFacetsGrouped
    }

    //Compares based on parent (group) name between SearchFilter and facet
    protected List<SearchFilter> getAppliedFiltersDisplayedOutsideFacets(List<SearchFilter> searchFiltersNotYetApplied, Collection<Value> facets) {
        List<SearchFilter> appliedFiltersDisplayedOutsideFacets = []
        if (searchFiltersNotYetApplied) {
            appliedFiltersDisplayedOutsideFacets = searchFiltersNotYetApplied.findAll {SearchFilter filter ->
                //filter.filterName is not in any of the parents' ids
                return !(facets.find { Value parent -> parent.id.trim().equalsIgnoreCase(filter.filterName.trim())})
            }
        }

        return appliedFiltersDisplayedOutsideFacets
    }
}
