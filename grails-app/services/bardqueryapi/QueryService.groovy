package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
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
    String ncgcSearchBaseUrl    //grailsApplication.config.ncgc.server.structureSearch.root.url
    String elasticSearchRootURL   //grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
    String bardAssayViewUrl // grailsApplication.config.bard.assay.view.url
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
    /**
     * TODO: Should return facets and all other parameters as REST API
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @return
     */
    List<CompoundAdapter> findCompoundsByTextSearch(final String searchString, final int top = 10, final int skip = 0) {
        final List<CompoundAdapter> foundCompoundAdapters = []
        if (searchString) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            final SearchParams params = new SearchParams(searchString).setSkip(skip).setTop(top);
            final ServiceIterator<Compound> searchIterator = restCompoundService.search(params)
            while (searchIterator.hasNext()) {
                final Compound compound = searchIterator.next();
                final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
                foundCompoundAdapters.add(compoundAdapter)
            }
        }
        return foundCompoundAdapters
    }
    /**
     *  //TODO ask for paging? Perhaps not the client should just display all they get
     * @param smiles
     * @param structureSearchParamsType
     * @param top
     * @param skip
     * @return of compounds
     */
    List<CompoundAdapter> structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final int top = 10, final int skip = 0) {
        List<CompoundAdapter> compounds = []
        if (smiles) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()

            final StructureSearchParams structureSearchParams =
                new StructureSearchParams(smiles, structureSearchParamsType)
            structureSearchParams.setSkip(skip).setTop(top);

            if (structureSearchParamsType == StructureSearchParams.Type.Similarity) {
                structureSearchParams.setThreshold(0.9)
            }
            ServiceIterator<Compound> iter = restCompoundService.structureSearch(structureSearchParams);
            while (iter.hasNext()) {
                Compound compound = iter.next();
                compounds.add(new CompoundAdapter(compound))
            }
        }
        return compounds
    }
    /**
     * Given a list of Compound Ids return all the compounds that were found
     * @param compoundIds
     * @return list
     */
    List<CompoundAdapter> findCompoundsByCIDs(final List<Long> compoundIds) {
        List<CompoundAdapter> compoundAdapters = []
        if (compoundIds) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            final Collection<Compound> compounds = restCompoundService.get(compoundIds)
            for (Compound compound : compounds) {
                final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
                compoundAdapters.add(compoundAdapter)
            }
        }
        return compoundAdapters
    }
    /**
     * Given a CID, get detailed compound information from REST API
     * @param compoundId
     * @return
     */
    CompoundAdapter showCompound(final Integer compoundId) {
        if (compoundId) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            final Compound compound = restCompoundService.get(compoundId)
            if (compound) {
                return new CompoundAdapter(compound)
            }
        }
        return null
    }
    /**
     * TODO: Should return facets and all other parameters as REST API
     * TODO: Not ready for use
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @return
     */
    List<Assay> findAssaysByTextSearch(final String searchString, final int top = 10, final int skip = 0) {
        final List<Assay> foundAssays = []
        if (searchString) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            final SearchParams params = new SearchParams(searchString).setSkip(skip).setTop(top);
            final ServiceIterator<Assay> searchIterator = restAssayService.search(params)
            while (searchIterator.hasNext()) {
                final Assay assay = searchIterator.next();
                foundAssays.add(assay)
            }
        }
        return foundAssays
    }
    /**
     * Given a list of Assay Ids return all the assays that were found
     * TODO: Not ready to use because it returns an empty list from NCGC
     * TODO: Also need facet and hits information
     * @param assayIds
     * @return list
     */
    List<Assay> findAssaysByAPIDs(final List<Long> assayIds) {
        final List<Assay> foundAssays = []
        if (assayIds) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            final Collection<Assay> assays = restAssayService.get(assayIds)
            //TODO: add facet information
            foundAssays.addAll(assays)
        }
        return foundAssays
    }
    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return
     * TODO: switch to use #findAssaysByAPID
     */
    Assay showAssay(final Integer assayId) {
        if (assayId) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            return restAssayService.get(assayId)
        }
        return null
    }
    /**
     * TODO: Should return facets hits and all other parameters as REST API
     * TODO: Not ready for use
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @return
     */
    List<Project> findProjectsByTextSearch(final String searchString, final int top = 10, final int skip = 0) {
        final List<Project> foundProjects = []
        if (searchString) {
            final RESTProjectService restProjectService = this.queryServiceWrapper.getRestProjectService()
            final SearchParams params = new SearchParams(searchString).setSkip(skip).setTop(top);
            final ServiceIterator<Project> searchIterator = restProjectService.search(params)
            while (searchIterator.hasNext()) {
                final Project project = searchIterator.next();
                foundProjects.add(project)
            }
        }
        return foundProjects
    }
    /**
     * TODO: Not ready to use because it returns an empty list from NCGC
     * TODO: Also need facet and hits information
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @return list
     */
    List<Project> findProjectsByPIDs(final List<Long> projectIds) {
        final List<Project> foundProjects = []
        if (projectIds) {
            final RESTProjectService restProjectService = this.queryServiceWrapper.getRestProjectService()
            final Collection<Project> projects = restProjectService.get(projectIds)
            //TODO: add facet information
            foundProjects.addAll(projects)
        }
        return foundProjects
    }
    /**
     * Given an projectId, get detailed Project information from the REST API
     * @param projectId
     * @return
     * TODO: switch to use #findProjectsByID
     */
    Project showProject(final Integer projectId) {
        if (projectId) {
            final RESTProjectService restProjectService = this.queryServiceWrapper.getRestProjectService()
            return restProjectService.get(projectId)
        }
        return null
    }
    /**
     * Given an experimentId, get detailed Experiment information from the REST API
     * @param experimentId
     * @return
     */
    Experiment showExperiment(final Integer experimentId) {
        if (experimentId) {
            final RESTExperimentService restExperimentService = this.queryServiceWrapper.getRestExperimentService()
            return restExperimentService.get(experimentId)
        }
        return null
    }


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