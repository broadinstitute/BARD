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


    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            JSONObject compoundESDocument = elasticSearchService.getCompoundDocument(compoundId)
            JSONObject compoundJson = [cid: compoundESDocument?._id,
                    sids: compoundESDocument?._source?.sids,
                    probeId: compoundESDocument?._source?.probeId,
                    smiles: compoundESDocument?._source?.smiles] as JSONObject
            render(view: "showCompound", model: [compoundJson: compoundJson, compoundId: compoundId])
        }
        else {
            render "Compound ID (CID) parameter required"
        }
    }
}
