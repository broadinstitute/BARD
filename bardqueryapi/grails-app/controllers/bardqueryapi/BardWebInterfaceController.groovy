package bardqueryapi

import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESXCompound
import elasticsearchplugin.ElasticSearchService
import wslite.json.JSONObject

/**
 * TODO: Unify the use of JSONObject
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(AutoCompleteHelper)
class BardWebInterfaceController {

    ElasticSearchService elasticSearchService
    QueryExecutorInternalService queryExecutorInternalService

    def index() {
        homePage()
    }

    def homePage() {
        render(view: "homePage", totalCompounds: 0, model: [assays: [], compounds: [], experiments: [], projects: []])
    }
    /**
     * TODO: This will require refactoring after this iteration
     * when we add more functionality
     * @return
     */
    def search() {
        /**
         * TODO - How to build the Search result set
         * 1. Collect all assay documents from assays/assay (goes as primary to assay tab)
         * 2. Collect all compound documents from compound/Xcompound (goes as primary to compound tab)
         * 3. Collect all assay documents from compound/Xcompound.apids[] (goes as secondary to assay tab)
         * 4. Collect all compound documents from compound/Xcompound (goes as secondary to compound tab). this is an ElasticSearch hit on Xcompound.aid[]
         */
        def searchString = params.searchString?.trim()
        if (searchString) {
            Map<String, List> result = elasticSearchService.elasticSearchQuery(searchString)

            List<Map> assays = []
            for (ESAssay assay in result.assays) {
                String assayString = assay.toString()
                String bardAssayViewUrl = grailsApplication.config.bard.assay.view.url
                String showAssayResource = "${bardAssayViewUrl}/${assay.assayNumber}"
                def assayMap = [assayName: assayString, assayResource: showAssayResource, assayNumber: assay.assayNumber] as Map
                assays.add(assayMap)
            }

            Set<String> compounds = [] as Set
            for (ESXCompound compound in result.xcompounds) {
                compounds.add(compound)
                // Note:  if we want to perform a secondary search and pull back the APID records for each compound
                //  then we could use the lines below.  As of code review on July 26 I am leaving them commented out,
                //  since the result of these extra records seems undesirable.
//                for (int apid in compound?.apids) {
//                    def assayMap = [assayName: "assay referencing cid=${compound.cid}", assayResource: "referenced assay", assayNumber: apid] as Map
//                    assays.add(assayMap)
//                }
            }

            render(view: "homePage", model: [totalCompounds: compounds.size(), assays: assays as List<Map>, compounds: compounds.toList(), compoundHeaderInfo: result.compoundHeaderInfo ,experiments: [], projects: []])
            return
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }


    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            JSONObject compoundESDocument = elasticSearchService.getCompoundDocument(compoundId)
            JSONObject compoundJson = [cid: compoundESDocument?._id,
                    sids: compoundESDocument?._source?.sids,
                    probeId: compoundESDocument?._source?.probeId,
                    smiles: compoundESDocument?._source?.smiles] as JSONObject
            render(view: "showCompound", model: [compoundJson: compoundJson, compoundId: compoundId])
        }
        else {
            render "Compound ID (CID) parameter required"
        }
    }
    def autoCompleteAssayNames() {
        final String elasticSearchRootURL = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        final List<String> assayNames = handleAutoComplete(this.elasticSearchService, elasticSearchRootURL)

        render(contentType: "text/json") {
            for (String assayName: assayNames) {
                element assayName
            }
            if (!assayNames){
                element ""
            }
        }
    }


    /**
     * An Action to provide a search-call to NCGC REST API: find CIDs by structure (SMILES).
     * @param smiles
     * @param structureSearchType
     * @return
     */
    def structureSearch(String smiles, String structureSearchType) {

        List<String> molecules = getCIDsByStructureFromNCGC(smiles, structureSearchType)

        if (molecules.isEmpty()) {
            flash.message = message(code: 'structure.search.nonFound', default: 'Structure search could not find any structure')
            render(view: 'homePage', model: [totalCompounds: 0, assays: [] as List<Map>, compounds: [], compoundHeaderInfo: '', experiments: [], projects: []])
            return
        }
        else {
            String searchString = molecules.join(' ')
            redirect(action: "search", params: ['searchString': searchString])
            return
        }
    }

    /**
     * Query NCGC REST API for a list of CIDs based on a structure (SIMLES) provided; the query could be of type: exact-match, sub-structure search or similarity-search.
     * Also see: https://github.com/ncatsdpiprobedev/bard/wiki/Structure-search
     *
     * @param structureSearchType
     * @param smiles
     * @return
     */
    protected List<String> getCIDsByStructureFromNCGC(String smiles, String structureSearchType) {
        StructureSearchType searchType = structureSearchType as StructureSearchType
        String searchString = ''

        String ncgcSearchBaseUrl = grailsApplication.config.ncgc.server.structureSearch.root.url

        /**
         * Build the NCGC REST call-url.
         * For example: http://assay.nih.gov/bard/rest/v1/compounds?filter=n1cccc2ccccc12[structure]&type=sim&cutoff=0.9
         *  type - can be sub, super, exact or sim
         *  cutoff - the similarity cutoff if a similarity search is desired
         */
        String searchModifiers
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
        def resultJson = queryExecutorInternalService.executeGetRequestJSON(searchUrl, null)

        //Strip the CID from the end part of a compound's relative resource-url. e.g.: /bard/rest/v1/compounds/6796
        List<String> molecules = resultJson.collect { String compoundUri ->
            compoundUri.split('/').last()
        }

        return molecules
    }
}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
class AutoCompleteHelper {

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
     * Construct a query String query and pass it on to Elastic Search
     * @param elasticSearchService
     * @param elasticSearchRootURL
     * @return
     */
    protected List<String> handleAutoComplete(final ElasticSearchService elasticSearchService,final String elasticSearchRootURL){
        final String urlToElastic = "${elasticSearchRootURL}/${AUTO_COMPLETE_SEARCH_URL}"
        String request = ELASTIC_AUTO_COMPLETE_SEARCH
        if (params?.term) {
            request = ELASTIC_AUTO_COMPLETE_SEARCH.replaceAll("\\*", "${params.term}*")

        }
        final JSONObject jsonObject =  new JSONObject(request)

        final JSONObject responseObject = elasticSearchService.searchQueryStringQuery(urlToElastic,jsonObject)
        return responseObject?.hits?.hits.collect { it.fields }.collect { it.name }

    }

}

public enum StructureSearchType {
    EXACT_MATCH("Exact match"),
    SUB_STRUCTURE("Sub-structure"),
    SIMILARITY("Similarity");

    final String description

    StructureSearchType(String description) {
        this.description = description
    }

    String getDescription() {
        return this.description;
    }
}