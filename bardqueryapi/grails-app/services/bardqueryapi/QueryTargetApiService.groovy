package bardqueryapi

import org.codehaus.groovy.grails.commons.GrailsApplication
import wslite.json.JSONArray

class QueryTargetApiService {

    QueryExecutorService queryExecutorService
    final String accessionUrl
    final String geneUrl
    /**
     *
     * @param rootURLToNCGC - The root url of the NCGC rest API
     * @param accessionUrl - The relative URL to get accessions for example  - /targets/accession/
     * @param geneUrl - The relative URL to get genes - for example   /targets/geneid/
     *
     * Wiring of this service is done through resources.groovy
     */
    public QueryTargetApiService(final String accessionUrl, final String geneUrl) {
        this.accessionUrl = accessionUrl
        this.geneUrl = geneUrl
    }
    /**
     * Returns a  list of AIDs given an accession number
     * @param max
     * @param offset
     * @param assayId
     * @return List < String >  of AIDs
     */
    List<String> findAssaysForAccessionTarget(final String accessionNumber) {
        final String url = "${this.accessionUrl}${accessionNumber}/assays"
        final List<String> assayIds = []
        try {
            def assays = queryExecutorService.executeGetRequestJSON(url, null)
            assays.each { assayUrl ->
                String currentAID = assayUrl.split('/').toList().last()
                assayIds.add(currentAID)
            }
        }
        catch (Exception exp) {
            log.error("We got back an error from the NCGC server: ${exp.message}")
        }
        return assayIds
    }

    //Target
    ///v1/targets/accession/{acc} - JSON representation of a protein target identified by Uniprot accession

    ///v1/targets/geneid/{id} - JSON representation of a protein target identified by Entrez gene ID

    //Note that the filter parameter is not handled for the above resources, since both an accession or
    // Entrez gene id (should) give a single target. In addition the resource at /v1/targets does not
    // list all the targets available, due to the large number of targets.
    // Instead, use the filter parameter with this resource to identify a subset of targets.
    def findProteinByUniprotAccession(String accessionNumber) {
        final String url = "${this.accessionUrl}${accessionNumber}"
        return queryExecutorService.executeGetRequestJSON(url, null)
    }
    /**
     *
     * @param geneUrl
     * @return
     */
    def findProteinByGeneId(String geneId) {
        final String url = "${this.geneUrl}${geneId}"
        return queryExecutorService.executeGetRequestJSON(url, null)
    }

}
