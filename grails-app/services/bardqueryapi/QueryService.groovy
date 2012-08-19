package bardqueryapi

import bard.QueryServiceWrapper
import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESXCompound
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

//    Map searchCompounds(final String phrase, final LinkedHashMap additionalParms = [:]) {
//
//    }
//
//    Map searchAssays(final String phrase, final LinkedHashMap additionalParms = [:]) {
//
//        this.queryExecutorService.executeGetRequestString(phrase, [:])
//    }
//
//    Map searchProjects(final String phrase, final LinkedHashMap additionalParms = [:]) {
//
//
//    }
//
//    Map searchExperiments(final String phrase, final LinkedHashMap additionalParms = [:]) {
//
//
//    }
    List<CompoundAdapter> structureSearch(final String smiles,final StructureSearchParams.Type structureSearchParamsType ){
        List<CompoundAdapter> compounds = []
        if (smiles) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()
            ServiceIterator<Compound> iter  = restCompoundService.structureSearch(new StructureSearchParams
            (smiles,structureSearchParamsType));
            while (iter.hasNext()) {
                Compound compound = iter.next();
                compounds.add(new CompoundAdapter(compound))
             }
        }
        return compounds
    }
    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return
     */
    Assay showAssay(final Integer assayId) {
        if (assayId) {
            final RESTAssayService restAssayService = this.queryServiceWrapper.getRestAssayService()
            return restAssayService.get(assayId)
        }
        return null
    }
    /**
     * Given an projectId, get detailed Project information from the REST API
     * @param projectId
     * @return
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
                return  new CompoundAdapter(compound)
            }
        }
        return null
    }
    /**
     * We pre-process the search String, to extract the type of search
     * Structure searches at this point, requires that we get the CIDs and then
     * use them to do another search. This should go away probably in the next iteration
     * when we start using NCGC resources directly instead of ES
     * @param searchString
     * @return
     */
    protected String preprocessSearch(String searchString) {
        //if the search string is a structure search, then do a structure search, otherwise
        //do a regular search
        if (searchString) {
            final String[] searchStringSplit = searchString.split(":")
            final String searchType = searchStringSplit[0]
            List<String> molecules = []
            switch (searchType) {
                case StructureSearchType.EXACT_MATCH.description:
                    molecules = this.getCIDsByStructure(searchStringSplit[1], StructureSearchType.EXACT_MATCH)
                    break
                case StructureSearchType.SIMILARITY.description:
                    molecules = this.getCIDsByStructure(searchStringSplit[1], StructureSearchType.SIMILARITY)
                    break
                case StructureSearchType.SUB_STRUCTURE.description:
                    molecules = this.getCIDsByStructure(searchStringSplit[1], StructureSearchType.SUB_STRUCTURE)
                    break
                default:
                    return searchString
            }
            if (molecules) {
                return molecules.join(' ')
            }
        }
        return ""
    }
    /**
     * 1. Collect all assay documents from assays/assay (goes as primary to assay tab)
     * 2. Collect all compound documents from compound/Xcompound (goes as primary to compound tab)
     * 3. Collect all assay documents from compound/Xcompound.apids[] (goes as secondary to assay tab)
     * 4. Collect all compound documents from compound/Xcompound (goes as secondary to compound tab). this is an ElasticSearch hit on Xcompound.aid[]
     * @param searchString
     * @return
     */
    public Map<String, List> search(final String userInput) {
        //preprocess to switch on the type of search we are about to do
        final String searchString = preprocessSearch(userInput)
        final Set<String> compounds = [] as Set
        final List<Map> assays = []
        Map<String, List> result = [:]
        if (searchString) {
            result = this.elasticSearchService.elasticSearchQuery(searchString)

            for (ESAssay assay in result.assays) {
                String assayString = assay.toString()
                String showAssayResource = "${bardAssayViewUrl}/${assay.assayNumber}"
                def assayMap = [assayName: assayString, assayResource: showAssayResource, assayNumber: assay.assayNumber] as Map
                assays.add(assayMap)
            }
            for (ESXCompound compound in result.xcompounds) {
                compounds.add(compound)
            }
        }
        return [totalCompounds: compounds.size(), assays: assays as List<Map>, compounds: compounds.toList(), compoundHeaderInfo: result.compoundHeaderInfo, experiments: [], projects: []]
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
    /**
     * Query NCGC REST API for a list of CIDs based on a structure (SIMLES) provided; the query could be of type: exact-match, sub-structure search or similarity-search.
     * Also see: https://github.com/ncatsdpiprobedev/bard/wiki/Structure-search
     *
     * @param structureSearchType
     * @param smiles
     * @return
     *
     * TODO: NCGC JDO is not yet implemented
     */
    protected List<String> getCIDsByStructure(final String smiles, final StructureSearchType searchType) {
        /**
         * Build the NCGC REST call-url.
         * For example: http://assay.nih.gov/bard/rest/v1/compounds?filter=n1cccc2ccccc12[structure]&type=sim&cutoff=0.9
         *  type - can be sub, super, exact or sim
         *  cutoff - the similarity cutoff if a similarity search is desired
         */
        String searchModifiers = null
        switch (searchType) {
            case StructureSearchType.SUB_STRUCTURE:
                searchModifiers = '&type=sub'
                break
            case StructureSearchType.SIMILARITY:
                searchModifiers = "&type=sim&cutoff=0.9"
                break
            case StructureSearchType.EXACT_MATCH:
                searchModifiers = '&type=exact'
                break
            default:
                throw new RuntimeException("Undeifined structure-search type")
                break
        }

        String searchUrl = "${ncgcSearchBaseUrl}?filter=${smiles}[structure]${searchModifiers}"
        def resultJson = queryExecutorService.executeGetRequestJSON(searchUrl, [connectTimeout: 5000, readTimeout: 10000])

        //now use this to call ElasticSearch
        //Strip the CID from the end part of a compound's relative resource-url. e.g.: /bard/rest/v1/compounds/6796
        final List<String> molecules = resultJson.collect { String compoundUri ->
            compoundUri.split('/').last()
        }
        return molecules
    }
}
public enum StructureSearchType {
    EXACT_MATCH("Structure"),
    SUB_STRUCTURE("SubStructure"),
    SIMILARITY("Similarity");

    final String description

    StructureSearchType(String description) {
        this.description = description
    }

    String getDescription() {
        return this.description;
    }
}

