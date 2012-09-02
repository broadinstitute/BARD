package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTProjectService
import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.QueryExecutorService
import wslite.json.JSONObject
import bard.core.*

class QueryService {

    QueryServiceWrapper queryServiceWrapper
    ElasticSearchService elasticSearchService
    QueryExecutorService queryExecutorService

    //The following are configured in resources.groovy
    String ncgcSearchBaseUrl
    String elasticSearchRootURL   //grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
    //read from properties file
    final String AUTO_COMPLETE_SEARCH_URL = "assays/_search"

    /**
     * The JSON representation that would be used for Autocompletion of assay names
     * We  return the first ten matched objects
     */
    final String ELASTIC_AUTO_COMPLETE_SEARCH = '''{
  "fields": ["name"],
  "query": {
    "query_string": {
      "default_field": "name",
      "query": "*"
    }
  },
  "size": 10
}
'''

    //========================================================== Free Text Searches ================================
    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters - TODO: Not yet supported by JDO
     * @return
     */
    Map findCompoundsByTextSearch(final String searchString, final int top = 50, final int skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<CompoundAdapter> foundCompoundAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        if (searchString) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            final SearchParams searchParams = constructSearchParams(searchString, top, skip, searchFilters)
            //do the search
            final ServiceIterator<Compound> searchIterator = restCompoundService.search(searchParams)
            //collect results
            final Collection<Compound> compounds = searchIterator.collect()

            //convert to adapters
            foundCompoundAdapters.addAll(compoundsToAdapters(compounds))
            facets = searchIterator.facets
            nhits = searchIterator.count
        }
         return [compoundAdapters: foundCompoundAdapters, facets: facets, nHits: nhits]
    }

    /**
     * We can use a trick to get more than 50 records
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findAssaysByTextSearch(final String searchString, final int top = 50, final int skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        if (searchString) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            final SearchParams searchParams = constructSearchParams(searchString, top, skip, searchFilters)


            final ServiceIterator<Assay> searchIterator = restAssayService.search(searchParams)
            final Collection assays = searchIterator.collect()
            foundAssayAdapters.addAll(assaysToAdapters(assays))
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
     * @param searchFilters - TODO: Not yet supported by JDO
     * @return   Map
     */
    Map findProjectsByTextSearch(final String searchString, final int top = 50, final int skip = 0, final List<SearchFilter> searchFilters = []) {
        List<ProjectAdapter> foundProjectAdapters = []
        Collection<Value> facets = []
        int nhits = 0
        if (searchString) {
            //query for count
            final RESTProjectService restProjectService = this.queryServiceWrapper.getRestProjectService()
            final SearchParams searchParams = constructSearchParams(searchString, top, skip, searchFilters)
            final ServiceIterator<Project> searchIterator = restProjectService.search(searchParams)
            final Collection projects = searchIterator.collect()
            foundProjectAdapters.addAll(projectsToAdapters(projects))
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
    Map structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final int top = 50, final int skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<CompoundAdapter> compoundAdapters = []
        Collection<Value> facets = []

        if (smiles) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()

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
            final ServiceIterator<Compound> searchIterator = restCompoundService.structureSearch(structureSearchParams);
            //collect the results
            final Collection<Compound> compounds = searchIterator.collect()
            //convert to adapters
            compoundAdapters.addAll(compoundsToAdapters(compounds))

            //collect the facets
            facets = searchIterator.facets
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
    Map findCompoundsByCIDs(final List<Long> compoundIds) {
        final List<CompoundAdapter> compoundAdapters = []
        Collection<Value> facets = []

        if (compoundIds) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            //create ETAG using a random name
            final Object etag = restCompoundService.newETag("Compound ETags", compoundIds);
            facets = restCompoundService.getFacets(etag)
            final Collection<Compound> compounds = restCompoundService.get(compoundIds)
            compoundAdapters.addAll(compoundsToAdapters(compounds))
        }
        int nhits = compoundAdapters.size()
        return [compoundAdapters: compoundAdapters, facets: facets, nHits: nhits]
    }

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @return list
     */
    Map findAssaysByADIDs(final List<Long> assayIds) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []

        if (assayIds) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            final Collection<Assay> assays = restAssayService.get(assayIds)
            foundAssayAdapters.addAll(assaysToAdapters(assays))
            //TODO: add facet information
        }
        final int nhits = foundAssayAdapters.size()
        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     * TODO: Facet needed. Not yet ready in JDO
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @return list
     */
    Map findProjectsByPIDs(final List<Long> projectIds) {
        Collection<Value> facets = []
        final List<ProjectAdapter> foundProjectAdapters = []
        if (projectIds) {
            final RESTProjectService restProjectService = this.queryServiceWrapper.getRestProjectService()
            final Collection<Project> projects = restProjectService.get(projectIds)
            foundProjectAdapters.addAll(projectsToAdapters(projects))
            //TODO: add facet information
        }
        final int nhits = foundProjectAdapters.size()
        return [projectAdapters: foundProjectAdapters, facets: facets, nHits: nhits]

    }
    //=============== Show Resources Given a Single ID ================

    /**
     * Given a CID, get detailed compound information from REST API
     * @param compoundId
     * @return
     */
    CompoundAdapter showCompound(final Long compoundId) {
        if (compoundId) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            final Compound compound = restCompoundService.get(compoundId)
            if (compound) {
                CompoundAdapter compoundAdapter = new CompoundAdapter(compound)

                //Bug in JDO. You need to also set Compound for it to work
                compoundAdapter.setCompound(compound)
                return compoundAdapter
            }
        }
        return null
    }
    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return
     */
    AssayAdapter showAssay(final Integer assayId) {
        if (assayId) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            Assay assay = restAssayService.get(assayId)
            return new AssayAdapter(assay)
        }
        return null
    }
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return ProjectAdapter
     */
    ProjectAdapter showProject(final Integer projectId) {
        if (projectId) {
            final RESTProjectService restProjectService = this.queryServiceWrapper.getRestProjectService()
            final Project project = restProjectService.get(projectId)
            if (project) {
                return new ProjectAdapter(project)
            }
        }
        return null
    }

    //============= Utility method for extracting facets =============

    /**
     * Apply the filters to the SearchParams
     * @param searchParams
     * @param searchFilters
     */
    void applySearchFilters(final SearchParams searchParams, final List<SearchFilter> searchFilters) {
        if (searchFilters) {
            List<String[]> filters = []
            for (SearchFilter searchFilter : searchFilters) {
                filters.add([searchFilter.filterName, searchFilter.filterValue] as String[])
            }
            searchParams.setFilters(filters)
        }
    }

    /**
     *
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return SearchParams
     */
    SearchParams constructSearchParams(final String searchString, final int top, final int skip, final List<SearchFilter> searchFilters) {
        final SearchParams searchParams = new SearchParams(searchString)
        searchParams.setSkip(skip)
        searchParams.setTop(top);
        applySearchFilters(searchParams, searchFilters)
        return searchParams

    }
    /**
     * Convert the list of compounds to the list of adapters
     * @param compounds
     * @return List < CompoundAdapter > 's
     */
    final List<CompoundAdapter> compoundsToAdapters(final Collection<Compound> compounds) {
        final List<CompoundAdapter> compoundAdapters = []
        for (Compound compound : compounds) {
            final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
            compoundAdapters.add(compoundAdapter)
        }
        return compoundAdapters
    }

    final List<AssayAdapter> assaysToAdapters(final Collection<Assay> assays) {
        final List<AssayAdapter> assayAdapters = []
        for (Assay assay : assays) {
            assayAdapters.add(new AssayAdapter(assay))
        }
        return assayAdapters
    }

    final List<ProjectAdapter> projectsToAdapters(final Collection<Project> projects) {
        final List<ProjectAdapter> projectAdapters = []
        for (Project project : projects) {
            projectAdapters.add(new ProjectAdapter(project))
        }
        return projectAdapters
    }
    //==============================================Elastic Search resources ======

    public List<String> autoComplete(final String term) {
        final List<String> assayNames = handleAutoComplete(term)
        return assayNames

    }
    /**
     * Construct a query String query and pass it on to Elastic Search
     * @param elasticSearchService
     * @param elasticSearchRootURL
     * @return
     */
    protected List<String> handleAutoComplete(final String term) {

        //TODO, this should go to NCGC, once their stuff is ready
        final String urlToElastic = "${this.elasticSearchRootURL}/${AUTO_COMPLETE_SEARCH_URL}"
        final String request = ELASTIC_AUTO_COMPLETE_SEARCH
        if (term) {
            request = ELASTIC_AUTO_COMPLETE_SEARCH.replaceAll("\\*", "${term}*")
        }
        final JSONObject jsonObject = new JSONObject(request)

        final JSONObject responseObject = this.elasticSearchService.searchQueryStringQuery(urlToElastic, jsonObject)
        return responseObject?.hits?.hits.collect { it.fields }.collect { it.name }

    }
}