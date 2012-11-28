package bardqueryapi

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.assays.FreeTextAssayResult
import bard.core.rest.spring.compounds.ExpandedCompoundResult
import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.rest.spring.project.ExpandedProjectResult
import bard.core.rest.spring.util.StructureSearchParams
import org.apache.commons.lang.time.StopWatch
import bard.core.rest.spring.compounds.Compound

class QueryService implements IQueryService {
    /**
     * {@link QueryHelperService}
     */
    QueryHelperService queryHelperService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService
    //========================================================== Free Text Searches ================================
    /**
     * Find Compounds by Text search
     *
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return Map of results
     */
    Map findCompoundsByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<CompoundAdapter> foundCompoundAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        // String eTag = null
        if (searchString) {
            //re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now become SearchString)
            String updatedSearchString = this.queryHelperService.stripCustomStringFromSearchString(searchString)

            final SearchParams searchParams = this.queryHelperService.constructSearchParams(updatedSearchString, top, skip, searchFilters)
            //do the search
            StopWatch sw = this.queryHelperService.startStopWatch()
            ExpandedCompoundResult expandedCompoundResult = compoundRestService.findCompoundsByFreeTextSearch(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find compounds by text search ${searchParams.toString()}")

            //convert to adapters
            foundCompoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(expandedCompoundResult))
            facets = expandedCompoundResult.getFacetsToValues()
            nhits = expandedCompoundResult.numberOfHits
            //   eTag = searchIterator.ETag.toString()
        }
        return [compoundAdapters: foundCompoundAdapters, facets: facets, nHits: nhits]
    }

    /**
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return Map of results
     */
    Map findAssaysByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        //String eTag = null
        if (searchString) {
            //re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now become SearchString)
            String updatedSearchString = this.queryHelperService.stripCustomStringFromSearchString(searchString)

            final SearchParams searchParams = this.queryHelperService.constructSearchParams(updatedSearchString, top, skip, searchFilters)

            StopWatch sw = this.queryHelperService.startStopWatch()
            FreeTextAssayResult freeTextAssayResult = assayRestService.findAssaysByFreeTextSearch(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find assays by text search ${searchParams.toString()}")
            foundAssayAdapters.addAll(this.queryHelperService.assaysToAdapters(freeTextAssayResult))
            facets = freeTextAssayResult.getFacetsToValues()
            nhits = freeTextAssayResult.numberOfHits
            //eTag = searchIterator.ETag.toString()
        }
        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters {@link SearchFilter}'s
     * @return Map
     */
    Map findProjectsByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        List<ProjectAdapter> foundProjectAdapters = []
        Collection<Value> facets = []
        //String eTag = null
        int nhits = 0
        if (searchString) {
            //query for count
            //re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now becomes SearchString)
            String updatedSearchString = this.queryHelperService.stripCustomStringFromSearchString(searchString)
            final SearchParams searchParams = this.queryHelperService.constructSearchParams(updatedSearchString, top, skip, searchFilters)
            StopWatch sw = this.queryHelperService.startStopWatch()
            ExpandedProjectResult expandedProjectResult = projectRestService.findProjectsByFreeTextSearch(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find projects by text search ${searchParams.toString()}")
            foundProjectAdapters.addAll(this.queryHelperService.projectsToAdapters(expandedProjectResult))
            facets = expandedProjectResult.getFacetsToValues()
            nhits = expandedProjectResult.numberOfHits
            // eTag = searchIterator.ETag.toString()
        }
        return [projectAdapters: foundProjectAdapters, facets: facets, nHits: nhits]
    }

    //====================================== Structure Searches ========================================
    /**
     * @param smiles
     * @param structureSearchParamsType {@link StructureSearchParams}
     * @param top
     * @param skip
     * @return Map
     */
    Map structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final List<SearchFilter> searchFilters = [], final Integer top = 50, final Integer skip = 0) {
        final List<CompoundAdapter> compoundAdapters = []
        Collection<Value> facets = []
        //String eTag = null
        if (smiles) {
            //construct search parameters
            final StructureSearchParams structureSearchParams =
                new StructureSearchParams(smiles, structureSearchParamsType).setSkip(skip).setTop(top);

            if (structureSearchParamsType == StructureSearchParams.Type.Similarity) {
                structureSearchParams.setThreshold(0.9)
            }
            if (searchFilters) {
                final List<String[]> filters = queryHelperService.convertSearchFiltersToFilters(searchFilters)
                structureSearchParams.setFilters(filters)
            }
            //do the search
            StopWatch sw = this.queryHelperService.startStopWatch()
            ExpandedCompoundResult expandedCompoundResult = compoundRestService.structureSearch(structureSearchParams);
            this.queryHelperService.stopStopWatch(sw, "structure search ${structureSearchParams.toString()}")
            //collect the results
            //convert to adapters
            compoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(expandedCompoundResult))

            //collect the facets
            //facets = searchIterator.facets
            //eTag = searchIterator.ETag.toString()
        }
        int nhits = compoundAdapters.size()
        return [compoundAdapters: compoundAdapters, facets: facets, nHits: nhits]
    }

    //===================== Find Resources given a list of IDs ================================
    /**
     * Given a list of Compound Ids return all the compounds that were found
     * @param compoundIds
     * @param filters {@link SearchFilter}'s  - We do not use filters because the JDO does not use them for ID searches yet
     * @return Map
     */
    Map findCompoundsByCIDs(final List<Long> compoundIds, List<SearchFilter> filters = []) {
        return [:]
//        final List<CompoundAdapter> compoundAdapters = []
//        Collection<Value> facets = []
//        //String eTag = null
//        if (compoundIds) {
//            //create ETAG using a random name
//            StopWatch sw = this.queryHelperService.startStopWatch()
//            //  eTag = restCompoundService.newETag("Compound ETags", compoundIds).toString();
//            //commenting out facets until we figure out how to apply filters to ID searches
//            //facets = restCompoundService.getFacets(etag)
//            final Collection<Compound> compounds = restCompoundService.get(compoundIds)
//            this.queryHelperService.stopStopWatch(sw, "find compounds by CIDs ${compoundIds.toString()}")
//            compoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(compounds))
//            //TODO: Even though facets are available they cannot be used for filtering
//        }
//        int nhits = compoundAdapters.size()
//        return [compoundAdapters: compoundAdapters, facets: facets, nHits: nhits]
    }

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @param filters {@link SearchFilter}'s  - We do not use filters because the JDO does not use them for ID searches yet
     * @return map
     */
    Map findAssaysByADIDs(final List<Long> assayIds, List<SearchFilter> filters = []) {
        return [:]
//        final List<AssayAdapter> foundAssayAdapters = []
//        Collection<Value> facets = []
//
//        if (assayIds) {
//            StopWatch sw = this.queryHelperService.startStopWatch()
//            final Collection<Assay> assays = this.restAssayService.get(assayIds)
//            this.queryHelperService.stopStopWatch(sw, "find assays by ADIDs ${assayIds.toString()}")
//            if (assays) {
//                foundAssayAdapters.addAll(this.queryHelperService.assaysToAdapters(assays))
//                //TODO: Facet needed. Not yet ready in JDO
//            }
//        }
//        final int nhits = foundAssayAdapters.size()
//        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     *
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @param filters {@link SearchFilter}'s   - We do not use filters because the JDO does not use them for ID searches yet
     * @return Map
     */
    Map findProjectsByPIDs(final List<Long> projectIds, List<SearchFilter> filters = []) {
//        Collection<Value> facets = []
//        final List<ProjectAdapter> foundProjectAdapters = []
//        if (projectIds) {
//            StopWatch sw = this.queryHelperService.startStopWatch()
//            final Collection<Project> projects = restProjectService.get(projectIds)
//            this.queryHelperService.stopStopWatch(sw, "find projects by PIDs ${projectIds.toString()}")
//            if (projects) {
//                foundProjectAdapters.addAll(this.queryHelperService.projectsToAdapters(projects))
//                //TODO: Facet needed. Not yet ready in JDO
//            }
//        }
//        final int nhits = foundProjectAdapters.size()
//        return [projectAdapters: foundProjectAdapters, facets: facets, nHits: nhits]
        return [:]

    }
    /**
     *
     * @param compound
     * @param activeOnly - true if we want only the active compounds
     * @return int the number of tested assays
     */
    @Deprecated
    public int getNumberTestedAssays(Long cid,
                                     boolean activeOnly) {
//        final Compound compound = restCompoundService.get(cid)
//
//        final Collection<Assay> assays = combinedRestService.getTestedAssays(compound, activeOnly)
//        return assays.size()
        return 0
    }

    //=============== Show Resources Given a Single ID ================

    /**
     * Given a CID, get detailed compound information from REST API
     * @param compoundId
     * @return CompoundAdapter
     */
    CompoundAdapter showCompound(final Long compoundId) {
        if (compoundId) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final Compound compound = compoundRestService.getCompoundById(compoundId)
            this.queryHelperService.stopStopWatch(sw, "show compound ${compoundId.toString()}")
            if (compound) {
                return new CompoundAdapter(compound)
            }
        }
        return null
    }

    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return Map
     */
    Map showAssay(final Long assayId) {
//        if (assayId) {
//            StopWatch sw = this.queryHelperService.startStopWatch()
//            Assay assay = restAssayService.get(assayId)
//            this.queryHelperService.stopStopWatch(sw, "show assay ${assayId.toString()}")
//            if (assay) {
//                final SearchResult<Experiment> experimentIterator = combinedRestService.searchResultByAssay(assay, Experiment)
//                Collection<Experiment> experiments = experimentIterator.searchResults
//
//                final SearchResult<Project> projectIterator = combinedRestService.searchResultByAssay(assay, Project)
//                Collection<Project> projects = projectIterator.searchResults
//
//
//                final AssayAdapter assayAdapter = new AssayAdapter(assay)
//                return [assayAdapter: assayAdapter, experiments: experiments, projects: projects]
//            }
//        }
        return [:]
    }
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return Map
     */
    Map showProject(final Long projectId) {
//        if (projectId) {
//            StopWatch sw = this.queryHelperService.startStopWatch()
//            final RESTProjectService restProjectService = restProjectService
//            final Project project = restProjectService.get(projectId)
//            this.queryHelperService.stopStopWatch(sw, "show project ${projectId.toString()}")
//            if (project) {
//                final SearchResult<Experiment> experimentIterator = combinedRestService.searchResultByProject(project, Experiment)
//                List<Experiment> experiments = experimentIterator.searchResults
//                experiments.sort { it.role }
//                final SearchResult<Assay> assayIterator = combinedRestService.searchResultByProject(project, Assay)
//                Collection<Assay> assays = assayIterator.searchResults
//                ProjectAdapter projectAdapter = new ProjectAdapter(project)
//                return [projectAdapter: projectAdapter, experiments: experiments, assays: assays]
//            }
//        }
        return [:]
    }

    //==============================================Auto Complete ======
    /**
     *
     * @param term
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(final String term) {

        //the number of items to retrieve per category
        final int numberOfTermsToRetrieve = 3
        //if string is already quoted strip it
        final String normalizedTerm = term.replaceAll("\"", "");

        final SuggestParams suggestParams = new SuggestParams(normalizedTerm, numberOfTermsToRetrieve)
        //we only use the terms in the assay service, because the other suggest services do not seem to
        //have useful things
        final Map<String, List<String>> autoSuggestResponseFromJDO = assayRestService.suggest(suggestParams);
        final List<Map<String, String>> autoSuggestTerms = this.queryHelperService.autoComplete(term, autoSuggestResponseFromJDO)
        return autoSuggestTerms
    }
    /**
     *
     * Extract filters from the search string if any
     * @param searchFilters {@link SearchFilter}'s
     * @param searchString
     * @return list of filters from search String
     */
    public void findFiltersInSearchBox(final List<SearchFilter> searchFilters, final String searchString) {
        queryHelperService.findFiltersInSearchBox(searchFilters, searchString)
    }
    /**
     *
     * @param cid
     * return Map
     * Success would return [status: 200, message: 'Success', promiscuityScore: promiscuityScore]
     * Failure would return [status: 404, message: "Error getting Promiscuity Score for ${CID}", promiscuityScore: null]
     */
    public Map findPromiscuityScoreForCID(Long cid) {
        final PromiscuityScore promiscuityScore = compoundRestService.findPromiscuityScoreForCompound(cid);
        if (promiscuityScore) {
            return [status: 200, message: 'Success', promiscuityScore: promiscuityScore]
        }
        return [status: 404, message: "Error getting Promiscuity Score for ${cid}", promiscuityScore: null]
    }

}