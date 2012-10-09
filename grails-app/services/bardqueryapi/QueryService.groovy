package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTProjectService
import org.apache.commons.lang.time.StopWatch
import promiscuity.PromiscuityScoreService
import bard.core.*

class QueryService implements IQueryService {
    /**
     * {@link QueryServiceWrapper}
     */
    QueryServiceWrapper queryServiceWrapper
    /**
     * {@link QueryHelperService}
     */
    QueryHelperService queryHelperService
    /**
     * {@link PromiscuityScoreService}
     */
    PromiscuityScoreService promiscuityScoreService

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
        if (searchString) {
            //re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now become SearchString)
            String updatedSearchString = this.queryHelperService.stripCustomStringFromSearchString(searchString)

            final SearchParams searchParams = this.queryHelperService.constructSearchParams(updatedSearchString, top, skip, searchFilters)
            //do the search
            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Compound> searchIterator = this.queryServiceWrapper.restCompoundService.search(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find compounds by text search ${searchParams.toString()}")
            //collect results
            final Collection<Compound> compounds = searchIterator.collect()

            //convert to adapters
            foundCompoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(compounds))
            facets = searchIterator.facets
            nhits = searchIterator.count
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
        if (searchString) {
            //re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now become SearchString)
            String updatedSearchString = this.queryHelperService.stripCustomStringFromSearchString(searchString)

            final SearchParams searchParams = this.queryHelperService.constructSearchParams(updatedSearchString, top, skip, searchFilters)

            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Assay> searchIterator = this.queryServiceWrapper.restAssayService.search(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find assays by text search ${searchParams.toString()}")

            final Collection assays = searchIterator.collect()
            foundAssayAdapters.addAll(this.queryHelperService.assaysToAdapters(assays))
            facets = searchIterator.facets
            nhits = searchIterator.count
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
        int nhits = 0
        if (searchString) {
            //query for count
            //re-normalize the search string to strip out custom syntax (e.g gobp:SearchString now becomes SearchString)
            String updatedSearchString = this.queryHelperService.stripCustomStringFromSearchString(searchString)
            final SearchParams searchParams = this.queryHelperService.constructSearchParams(updatedSearchString, top, skip, searchFilters)
            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Project> searchIterator = this.queryServiceWrapper.restProjectService.search(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find projects by text search ${searchParams.toString()}")
            final Collection projects = searchIterator.collect()
            foundProjectAdapters.addAll(this.queryHelperService.projectsToAdapters(projects))
            facets = searchIterator.facets
            nhits = searchIterator.count
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
    Map structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final List<SearchFilter> searchFilters = [], final int top = 50, final int skip = 0) {
        final List<CompoundAdapter> compoundAdapters = []
        Collection<Value> facets = []

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
            final ServiceIterator<Compound> searchIterator = this.queryServiceWrapper.restCompoundService.structureSearch(structureSearchParams);
            this.queryHelperService.stopStopWatch(sw, "structure search ${structureSearchParams.toString()}")
            //collect the results
            final Collection<Compound> compounds = searchIterator.collect()
            //convert to adapters
            compoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(compounds))

            //collect the facets
            //facets = searchIterator.facets
        }
        int nhits = compoundAdapters.size()
        return [compoundAdapters: compoundAdapters, facets: facets, nHits: nhits]
    }

    //===================== Find Resources given a list of IDs ================================
    /**
     * Given a list of Compound Ids return all the compounds that were found
     * @param compoundIds
     * @param filters {@link SearchFilter}'s
     * @return Map
     */
    Map findCompoundsByCIDs(final List<Long> compoundIds, List<SearchFilter> filters = []) {
        final List<CompoundAdapter> compoundAdapters = []
        Collection<Value> facets = []

        if (compoundIds) {
            //create ETAG using a random name
            StopWatch sw = this.queryHelperService.startStopWatch()
            //final Object etag = restCompoundService.newETag("Compound ETags", compoundIds);
            //commenting out facets until we figure out how to apply filters to ID searches
            //facets = restCompoundService.getFacets(etag)
            final Collection<Compound> compounds = this.queryServiceWrapper.restCompoundService.get(compoundIds)
            this.queryHelperService.stopStopWatch(sw, "find compounds by CIDs ${compoundIds.toString()}")
            compoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(compounds))
            //TODO: Eben though facets are available they cannnot be used for filtering
        }
        int nhits = compoundAdapters.size()
        return [compoundAdapters: compoundAdapters, facets: facets, nHits: nhits]
    }

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @param filters {@link SearchFilter}'s
     * @return map
     */
    Map findAssaysByADIDs(final List<Long> assayIds, List<SearchFilter> filters = []) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []

        if (assayIds) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final Collection<Assay> assays = this.queryServiceWrapper.restAssayService.get(assayIds)
            this.queryHelperService.stopStopWatch(sw, "find assays by ADIDs ${assayIds.toString()}")
            if (assays) {
                foundAssayAdapters.addAll(this.queryHelperService.assaysToAdapters(assays))
                //TODO: Facet needed. Not yet ready in JDO
            }
        }
        final int nhits = foundAssayAdapters.size()
        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     *
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @param filters {@link SearchFilter}'s
     * @return Map
     */
    Map findProjectsByPIDs(final List<Long> projectIds, List<SearchFilter> filters = []) {
        Collection<Value> facets = []
        final List<ProjectAdapter> foundProjectAdapters = []
        if (projectIds) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final Collection<Project> projects = this.queryServiceWrapper.restProjectService.get(projectIds)
            this.queryHelperService.stopStopWatch(sw, "find projects by PIDs ${projectIds.toString()}")
            if (projects) {
                foundProjectAdapters.addAll(this.queryHelperService.projectsToAdapters(projects))
                //TODO: Facet needed. Not yet ready in JDO
            }
        }
        final int nhits = foundProjectAdapters.size()
        return [projectAdapters: foundProjectAdapters, facets: facets, nHits: nhits]

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
            final Compound compound = this.queryServiceWrapper.restCompoundService.get(compoundId)
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
        if (assayId) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final RESTAssayService restAssayService = this.queryServiceWrapper.restAssayService
            Assay assay = restAssayService.get(assayId)
            this.queryHelperService.stopStopWatch(sw, "show assay ${assayId.toString()}")
            if (assay) {
                final ServiceIterator<Experiment> experimentIterator = restAssayService.iterator(assay, Experiment)
                Collection<Experiment> experiments = experimentIterator.collect()

                final ServiceIterator<Project> projectIterator = restAssayService.iterator(assay, Project)
                Collection<Project> projects = projectIterator.collect()


                final AssayAdapter assayAdapter = new AssayAdapter(assay)
                return [assayAdapter: assayAdapter, experiments: experiments, projects: projects]
            }
        }
        return [:]
    }
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return Map
     */
    Map showProject(final Long projectId) {
        if (projectId) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final RESTProjectService restProjectService = this.queryServiceWrapper.restProjectService
            final Project project = restProjectService.get(projectId)
            this.queryHelperService.stopStopWatch(sw, "show project ${projectId.toString()}")
            if (project) {
                final ServiceIterator<Experiment> experimentIterator = restProjectService.iterator(project, Experiment)
                List<Experiment> experiments = experimentIterator.collect()
                experiments.sort { it.role }
                final ServiceIterator<Assay> assayIterator = restProjectService.iterator(project, Assay)
                Collection<Assay> assays = assayIterator.collect()
                ProjectAdapter projectAdapter = new ProjectAdapter(project)
                return [projectAdapter: projectAdapter, experiments: experiments, assays: assays]
            }
        }
        return [:]
    }

    //==============================================Auto Complete ======
    /**
     *
     * @param term
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(String term) {

        //the number of items to retrieve per category
        final int numberOfTermsToRetrieve = 3
        //if string is already quoted strip it
        term = term.replaceAll("\"", "");

        final SuggestParams suggestParams = new SuggestParams(term, numberOfTermsToRetrieve)
        //we only use the terms in the assay service, because the other suggest services do not seem to
        //have useful things
        final Map<String, List<String>> autoSuggestResponseFromJDO = this.queryServiceWrapper.restAssayService.suggest(suggestParams);
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
     * Success would return [status: resp.status, message: 'Success', promiscuityScore: promiscuityScore]
     * Failure would return [status: HHTTP Error Code, message: "Error getting Promiscuity Score for ${fullURL}", promiscuityScore: null]
     */
    public Map findPromiscuityScoreForCID(Long cid) {
        final String promiscuityScoreURL = "${queryServiceWrapper.promiscuityScoreURL}"
        return this.promiscuityScoreService.findPromiscuityScoreForCID("${promiscuityScoreURL}${cid}")
    }

}