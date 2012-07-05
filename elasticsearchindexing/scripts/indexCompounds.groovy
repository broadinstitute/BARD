//get the service from the context
elasticSearchIndexService = ctx.getBean("elasticSearchIndexService")
def grailsApp =ctx.getBean("grailsApplication") 
elasticRootURL = grailsApp.config.elasticsearch.server.url
//set the url to elasticsearch server
elasticSearchIndexService.elasticSearchURL = elasticRootURL 


//The root url to the ncgc server
ncgcRootURL=elasticSearchIndexService.ncgcRootURL

//The relative url to the ncgc server something like bard/rest/v1/
def ncgcRelativeURL=elasticSearchIndexService.ncgcRelativeURL
 

//the index name for compounds
String compoundIndexName = "compounds"
//the type for compounds
String compoundIndexType = "compound"

//url to NCGC to fetch compounds
final String ncgcCompoundsURL = "${ncgcRootURL}${ncgcRelativeURL}compounds/"

//url to NCGC to fetch experiments
final String ncgcExperimentsURL = "${ncgcRootURL}${ncgcRelativeURL}experiments/"

//url to NCGC to fetch assays
final String ncgcAssaysURL = "${ncgcRootURL}${ncgcRelativeURL}assays/"
//create the index

try {
    createIndexForCompounds()


   //lets fetch all of the aids from NCGC
   def jsonArray = elasticSearchIndexService.executeGetRequestJSON(ncgcAssaysURL)



   //iterate over the aids
   jsonArray.each {assay ->

       //strip out the string /bard/rest/v1/assays/223 from the current, because we want only the aid
       final String aid = assay.toString().replaceAll('/bard/rest/v1/assays/', '')
 
       //construct url to fetch the compounds associated with the current aid
       final String compoundsForAssayURL = "${ncgcExperimentsURL}${aid}/compounds"


       //fetch all the compounds associated with this assay
       indexCompounds(aid, compoundsForAssayURL)
       }
 } catch (Exception ee) {
 
  elasticSearchIndexService.logMessage(ee.message.toString(), "error")
  ee.printStackTrace()
 
 }

void createIndexForCompounds(){
try{
def restClient = elasticSearchIndexService.restClient
        restClient.url = "${elasticRootURL}assays"
          restClient.put() {
         text ""
}
}catch(Exception ee){
    elasticSearchIndexService.logMessage(ee.message.toString(), "error") 
}



}



    /**
     * Index each compound.  This step requires a query to to NCGC API.  If the
     *  number of compounds returned exceeds the maximum then step through all
     *  the associated compounds.
     * 
     * @param aid
     * @param compoundsForAssayURL
     */
void indexCompounds(String aid, String compoundsForAssayURL) {
    //fetch the compounds associated with the current aid
    def compoundsArray = elasticSearchIndexService.executeGetRequestJSON(compoundsForAssayURL)
    //println compoundsArray
    //list of compounds are located in the collection
    def compounds = compoundsArray.collection
   // compounds.put("aid", aid)
    //if there are more compounds they would be included in the link
    def moreCompounds = compoundsArray.link

    println "More compounds: ${moreCompounds}"
    //iterate over each compound
    compounds.each {compound ->
        //strip out the url,, so we have just the cid
        final String cidURL = compound.toString().replaceFirst('/', '')
         final String cid = compound.toString().replaceAll('/bard/rest/v1/compounds/', '')
        // println cid
        final String compUrl = "${ncgcRootURL}${cidURL}"
        println compUrl
        
       def compoundJSON = elasticSearchIndexService.executeGetRequestJSON(compUrl)
       compoundJSON.put("aid", aid)
        def restClient = elasticSearchIndexService.restClient
        restClient.url = "${elasticRootURL}/assays/compound/${cid}"
          restClient.put() {
          text compoundJSON.toString()
      }
        println compoundJSON
    }
  }

   
    /**
     * Insert the document into Elastic Search by indexing the term.
     * 
     * @param indexName
     * @param indexType
     * @param id
     * @param requestToSend
     */
    void indexDocument(final String indexName, final String indexType, final String id, final String requestToSend) {
        final String indexUrl = "${elasticSearchURL}${indexName}${indexType}${id}"
        this.restClient.url = indexUrl
        def restClient = elasticSearchIndexService.restClient
        restClient.url = "${indexUrl}"
          restClient.put() {
         text String requestToSen
     }
    }
   