package bardqueryapi

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

import javax.servlet.http.HttpServletResponse
import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESCompound

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
class BardWebInterfaceController {

    QueryAssayApiService queryAssayApiService
    QueryExecutorInternalService queryExecutorInternalService
    QueryTargetApiService queryTargetApiService
    ElasticSearchService elasticSearchService

    def index() {
        homePage()
    }

    def homePage() {
        render(view: "homePage", totalCompounds: 0, model: [assays: [], compounds: [], experiments: [], projects: []])
    }
    /**
     * TODO: This will require refactoring after this iteration
     * when we add more functionality
     * @return
     */
    def search() {
        def searchString = params.searchString?.trim()
        if (searchString) {
            JSONObject result = elasticSearchService.search(searchString)

            List<Map> assays = []
            for (ESAssay assay in result.assays) {
                String assayString = assay.toString()
                String bardAssayViewUrl = grailsApplication.config.bard.assay.view.url
                String showAssayResource = "${bardAssayViewUrl}/${assay.assayNumber}"
                def assayMap = [assayName: assayString, assayResource: showAssayResource] as Map
                assays.add(assayMap)
            }

            List<String> compounds = []
            for (ESCompound compound in result.compounds) {
                String compoundString = compound.toString()
                compounds.add(compoundString)
            }

            render(view: "homePage", model: [totalCompounds: compounds.size, assays: assays as List<Map>, compounds: compounds as List<String>, experiments: [], projects: []])
            return
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
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

        if (assay) {
            final Integer totalCompounds = queryAssayApiService.getTotalAssayCompounds(assay)
            final List<String> assayCompoundsPage = queryAssayApiService.getAssayCompoundsResultset(max, offset, assay)

            render(view: "findCompoundsForAssay", model: [assayCompoundsJsonArray: assayCompoundsPage, totalCompounds: totalCompounds, aid: assay])
        }
        else {
            response.status = HttpServletResponse.SC_NOT_FOUND
            render "Assay ID is not defined"
        }
    }

    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            final String compoundResourceUrl = "/bard/rest/v1/compounds/" + compoundId.toString()
            final String compoundUrl = grailsApplication.config.ncgc.server.root.url + compoundResourceUrl
            def compoundJson = queryExecutorInternalService.executeGetRequestJSON(compoundUrl, null) //get the Assay instance
            def compound = JSON.parse(compoundJson.toString())
            //If the compound does not exist and we get back a server error, generate an empty JSON object.
            if (compound.getClass() == JSONObject && compound.errorMessage) {
                compoundJson = null
            }
            render(view: "showCompound", model: [compoundJson: compoundJson, compoundId: compoundId])
        }
        else {
            render "Compound ID (CID) parameter required"
        }
    }
}
