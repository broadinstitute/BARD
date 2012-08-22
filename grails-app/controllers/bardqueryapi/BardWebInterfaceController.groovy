package bardqueryapi

import bard.core.Assay
import bard.core.Experiment
import bard.core.Project
import bard.core.StructureSearchParams
import bard.core.adapter.CompoundAdapter
import elasticsearchplugin.QueryExecutorService
import wslite.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(SearchHelper)
class BardWebInterfaceController {

    QueryService queryService
    QueryExecutorService queryExecutorService
    final static String NCGC_ROOT_URL = "http://bard.nih.gov/api/v1"

    def index() {
        homePage()
    }

    def homePage() {
        render(view: "homePage")
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
        JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
        render(template: 'compounds', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
    }

    def searchAssays() {
        def map = [:]
        String searchString = params.searchString?.trim()
        handleSearchParams('/search/assays', map)
        JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
        render(template: 'assays', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
    }

    def searchProjects() {
        String searchString = params.searchString?.trim()
        def map = [:]
        handleSearchParams('/search/projects',map)
        JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
        render(template: 'projects', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
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
