package bardqueryapi

import bard.core.Assay
import bard.core.Project
import bard.core.Experiment
import bard.core.adapter.CompoundAdapter

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
class BardWebInterfaceController {

    QueryService queryService

    def index() {
        homePage()
    }

    def example() {

    }

    def homePage() {
        render(view: "homePage", totalCompounds: 0, model: [assays: [], compounds: [], experiments: [], projects: []])
    }
    /**
     * @return
     */
    def search() {

        def searchString = params.searchString?.trim()
        if (searchString) {
            def result = this.queryService.search(searchString)
            render(view: "homePage", model: result)
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
    def searchCompounds(){

    }
    def searchAssays(){

    }
    def searchProjects(){

    }
    def searchExperiments(){

    }
    //TODO: Whomever creates the gsp should also write unit tests for this method
    def showAssay(Integer assayProtocolId) {
        Integer assayId = assayProtocolId ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (assayId) {
            Assay assay = this.queryService.showAssay(assayId)
            render(view: "showAssay", model: [assay:assay])
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
            render(view: "showProject", model: [project:project])
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
            render(view: "showExperiment", model: [experiment : experiment])
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

    /**
     * An Action to provide a search-call to NCGC REST API: find CIDs by structure (SMILES).
     * @param smiles
     * @param structureSearchType
     * @return
     */
    def structureSearch(String smiles, String structureSearchType) {
        final StructureSearchType searchType = structureSearchType as StructureSearchType
        switch (searchType) {
            case StructureSearchType.SUB_STRUCTURE:
            case StructureSearchType.SIMILARITY:
            case StructureSearchType.EXACT_MATCH:
                redirect(action: "search", params: ['searchString': searchType.description + ":" + smiles])
                break
            default:
                throw new RuntimeException("Undeifined structure-search type")
                break
        }


    }

}
