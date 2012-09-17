package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import org.apache.commons.lang3.time.StopWatch
import bard.core.*

class QueryService implements IQueryService {

    QueryServiceWrapper queryServiceWrapper
    QueryHelperService queryHelperService

    //========================================================== Free Text Searches ================================
    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return
     */
    Map findCompoundsByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<CompoundAdapter> foundCompoundAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        if (searchString) {
            final SearchParams searchParams = this.queryHelperService.constructSearchParams(searchString, top, skip, searchFilters)
            //do the search
            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Compound> searchIterator = this.queryServiceWrapper.getRestCompoundService().search(searchParams)
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
     * We can use a trick to get more than 10 records
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findAssaysByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        if (searchString) {
            final SearchParams searchParams = this.queryHelperService.constructSearchParams(searchString, top, skip, searchFilters)

            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Assay> searchIterator = this.queryServiceWrapper.getRestAssayService().search(searchParams)
            this.queryHelperService.stopStopWatch(sw, "find assays by text search ${searchParams.toString()}")

            final Collection assays = searchIterator.collect()
            foundAssayAdapters.addAll(this.queryHelperService.assaysToAdapters(assays))
            facets = searchIterator.facets
            nhits = searchIterator.count
        }
        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findProjectsByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        List<ProjectAdapter> foundProjectAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        if (searchString) {
            //query for count
            final SearchParams searchParams = this.queryHelperService.constructSearchParams(searchString, top, skip, searchFilters)
            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Project> searchIterator = this.queryServiceWrapper.getRestProjectService().search(searchParams)
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
     * @param structureSearchParamsType
     * @param top
     * @param skip
     * @return of compounds
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
                List<String[]> filters = []
                for (SearchFilter searchFilter : searchFilters) {
                    filters.add([searchFilter.filterName, searchFilter.filterValue] as String[])
                }
                structureSearchParams.setFilters(filters)
            }
            //do the search
            StopWatch sw = this.queryHelperService.startStopWatch()
            final ServiceIterator<Compound> searchIterator = this.queryServiceWrapper.getRestCompoundService().structureSearch(structureSearchParams);
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
     * @return list
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
            final Collection<Compound> compounds = this.queryServiceWrapper.getRestCompoundService().get(compoundIds)
            this.queryHelperService.stopStopWatch(sw, "find compounds by CIDs ${compoundIds.toString()}")
            compoundAdapters.addAll(this.queryHelperService.compoundsToAdapters(compounds))
        }
        int nhits = compoundAdapters.size()
        return [compoundAdapters: compoundAdapters, facets: facets, nHits: nhits]
    }

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @return list
     */
    Map findAssaysByADIDs(final List<Long> assayIds, List<SearchFilter> filters = []) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []

        if (assayIds) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final Collection<Assay> assays = this.queryServiceWrapper.getRestAssayService().get(assayIds)
            this.queryHelperService.stopStopWatch(sw, "find assays by ADIDs ${assayIds.toString()}")
            if (assays)
                foundAssayAdapters.addAll(this.queryHelperService.assaysToAdapters(assays))
            //TODO: Facet needed. Not yet ready in JDO
        }
        final int nhits = foundAssayAdapters.size()
        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     *
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @return list
     */
    Map findProjectsByPIDs(final List<Long> projectIds, List<SearchFilter> filters = []) {
        Collection<Value> facets = []
        final List<ProjectAdapter> foundProjectAdapters = []
        if (projectIds) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final Collection<Project> projects = this.queryServiceWrapper.getRestProjectService().get(projectIds)
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
            final Compound compound = this.queryServiceWrapper.getRestCompoundService().get(compoundId)
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
     * @return AssayAdapter
     */
    AssayAdapter showAssay(final Long assayId) {
        if (assayId) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            Assay assay = this.queryServiceWrapper.getRestAssayService().get(assayId)
            this.queryHelperService.stopStopWatch(sw, "show assay ${assayId.toString()}")
            if (assay) {
                return new AssayAdapter(assay)
            }
        }
        return null
    }
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return ProjectAdapter
     */
    ProjectAdapter showProject(final Long projectId) {
        if (projectId) {
            StopWatch sw = this.queryHelperService.startStopWatch()
            final Project project = this.queryServiceWrapper.getRestProjectService().get(projectId)
            this.queryHelperService.stopStopWatch(sw, "show project ${projectId.toString()}")
            if (project) {
                return new ProjectAdapter(project)
            }
        }
        return null
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

        final SuggestParams suggestParams = new SuggestParams(term, numberOfTermsToRetrieve)
        //we only use the terms in the assay service, because the other suggest services do not seem to
        //have useful things
        final Map<String, List<String>> autoSuggestResponseFromJDO = this.queryServiceWrapper.getRestAssayService().suggest(suggestParams);
        final List<Map<String, String>> autoSuggestTerms = this.queryHelperService.autoComplete(term, autoSuggestResponseFromJDO)
        return autoSuggestTerms
    }
    /**
     * Extract filters from the search string if any
     * @return list of filters from search String
     */
    public void findFiltersInSearchBox(final List<SearchFilter> searchFilters, final String searchString) {
        queryHelperService.findFiltersInSearchBox(searchFilters, searchString)
    }

    //=================================================== Molecular SpreeadSheet related =========================
//    public void a(final List<Long> cids) {
//        StopWatch sw = this.queryHelperService.startStopWatch()
//        final String eTagName = "ETAG_" + sw.getStartTime().toString()
//        Object etag = queryServiceWrapper.getRestCompoundService().newETag(eTagName, cids);
//        this.queryHelperService.stopStopWatch(sw, "Create ETag ${eTagName}")
//
//        final Experiment experiment = this.experimentService.get(experimentid)
//        Collection<Compound> compounds = this.compoundService.get(cids)
//        then: "We expect to get back a list of facets"
//        ServiceIterator<Value> eiter = this.experimentService.activities(experiment, etag);
//        assertNotNull eiter
//        ExperimentHolder experimentHolder = new ExperimentHolder()
//        while (eiter.hasNext() && eiter.getConsumedCount() < 1000000) {
//            Value v = eiter.next();
//            assert v
//            experimentHolder.appendValue(v)
//        }
//        assert experimentHolder.dataHolder.size() == cids.size()
//    }
}