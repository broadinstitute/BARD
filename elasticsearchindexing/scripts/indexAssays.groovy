//get the service from the context
elasticSearchIndexService = ctx.getBean("elasticSearchIndexService")
def grailsApp =ctx.getBean("grailsApplication") 
elasticRootURL = "http://bard-dev-vm:9200/"
//set the url to elasticsearch server
elasticSearchIndexService.elasticSearchURL = elasticRootURL 


//The root url to the ncgc server
ncgcRootURL=elasticSearchIndexService.ncgcRootURL

//The relative url to the ncgc server something like bard/rest/v1/
def ncgcRelativeURL=elasticSearchIndexService.ncgcRelativeURL

//url to NCGC to fetch assays
String ncgcAssaysURL = "${ncgcRootURL}${ncgcRelativeURL}assays/"
try{
createIndexAndMappingForAssayType("assays","assay")
indexAssays(ncgcAssaysURL)
}catch(Exception ee){
 elasticSearchIndexService.logMessage(ee.message,"error");
}


     /**
     *  Fetches all the assays from NCGC,
     *  for each assay fetch the JSON, then index it
     *  into elasticsearch
     */
    public void indexAssays(String ncgcAssaysURL) {

        final def jsonArray = elasticSearchIndexService.executeGetRequestJSON(ncgcAssaysURL)
        int counter = 0
        jsonArray.each {assay ->
            ++counter
            //strip the relative url off the string
            //the string looks like /bard/rest/v1/assays/123
            final String assayId = assay.toString().replaceAll('/bard/rest/v1/assays/', '')
            try {
                final String assayUrl = "${ncgcAssaysURL}${assayId}"
                   //get assay document from NCGC
                final String requestToSend = elasticSearchIndexService.executeGetRequestJSON(assayUrl)
                
                //now index the current assay json document
               indexDocument("assays", "assay", assayId, requestToSend)

            } catch (Exception ee) {
                println ee.message
                //println("assayId ${assayId} not found")
               
            }
        }
        println "# ${counter} indexed"
    }
    
    
    /**
     *  Take each document and place it into Elastic Search for indexing
     *
     * @param indexName
     * @param indexType
     * @param id
     * @param requestToSend
     */
    void indexDocument(final String indexName, final String indexType, final String id, final String requestToSend) {
        final String indexUrl = "${elasticRootURL}${indexName}/${indexType}/${id}"

        // Form the put command      
        def restClient = elasticSearchIndexService.restClient
        restClient.url = indexUrl
        
        //insert into elastic search   
        def response = restClient.put() {
         text requestToSend
        }
      println response.statusCode
      println response.statusMessage
    }
    
    
    /**
     * Create a mapping for the assay type
     * This mapping marks some fields as "not_analyzed" ('not analyzed' means that the field in not compressed
     * via space removal, but instead retained exactly as it was received for exact term matching
     */
    public void createIndexAndMappingForAssayType(final String indexName, final String indexType) {
        
//create the index
try{
    def restClient = elasticSearchIndexService.restClient
        restClient.url = "${elasticRootURL}/${indexName}"
          restClient.put() {
         text ""
    }
    }
    catch(Exception ee){
        println ee.message
        elasticSearchIndexService.logMessage(ee.message,"error");
        
    }
        final String requestToSend = '''
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

        //construct the mapping url
        final String mappingUrl = "${elasticRootURL}/${indexName}/${indexType}/_mapping"
       
        def restClient = elasticSearchIndexService.restClient
        restClient.url = mappingUrl
          restClient.put() {
          text  requestToSend
      }
}