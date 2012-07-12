package bardelasticsearch

//import XML
//import JSONObject


import wslite.json.JSONArray
import wslite.rest.RESTClient

class ElasticSearchAssayIndexService extends ElasticSearchIndexAbstractService {
    private final static String mappingForAssay = '''
{
  "assay":{
    "properties":{
      "aid":{
        "index":"not_analyzed",
        "type":"string"
      },
      "grantNo":{
        "index":"not_analyzed",
        "type":"string"
      },
      "resourcePath":{
        "index":"not_analyzed",
        "type":"string"
      },
      "source":{
        "index":"not_analyzed",
        "type":"string"
      },
      "entityTag":{
        "index":"not_analyzed",
        "type":"string"
      },
      "publications":{
        "type":"object",
        "properties":{
          "resourcePath":{
            "index":"not_analyzed",
            "type":"string"
          },
          "pubmedId":{
            "index":"not_analyzed",
            "type":"string"
          },
          "title":{
            "index":"not_analyzed",
            "type":"string"
          },
          "doi":{
            "index":"not_analyzed",
            "type":"string"
          },
          "status":{
            "index":"not_analyzed",
            "type":"string"
          }
        }
      },
      "targets":{
        "type":"object",
        "properties":{
          "resourcePath":{
            "index":"not_analyzed",
            "type":"string"
          },
          "status":{
            "index":"not_analyzed",
            "type":"string"
          }
        }
      }
    }
  }
}
'''
    /**
     * Create a mapping for the assay type
     * This mapping marks some fields as "not_analyzed" ('not analyzed' means that the field in not compressed
     * via space removal, but instead retained exactly as it was received for exact term matching
     */
    public void createIndexAndMappingForAssayType(final String indexName, final String indexType) {
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
    public void indexAssays(final String indexName, final String indexType, final String ncgcAssaysURL) {

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
        println "# ${counter} Assays indexed"
    }
}
