package bardqueryapi

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

import java.util.regex.Matcher
import java.util.regex.Pattern

import bard.core.*

class QueryService {

    final Pattern ID_PATTERN =
        Pattern.compile("^([0-9]+,? *)+")
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

    public QuerySearchType getQuerySearchType(String searchString) {

        final Matcher matcher = ID_PATTERN.matcher(searchString)

        if (matcher.matches()) { //matches an ID search
            return QuerySearchType.ID
        }
        if (isStructureSearch(searchString)) {
            return QuerySearchType.STRUCTURE
        }
        return QuerySearchType.REGULAR
    }
    /**
     *
     * @param searchString
     * @return true if this is a structure search, false otherwise
     * Structure searches are of the form
     *  StructureSearchParams.Type:SMILES_STRING
     */
    protected boolean isStructureSearch(String searchString) {

        try {
            if (searchString) {
                final String[] searchStringSplit = searchString.split(":")
                final StructureSearchParams.Type searchType = searchStringSplit[0] as StructureSearchParams.Type
                if (searchStringSplit.length == 2) {//must be if the form SubStructure:CC
                    switch (searchType) {
                        case StructureSearchParams.Type.Substructure:
                        case StructureSearchParams.Type.Similarity:
                        case StructureSearchParams.Type.Exact:
                        case StructureSearchParams.Type.Superstructure:
                            return true
                        default:
                            return false
                    }
                }
            }
        } catch (Exception ee) {
            log.error(ee)
        }
        return false
    }
    /**
     *
     * @param searchString
     * @return true if this is a structure search, false otherwise
     * Structure searches are of the form
     *  StructureSearchParams.Type:SMILES_STRING
     */
    public StructureSearchParams.Type getStructureSearchType(String searchString) {

        try {
            if (searchString) {
                final String[] searchStringSplit = searchString.split(":")
                final StructureSearchParams.Type searchType = searchStringSplit[0] as StructureSearchParams.Type
                return searchType
            }
        } catch (Exception ee) {
            log.error(ee)
        }
        return false
    }

    List<CompoundAdapter> structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final int top = 10, final int skip = 0) {
        List<CompoundAdapter> compounds = []
        if (smiles) {
            final RESTCompoundService restCompoundService = this.queryServiceWrapper.getRestCompoundService()

            final StructureSearchParams structureSearchParams =
                new StructureSearchParams(smiles)
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
                return new CompoundAdapter(compound)
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
//        if (searchString) {
//            final String[] searchStringSplit = searchString.split(":")
//            final String searchType = searchStringSplit[0]
//            List<String> molecules = []
//            switch (searchType) {
//                case StructureSearchType.EXACT_MATCH.description:
//                    molecules = this.getCIDsByStructure(searchStringSplit[1], StructureSearchType.EXACT_MATCH)
//                    break
//                case StructureSearchType.SIMILARITY.description:
//                    molecules = this.getCIDsByStructure(searchStringSplit[1], StructureSearchType.SIMILARITY)
//                    break
//                case StructureSearchType.SUB_STRUCTURE.description:
//                    molecules = this.getCIDsByStructure(searchStringSplit[1], StructureSearchType.SUB_STRUCTURE)
//                    break
//                default:
//                    return searchString
//            }
//            if (molecules) {
//                return molecules.join(' ')
//            }
//        }
        return searchString
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
//    protected List<String> getCIDsByStructure(final String smiles, final StructureSearchType searchType) {
//        /**
//         * Build the NCGC REST call-url.
//         * For example: http://assay.nih.gov/bard/rest/v1/compounds?filter=n1cccc2ccccc12[structure]&type=sim&cutoff=0.9
//         *  type - can be sub, super, exact or sim
//         *  cutoff - the similarity cutoff if a similarity search is desired
//         */
//        String searchModifiers = null
//        switch (searchType) {
//            case StructureSearchType.SUB_STRUCTURE:
//                searchModifiers = '&type=sub'
//                break
//            case StructureSearchType.SIMILARITY:
//                searchModifiers = "&type=sim&cutoff=0.9"
//                break
//            case StructureSearchType.EXACT_MATCH:
//                searchModifiers = '&type=exact'
//                break
//            default:
//                throw new RuntimeException("Undeifined structure-search type")
//                break
//        }
//
//        String searchUrl = "${ncgcSearchBaseUrl}?filter=${smiles}[structure]${searchModifiers}"
//        def resultJson = queryExecutorService.executeGetRequestJSON(searchUrl, [connectTimeout: 5000, readTimeout: 10000])
//
//        //now use this to call ElasticSearch
//        //Strip the CID from the end part of a compound's relative resource-url. e.g.: /bard/rest/v1/compounds/6796
//        final List<String> molecules = resultJson.collect { String compoundUri ->
//            compoundUri.split('/').last()
//        }
//        return molecules
//    }
}

public enum QuerySearchType {
    STRUCTURE("Structure"),
    ID("Id"),
    REGULAR("Regular");

    final String description

    QuerySearchType(String description) {
        this.description = description
    }

    String getDescription() {
        return this.description;
    }

}

