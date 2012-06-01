package bardqueryapi

class QueryCompoundApiService extends QueryExecutorService {

   //Compound
    ///v1/compounds/{id} - Retrieve compound by its CID
    ///v1/compounds/sid/{id} - Retrieve compound by it SID
    ///v1/compounds/probeid/{id} - Retrieve compound by its probe id
    //By default the return value is JSON list of links to compound resources.
    // It is possible to retrieve the compound in SDF or SMILES format by
    // specifying the “Accept: chemical/x-mdl-sdfile” or
    // Accept: chemical/x-daylight-smiles" request headers respectively.
    def findCompoundByCID(final String cidUrl, final Map headers) {
        final String url = grailsApplication.config.ncgc.server.root.url + cidUrl
        println url
        if(headers){
            return executeGetRequestString(url,headers)
        }
        return executeGetRequestJSON(url,null)
    }
    /**
     *
     * @param sidUrl
     * @param headers
     * @return
     */
    def findCompoundBySID(final String sidUrl, final Map headers) {
        final String url = grailsApplication.config.ncgc.server.root.url +  sidUrl
        println url
        if(headers){
            return executeGetRequestString(url,headers)
        }
        return executeGetRequestJSON(url,null)
    }
    /**
     *
     * @param probeUrl
     * @param headers
     * @return
     */
    def findCompoundByProbeId(final String probeUrl, final Map headers) {
        final String url = grailsApplication.config.ncgc.server.root.url +  probeUrl
        println url
        if(headers){
            return executeGetRequestString(url,headers)
        }
        return executeGetRequestJSON(url,null)
    }
}
