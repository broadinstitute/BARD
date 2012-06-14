package bardqueryapi

import grails.converters.JSON

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
        offset = offset ?: 0
        max = max ?: 100
        params.max = max.toString()

        Integer assayId = assay ?: params.id as Integer //if 'assay' param is provided, use that; otherwise, try the default id one

        if (assayId) {
            final Integer totalCompounds = queryAssayApiService.getTotalAssayCompounds(assayId)
            final List<String> assayCompoundsPage = queryAssayApiService.getAssayCompoundsResultset(max, offset, assayId)

            render(view: "findCompoundsForAssay", model: [assayCompoundsJsonArray: assayCompoundsPage, totalCompounds: totalCompounds, aid: assayId])
        }
        else {
            render "Assay ID is not defined"
        }
    }

    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            final String compoundResourceUrl = "/bard/rest/v1/compounds/" + compoundId.toString()
            final String compoundUrl = grailsApplication.config.ncgc.server.root.url + compoundResourceUrl
            def compoundJson = queryAssayApiService.executeGetRequestJSON(compoundUrl, null) //get the Assay instance
            def compound = JSON.parse(compoundJson.toString())
            //If the compound does not exist and we get back a server error, generate an empty JSON object.
            if (compound.errorMessage) {
                compoundJson = null
            }
            render(view: "showCompound", model: [compoundJson: compoundJson, compoundId: compoundId])
        }
        else {
            render "Compound ID (CID) parameter required"
        }

    }
}
