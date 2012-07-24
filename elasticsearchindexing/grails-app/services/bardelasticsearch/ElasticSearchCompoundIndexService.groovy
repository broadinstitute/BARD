package bardelasticsearch

import wslite.rest.RESTClient
import wslite.json.JSONArray

//import XML
//import JSONObject


class ElasticSearchCompoundIndexService extends ElasticSearchIndexAbstractService {

private final static String mappingForCompound = '''
{
  "compound":{
    "properties":{
      "cid":{
        "index":"not_analyzed",
        "type":"string"
      },
      "probeId":{
        "index":"not_analyzed",
        "type":"string"
      },
      "sids":{
        "index":"not_analyzed",
        "type":"array"
      },
      "apids":{
        "index":"not_analyzed",
        "type":"array"
      },
      "url":{
        "index":"not_analyzed",
        "type":"string"
      },
      "smiles":{
        "index":"not_analyzed",
        "type":"string"
      }
    }
  }
}
'''




public void createIndexAndMappingForCompoundType(final String indexName, final String indexType) {
    RESTClient restClientClone = this.restClientFactoryService.createNewRestClient("${this.elasticSearchURL}${indexName}")

    //create the index  if it does not already exist
    putRequest(restClientClone, "")

    //construct the mapping url
    final String mappingUrl = "${elasticSearchURL}${indexName}/${indexType}/_mapping"

    restClientClone.url = mappingUrl

    //Create the mappings
    putRequest(restClientClone, mappingForAssay)
}

/**
 *  Fetches all the assays from NCGC,
 *  for each assay fetch the JSON, then index it
 *  into elasticsearch
 */
public void indexCompound(final String indexName, final String indexType, final String ncgcAssaysURL) {

    final RESTClient restClientClone = this.restClientFactoryService.createNewRestClient(ncgcAssaysURL)

    //fetch the list of all assays from NCGC
    final def jsonArray = this.executeGetRequestJSON(restClientClone)
    assert jsonArray

    assert jsonArray instanceof JSONArray

    final JSONArray assaysAsJSON = (JSONArray) jsonArray
    final List<String> assays = assaysAsJSON.subList(0, assaysAsJSON.length())



    int counter = 0
    //for each assay  get the JSON doc from NCGC and index it
    for (String assay : assays) {
        ++counter
        //strip the relative url off the string
        //the string looks like /bard/rest/v1/assays/123 will be 123 after striping
        final String assayId = assay.toString().replaceAll('/bard/rest/v1/assays/', '')

        //construct url to fetch assay json from NCGC
        final String assayUrl = "${ncgcAssaysURL}${assayId}"
        restClientClone.url = assayUrl
        //get assay document from NCGC
        final String requestToPut = this.executeGetRequestJSON(restClientClone)

        //construct the url to use for indexing this document
        final String indexUrl = "${this.elasticSearchURL}${indexName}/${indexType}/${assayId}"

        // Form the put command
        restClientClone.url = indexUrl
        putRequest(restClientClone, requestToPut)
    }
    println "# ${counter} Compound indexed"
}
}
