package bardqueryapi

import bard.core.Assay
import bard.core.Experiment
import bard.core.Project
import bard.core.StructureSearchParams
import bard.core.adapter.CompoundAdapter
import elasticsearchplugin.QueryExecutorService
import elasticsearchplugin.RestClientFactoryService
import wslite.json.JSONArray
import wslite.json.JSONObject
import wslite.rest.RESTClient
import wslite.rest.RESTClientException

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(SearchHelper)
class BardWebInterfaceController {

    def shoppingCartService
    QueryService queryService
    QueryExecutorService queryExecutorService
    RestClientFactoryService restClientFactoryService
    final static String NCGC_ROOT_URL = "http://bard.nih.gov/api/v1"

    def index() {
        homePage()
    }

    def homePage() {
        render(view: "homePage")
    }

    def searchCompoundsByIDs() {
        String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                def parameterMap = [:]
                parameterMap.put('path', "/compounds")
                Map dataMap = [ids: "${searchString}"]
                 JSONArray resultJson = (JSONArray) postFormRequest(this.restClientFactoryService, NCGC_ROOT_URL, dataMap, parameterMap)
                List docs = []
                int numberOfHits = 0
                resultJson.each { result ->
                    Map currentObject = [:]
                    currentObject.put("iupac_name", result.name)
                    currentObject.put("iso_smiles", result.smiles)
                    currentObject.put("cid", result.cid)
                    docs.add(currentObject)
                    ++numberOfHits
                }
                JSONObject metaData = new JSONObject([nhit: numberOfHits])
                render(template: 'compounds', model: [docs: docs, metaData: metaData, searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        message(code: 'compound.search.error', args: [exp.message], default: "Compound search has encountered an error:\n${exp.message}"))
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")

    }

    def searchAssaysByIDs() {

        String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                def parameterMap = [:]
                parameterMap.put('path', "/assays")
                Map dataMap = [ids: "${searchString}"]
                JSONArray resultJson = (JSONArray) postFormRequest(this.restClientFactoryService, NCGC_ROOT_URL, dataMap, parameterMap)
                List docs = []
                int numberOfHits = 0
                resultJson.each { result ->
                    Map currentObject = [:]
                    currentObject.put("assay_id", result.aid)
                    currentObject.put("name", result.name)
                    currentObject.put("highlight", result.source)
                    docs.add(currentObject)
                    ++numberOfHits
                }
                JSONObject metaData = new JSONObject([nhit: numberOfHits])
                render(template: 'assays', model: [docs: docs, metaData: metaData, searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        message(code: 'assay.search.error', args: [exp.message], default: "Assay search has encountered an error:\n${exp.message}"))
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }

    def searchProjectsByIDs() {
        String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                def parameterMap = [:]
                parameterMap.put('path', "/projects")
                Map dataMap = [ids: "${searchString}"]
                JSONArray resultJson = (JSONArray) postFormRequest(this.restClientFactoryService, NCGC_ROOT_URL, dataMap, parameterMap)
                List docs = []
                int numberOfHits = 0
                resultJson.each { result ->
                    Map currentObject = [:]
                    currentObject.put("proj_id", result.projectId)
                    currentObject.put("name", result.name)
                    currentObject.put("highlight", result.source)
                    docs.add(currentObject)
                    ++numberOfHits
                }
                JSONObject metaData = new JSONObject([nhit: numberOfHits])
                render(template: 'projects', model: [docs: docs, metaData: metaData, searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        message(code: 'project.search.error', args: [exp.message], default: "Project search has encountered an error:\n${exp.message}"))
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")


    }
    /**
     * @return
     */
    def searchStructures() {

        String searchString = params.searchString?.trim()
        if (searchString) {
            //we make the first character capitalized to match the ENUM
            final String[] searchStringSplit = searchString.toLowerCase().capitalize().split(":")
            final StructureSearchParams.Type searchType = searchStringSplit[0] as StructureSearchParams.Type
            List<CompoundAdapter> compoundAdapters = queryService.structureSearch(searchStringSplit[1], searchType)
            def metaDataMap = [nhit: compoundAdapters.size()]
            def listDocs = []

            for (CompoundAdapter compoundAdapter : compoundAdapters) {
                def adapter = [:]
                long cid = compoundAdapter.pubChemCID
                adapter.put("cid", cid)
                adapter.put("iupac_name", cid)
                adapter.put("iso_smiles", compoundAdapter.structureSMILES)
                listDocs.add(adapter)
            }

            render(template: 'compounds', model: [docs: listDocs, metaData: metaDataMap])
            return
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }


    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if '' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            CompoundAdapter compoundAdapter = this.queryService.showCompound(compoundId)
            render(view: "showCompound", model: [compound: compoundAdapter])
        }
        else {
            render "Compound ID (CID) parameter required"
        }
    }

    def searchCompounds() {
        def map = [:]
        String searchString = params.searchString?.trim()
        handleSearchParams('/search/compounds', map)
        try {
            JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
            render(template: 'compounds', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    message(code: 'compound.search.error', args: [exp.message], default: "Compound search has encountered an error:\n${exp.message}"))
        }
    }

    def searchAssays() {
        def map = [:]
        String searchString = params.searchString?.trim()
        handleSearchParams('/search/assays', map)
        try {
            JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
            render(template: 'assays', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    message(code: 'assay.search.error', args: [exp.message], default: "Assay search has encountered an error:\n${exp.message}"))
        }
    }

    def searchProjects() {
        String searchString = params.searchString?.trim()
        def map = [:]
        handleSearchParams('/search/projects', map)
        try {
            JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
            render(template: 'projects', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    message(code: 'project.search.error', args: [exp.message], default: "Project search has encountered an error:\n${exp.message}"))
        }
    }

    //TODO: Whomever creates the gsp should also write unit tests for this method
    def showAssay(Integer assayProtocolId) {
        Integer assayId = assayProtocolId ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (assayId) {
            Assay assay = this.queryService.showAssay(assayId)
            render(view: "showAssay", model: [assayInstance: assay])
        }
        else {
            render "Assay Protocol ID parameter required"
        }
    }
    //TODO: Whomever creates the gsp should also write unit tests for this method
    def showProject(Integer projectId) {
        Integer projId = projectId ?: params.id as Integer//if 'project' param is provided, use that; otherwise, try the default id one

        if (projId) {
            Project project = this.queryService.showProject(projId)
            render(view: "showProject", model: [projectInstance: project])
        }
        else {
            render "Project ID parameter required"
        }
    }
    //TODO: Whomever creates the gsp should also write unit tests for this method
    def showExperiment(Integer experimentId) {
        Integer exptId = experimentId ?: params.id as Integer//if 'project' param is provided, use that; otherwise, try the default id one

        if (exptId) {
            Experiment experiment = this.queryService.showExperiment(exptId)
            render(view: "showExperiment", model: [experiment: experiment])
        }
        else {
            render "Experiment ID parameter required"
        }
    }

    def autoCompleteAssayNames() {
        final List<String> assayNames = this.queryService.autoComplete(params?.term)
        render(contentType: "text/json") {
            for (String assayName : assayNames) {
                element assayName
            }
            if (!assayNames) {
                element ""
            }
        }
    }
}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
class SearchHelper {

    /**
     * TODO: We will squirell this here for now Since will not be needing it after the JDO is released
     * @param url
     * @param data
     * @return
     * @throws wslite.rest.RESTClientException
     */
    def postFormRequest(elasticsearchplugin.RestClientFactoryService restClientFactoryService, final String url, final Map dataMap, Map parameterMap = [:]) throws RESTClientException {
        parameterMap.put('query', [expand: "TRUE", include_entities: false])
        parameterMap.put('connectTimeout', 5000)
        parameterMap.put('readTimeout', 10000)

        final RESTClient restClientClone = restClientFactoryService.createNewRestClient(url)
        def response = restClientClone.post(parameterMap) {
            urlenc dataMap
        }
        return response.json
    }
    /**
     *
     * @param relativePath for example /search/compounds
     */
    public void handleSearchParams(String relativePath, def parameterMap) {
        String searchString = params.searchString?.trim()
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.offset = params.int('offset') ?: 0
        int max = new Integer(params.max)
        int offset = new Integer(params.offset)
        //check for nulls

        parameterMap.put('query', [top: max, skip: "${offset}", q: "${searchString}", include_entities: false])

        parameterMap.put('path', "${relativePath}")
        parameterMap.put('connectTimeout', 5000)
        parameterMap.put('readTimeout', 10000)

    }
}
