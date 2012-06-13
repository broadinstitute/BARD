package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
class BardWebInterfaceController {

    QueryAssayApiService queryAssayApiService

    def index() {
        render(view: "index.gsp")
    }

    /**
     * The format that the NCGC api expects is: http://assay.nih.gov/bard/rest/v1/assays/862/compounds?skip=20&top=5
     * skip is offset
     * top is max
     *
     * @return
     */
    def findCompoundsForAssay(Integer max, Integer offset, Integer assay) {
        //NCGS' max and offset
        Integer skip = offset ?: 0
        Integer top = max ?: 10
        params.max = top

        String assayId = assay.toString() ?: params.id //if 'assay' param is provided, use that; otherwise, try the default id one
        final AssayDisplayType assayDisplayType = AssayDisplayType.Compounds
        if (assayId) {
            final String assayResourceUrl = "/bard/rest/v1/assays/" + assayId
            final String assayUrl = grailsApplication.config.ncgc.server.root.url + assayResourceUrl
            final wslite.json.JSONObject assayJson = queryAssayApiService.executeGetRequestJSON(assayUrl, null) //get the Assay instance
            final Integer totalCompounds = assayJson.samples
            final String assayUrlPaging = assayUrl + '/compounds?skip=' + skip + '&top=' + top
            final wslite.json.JSONObject assayCompoundsJson = queryAssayApiService.executeGetRequestJSON(assayUrlPaging, null)

            render(view: "findCompoundsForAssay", model: [assayCompoundsJsonArray: assayCompoundsJson.collection, totalCompounds: totalCompounds, aid: assayId])
        }
        else {
            render "Assay parameter required"
        }
    }

    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            final String compoundResourceUrl = "/bard/rest/v1/compounds/" + compoundId.toString()
            final String compoundUrl = grailsApplication.config.ncgc.server.root.url + compoundResourceUrl
            final wslite.json.JSONObject compoundJson = queryAssayApiService.executeGetRequestJSON(compoundUrl, null) //get the Assay instance

            render(view: "showCompound", model: [compoundJson: compoundJson])
        }
        else {
            render "Compound ID (CID) parameter required"
        }

    }
}
