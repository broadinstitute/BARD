package bardelasticsearch

//import XML
//import JSONObject


import wslite.json.JSONArray
import wslite.json.JSONObject
import wslite.rest.RESTClient

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

class ElasticSearchCompoundsIndexService extends ElasticSearchIndexAbstractService {

    //failures while indexing index 'compounds' and type 'compound'
    final Set<String> compoundsFailedIndexing = [] as Set<String>

    //failures while indexing index 'assays', with type 'compound'
    final Set<String> assayCompoundsFailedIndexing = [] as Set<String>

    ExecutorService executorService  //runs method concurrently
    int numberOfThreads = 10   //defaults to 10
    //Fetches all of the cids in the assays/compound index, in elastic search
    final static String ELASTIC_COMPOUNDS_SEARCH = ''' {
  "fields": [
    "cids"
  ],
  "query": {
    "query_string": {
      "fields": [
        "cids"
      ],
      "query": "*"
    }
  }
}
'''

    /**
     *
     * @param ncgcExperimentsURL
     * @param assays
     */
    public void startIndexingAssayCompoundsConcurrently(final String ncgcExperimentsURL, final JSONArray assays) {
        final int numberOfAssays = assays.length()
        //we want to get the number of assays per thread
        final int numberOfAssaysPerThread = numberOfAssays / numberOfThreads

        def tasks = []
        for (int i = 0; i < numberOfThreads; i++) {
            final int firstIndex = i * numberOfAssaysPerThread;
            final int lastIndex = i != (numberOfThreads - 1) ? (i + 1) * numberOfAssaysPerThread : numberOfAssays;
            final List assaySubList = assays.subList(firstIndex, lastIndex);
            final String threadName = "Thread" + i

            // When the curry() method is called on a closure instance with one or more arguments,
            // a copy of the closure is first made. The incoming arguments are then bound permanently
            // to the new closure instance so that the parameters 1..N to the curry()
            // call are bound to the 1..N parameters of the closure. The new curried closure is then returned the caller.
            tasks << (indexAssayCompoundsClosure.curry(ncgcExperimentsURL, assaySubList, threadName) as Callable)
        }
        //now execute all tasks
        final List<FutureTask> futureTasks = executorService.invokeAll(tasks)
        futureTasks.each {futureTask ->
            log.info "Future Task: " + futureTask.get()
        }
    }
    /**
     * Allows us to run it in a multi-threaded fashion using the executor service plugin
     */
    def indexAssayCompoundsClosure = {String ncgcExperimentsURL, List assays, String threadName ->
        String compoundsForAssayURL = null
        try {
            final RESTClient restClientClone = this.restClientFactoryService.createNewRestClient(ncgcExperimentsURL)

            println "Executing ${threadName}"
            int counter = 1
            int numberOfAssays = assays.size()
            for (String assay : assays) {
                //strip out the string /bard/rest/v1/assays/223 from the current, because we want only the aid
                final String aid = assay.toString().replaceAll('/bard/rest/v1/assays/', '')

                //construct url to fetch the compounds associated with the current aid
                compoundsForAssayURL = "${ncgcExperimentsURL}${aid}/compounds"
                restClientClone.url = compoundsForAssayURL
                if (counter % numberOfThreads == 0) {
                    println "Executing ${threadName} : (${counter} of ${numberOfAssays})"
                }
                //fetch all the compounds associated with this assay
                indexAssayCompounds(restClientClone, aid, compoundsForAssayURL, threadName)
                ++counter
            }
        } catch (Exception ee) {
            println "Error:  ${threadName} ${compoundsForAssayURL}"
            ee.printStackTrace()
            log.error(ee.message)
            return (threadName + " did not execute successfully")
        }
        return (threadName + " executed successfully")

    }
    /**
     * Index each compound.  This step requires a query to to NCGC API.  If the
     *  number of compounds returned exceeds the maximum then step through all
     *  the associated compounds.
     *
     * @param aid
     * @param compoundsForAssayURL
     */
    protected void indexAssayCompounds(final RESTClient restClientClone, final String aid, final String compoundsForAssayURL, final String threadName) {
        try {
            //fetch the compounds associated with the current aid
            def compoundsArray = this.executeGetRequestJSON(restClientClone)
            if (!compoundsArray instanceof JSONObject) {
                log.error("${compoundsArray.toString()} URL: ${compoundsForAssayURL}")
                return
            }
            //list of compounds are located in the collection
            final JSONArray compoundCollection = ((JSONObject) compoundsArray).collection

            //if the collection is not empty
            if (!compoundCollection.isEmpty()) {
                //if there are more compounds they would be included in the link object
                final String moreCompoundsLink = ((JSONObject) compoundsArray).link

                //recursively fetch compounds
                if (moreCompoundsLink != 'null') {
                    recursivelyFetchCompounds(restClientClone, moreCompoundsLink.toString().replaceFirst('/', ''), compoundCollection, threadName)
                }
                //strip out the string /bard/rest/v1/compounds/223 from the current, because we want only the cid
                final String cids = compoundCollection.toString().replaceAll('/bard/rest/v1/compounds/', '')
                //then we reconstruct the JSON with the aid
                final String cidJson = "{aid: ${aid}, cids:${cids}}"
                // println cidJson
                final JSONObject requestToSend = new JSONObject(cidJson)
                //construct the URL and then index the document
                final String urlForIndexing = "${elasticSearchURL}assays/compound/${aid}"
                restClientClone.url = urlForIndexing

                //index document
                this.putRequest(restClientClone, requestToSend.toString())

            }
        } catch (Exception ee) {
            //add the aids for compounds we could not index successfully
            assayCompoundsFailedIndexing.add(aid)
            log.error(ee)
        }
    }
/**
 * If there are more compounds we recursively call this method
 */
    protected void recursivelyFetchCompounds(final RESTClient restClientClone, final String moreCompoundsLink, final JSONArray compoundCollection, String threadName) {
        //moreCompoundLinks is something like /bard/rest/v1/experiments/346/compounds?skip=500&top=500&expand=false
        final String compoundsForAssayURL = "${ncgcRootURL}${moreCompoundsLink}"

        restClientClone.url = compoundsForAssayURL
        def compoundsArray = this.executeGetRequestJSON(restClientClone)
        if (!compoundsArray instanceof JSONObject) {
            log.error("${compoundsArray.toString()} URL: ${compoundsForAssayURL}")
            return
        }

        final JSONArray collection = ((JSONObject) compoundsArray).collection
        final String moreCompounds = ((JSONObject) compoundsArray).link
        //if the collection is not empty
        if (!collection.isEmpty()) {
            compoundCollection.addAll(collection)
        }

        //if there are more compounds, call method again
        if (moreCompounds != 'null') {
            recursivelyFetchCompounds(restClientClone, moreCompounds.toString().replaceFirst('/', ''), compoundCollection, threadName)
        }
    }
    /**
     * Allows us to run it in a multi-threaded fashion using the executor service plugin
     */
    def indexCompoundsClosure = {String ncgcCompoundsURL, List<String> cids, String threadName ->
        String compoundURL = null
        try {
            println "Executing ${threadName}"
            int counter = 1
            int numberOfCids = cids.size()
            cids.each {cid ->
                //construct url to fetch the compounds associated with the current aid
                compoundURL = "${ncgcCompoundsURL}${cid}"
                if (counter % 1000 == 0) {
                    println "Executing ${threadName} : (${counter} of ${numberOfCids})"
                }
                //fetch the compound JSON from NCGC and index it
                indexCompounds(cid, compoundURL, threadName)
                ++counter
            }
        } catch (Exception ee) {
            println "Error:  ${threadName} ${compoundURL}"
            // ee.printStackTrace()
            log.error(ee.message)
            return (threadName + " did not execute successfully")
        }
        return (threadName + " executed successfully")

    }
    /**
     * Process that starts indexing all cids in NCGC
     * We do this concurrently
     * @param ncgcCompoundsURL
     *
     */
    public void startIndexingCompounds(final String ncgcCompoundsURL) {
        final List<String> cids = fetchAllUniqueCIDsFromElasticSearch()
        final int numberOfCids = cids.size()
        //we want to get the number of cids per thread
        final int numberOfCidsPerThread = numberOfCids / numberOfThreads
        def tasks = []
        for (int i = 0; i < numberOfThreads; i++) {
            final int firstIndex = i * numberOfCidsPerThread;
            final int lastIndex = i != (numberOfThreads - 1) ? (i + 1) * numberOfCidsPerThread : numberOfCids;
            final List<String> cidSubList = cids.subList(firstIndex, lastIndex);
            final String threadName = "Thread" + i
            tasks << (indexCompoundsClosure.curry(ncgcCompoundsURL, cidSubList, threadName) as Callable)
        }

        //now execute all tasks
        final List<FutureTask> futureTasks = executorService.invokeAll(tasks)
        futureTasks.each {futureTask ->
            println "Future Task: " + futureTask.get()
        }
    }
    /**
     * Index each compound.  This step requires a query to to NCGC API to get the JSON
     * for the cid and then we index it in ES
     *
     * @param cid
     * @param compoundURL
     * @param threadName
     */
    public void indexCompounds(final String cid, final String compoundURL, final String threadName) {
        //fetch the compounds associated with the currentcid
        try {
            RESTClient restClientClone = this.restClientFactoryService.createNewRestClient(compoundURL)
            def compoundJSON = this.executeGetRequestJSON(restClientClone)
            if (!compoundJSON instanceof JSONObject) {
                log.error("${compoundJSON.toString()} URL: ${compoundURL}")
                return
            }
            final JSONObject requestToPut = (JSONObject) compoundJSON
            //construct the URL and then index the document
            final String urlForIndexing = "${elasticSearchURL}compounds/compound/${cid}"
            restClientClone.url = urlForIndexing
            //index document
            this.putRequest(restClientClone, requestToPut.toString())
        } catch (Exception ee) {
            compoundsFailedIndexing.add(compoundURL)
            println "Error: ${compoundURL}"
            log.error(ee)
        }
    }
    /**
     * We find all unique CIDs in ElasticSearch
     * @return list of unique cids found in ES
     */
    protected List<String> fetchAllUniqueCIDsFromElasticSearch() {
        final Set<String> cidSet = [] as Set<String>

        //the url for searching ES
        final String urlForSearching = "${elasticSearchURL}assays/compound/_search"

        final RESTClient cloneRestClient = this.restClientFactoryService.createNewRestClient(urlForSearching)

        //We post the request to ES
        def response = this.postRequest(cloneRestClient, ELASTIC_COMPOUNDS_SEARCH)

        //we only want to process JSONObject responses
        if (response instanceof JSONObject) {
            final JSONObject jsonResponse = (JSONObject) response

            //get the fields value from the JSONObject
            final List<JSONObject> fields = jsonResponse.hits?.hits?.fields

            if (fields) {
                //collect all the unique cids
                for (JSONObject field : fields) {
                    final JSONArray cids = field.cids
                    final List<String> allCIDsInTheCurrentField = cids.subList(0, cids.length())
                    cidSet.addAll(allCIDsInTheCurrentField)
                }
            }
        }
        return cidSet as List<String>
    }
}