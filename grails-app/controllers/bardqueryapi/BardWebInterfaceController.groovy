package bardqueryapi

import bard.core.Project
import bard.core.StructureSearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import elasticsearchplugin.QueryExecutorService
import wslite.json.JSONObject

import javax.servlet.http.HttpServletResponse

/**
 * TODO: Search by IDs now uses the JDO. Still requires some work. We would like to use the adpaters directly
 *
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
    QueryServiceWrapper queryServiceWrapper

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
                final List<Long> cids = searchString.split(",") as List<Long>
                Map compoundAdapterMap = this.queryService.findCompoundsByCIDs(cids)
                List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compounds
                def listDocs = []
                //TODO: we ought to use the adapter directly in the gsp
                for (CompoundAdapter compoundAdapter : compoundAdapters) {
                    def adapter = [:]
                    long cid = compoundAdapter.pubChemCID
                    String iupacName = compoundAdapter.compound.getValue(bard.core.Compound.IUPACNameValue)?.value as String
                    adapter.put("cid", cid)
                    adapter.put("iupac_name", iupacName)
                    adapter.put("iso_smiles", compoundAdapter.structureSMILES)
                    listDocs.add(adapter)
                }


                JSONObject metaData = new JSONObject([nhit: compoundAdapterMap.nHits])
                render(template: 'compounds', model: [docs: listDocs, metaData: metaData, searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Compound search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")

    }

    def searchAssaysByIDs() {

        String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                //TODO: Use the AssayAdapter directly
                final List<Long> adids = searchString.split(",") as List<Long>
                final Map assayAdapterMap = this.queryService.findAssaysByADIDs(adids)
                final int numberOfHits = assayAdapterMap.nHits
                final List<AssayAdapter> assays = assayAdapterMap.assays
                final List docs = []
                for (AssayAdapter assayAdapter : assays) {
                    Map currentObject = [:]
                    currentObject.put("assay_id", assayAdapter.assay.id)
                    currentObject.put("name", assayAdapter.assay.name)
                    currentObject.put("highlight", "")
                    docs.add(currentObject)
                }
                JSONObject metaData = new JSONObject([nhit: numberOfHits])
                render(template: 'assays', model: [docs: docs, metaData: metaData, searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Assay search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }

    def searchProjectsByIDs() {
        String searchString = params.searchString?.trim()
        if (searchString) {
            try {
                final List<Long> projectIds = searchString.split(",") as List<Long>
                List<Project> projects = this.queryService.findProjectsByPIDs(projectIds)
                List docs = []
                int numberOfHits = projects.size()
                for (Project project : projects) {
                    Map currentObject = [:]
                    currentObject.put("proj_id", project.id)
                    currentObject.put("name", project.name ?project.name:"" )
                    currentObject.put("highlight", "")
                    docs.add(currentObject)
                }
                JSONObject metaData = new JSONObject([nhit: numberOfHits])
                render(template: 'projects', model: [docs: docs, metaData: metaData, searchString: "${searchString}"])
                return
            }
            catch (Exception exp) {
                return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                        "Project search has encountered an error:\n${exp.message}")
            }
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")


    }
    /**
     * @return
     */
    def searchStructures() {
        //TODO: Add more error handling here
        String searchString = params.searchString?.trim()
        if (searchString) {
            //we make the first character capitalized to match the ENUM

            final String[] searchStringSplit = searchString.split(":")
            if (searchStringSplit.length == 2) {
                final String searchTypeString = searchStringSplit[0]
                final String smiles = searchStringSplit[1]
                final StructureSearchParams.Type searchType = searchTypeString?.toLowerCase().capitalize() as StructureSearchParams.Type
                Map compoundAdapterMap = queryService.structureSearch(smiles, searchType)
                List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compounds

                def metaDataMap = [nhit: compoundAdapterMap.nHits]
                def listDocs = []

                //TODO: we ought to use the adapter directly in the gsp
                for (CompoundAdapter compoundAdapter : compoundAdapters) {
                    def adapter = [:]
                    long cid = compoundAdapter.pubChemCID
                    String iupacName = compoundAdapter.compound.getValue(bard.core.Compound.IUPACNameValue)?.value as String
                    adapter.put("cid", cid)
                    adapter.put("iupac_name", iupacName)
                    adapter.put("iso_smiles", compoundAdapter.structureSMILES)
                    listDocs.add(adapter)
                }
                render(template: 'compounds', model: [docs: listDocs, metaData: metaDataMap])
                return
            }
        }
        flash.message = 'Search String is required must be of the form StructureSearchType:Smiles'
        redirect(action: "homePage")
    }


    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if '' param is provided, use that; otherwise, try the default id one

        CompoundAdapter compoundAdapter = null;
        if (compoundId) {
            compoundAdapter = this.queryService.showCompound(compoundId)
        }

        if (compoundAdapter) {
            render(view: "showCompound", model: [compound: compoundAdapter])
        }
        else {
            render "Could not find compound"
        }
    }

    def searchCompounds() {
        def map = [:]
        String searchString = params.searchString?.trim()
        handleSearchParams('/search/compounds', map)
        try {
            final String NCGC_ROOT_URL =  queryServiceWrapper.baseURL
            JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
            render(template: 'compounds', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Compound search has encountered an error:\n${exp.message}")
        }
    }

    def searchAssays() {
        def map = [:]
        String searchString = params.searchString?.trim()
        handleSearchParams('/search/assays', map)
        try {
            final String NCGC_ROOT_URL =  queryServiceWrapper.baseURL

            JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
            render(template: 'assays', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assay search has encountered an error:\n${exp.message}")
        }
    }

    def searchProjects() {
        String searchString = params.searchString?.trim()
        def map = [:]
        handleSearchParams('/search/projects', map)
        try {
            final String NCGC_ROOT_URL =  queryServiceWrapper.baseURL

            JSONObject resultJson = (JSONObject) queryExecutorService.executeGetRequestJSON(NCGC_ROOT_URL, map)
            render(template: 'projects', model: [docs: resultJson.docs, metaData: resultJson.metaData, searchString: "${searchString}"])
        }
        catch (Exception exp) {
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Project search has encountered an error:\n${exp.message}")
        }
    }

    def showAssay(Integer assayProtocolId) {
        Integer assayId = assayProtocolId ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (assayId) {
            AssayAdapter assayAdapter = this.queryService.showAssay(assayId)
            render(view: "showAssay", model: [assayAdapter: assayAdapter])
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

        parameterMap.put('query', [top: max, skip: "${offset}", q: "${searchString}", include_entities: false])
        parameterMap.put('path', "${relativePath}")
        parameterMap.put('connectTimeout', 5000)
        parameterMap.put('readTimeout', 10000)

    }
}
