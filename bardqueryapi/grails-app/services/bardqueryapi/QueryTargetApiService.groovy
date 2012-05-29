package bardqueryapi

class QueryTargetApiService extends QueryExecutorService{



    //Target
    ///v1/targets/accession/{acc} - JSON representation of a protein target identified by Uniprot accession

    ///v1/targets/geneid/{id} - JSON representation of a protein target identified by Entrez gene ID

    //Note that the filter parameter is not handled for the above resources, since both an accession or
    // Entrez gene id (should) give a single target. In addition the resource at /v1/targets does not
    // list all the targets available, due to the large number of targets.
    // Instead, use the filter parameter with this resource to identify a subset of targets.
    def findProteinByUniprotAccession(String accessionUrl) {
        final String url = grailsApplication.config.ncgc.server.root.url + accessionUrl
        println url
        return executeGetRequestJSON(url, null)
    }
    /**
     *
     * @param geneUrl
     * @return
     */
    def findProteinByGeneId(String geneUrl) {
        final String url = grailsApplication.config.ncgc.server.root.url + geneUrl
        println url
        return executeGetRequestJSON(url, null)
    }

}
