package bardqueryapi.compoundBioActivitySummary

import bardqueryapi.WebQueryValue
import bardqueryapi.TableModel
import bard.core.rest.spring.experiment.Activity

import bardqueryapi.GroupByTypes

import bardqueryapi.IQueryService

import bard.core.adapter.AssayAdapter

import org.apache.log4j.Logger
import bard.core.rest.spring.assays.Assay
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.project.Project
import bardqueryapi.AssayValue
import bardqueryapi.ProjectValue
import bard.core.rest.spring.experiment.ExperimentShow
import bardqueryapi.ExperimentValue

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 3/6/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
class CompoundBioActivitySummaryBuilder {
    IQueryService queryService
    static final Logger log = Logger.getLogger(CompoundBioActivitySummaryBuilder.class)

    CompoundBioActivitySummaryBuilder(IQueryService queryService) {
        this.queryService = queryService
    }

    public List<TableModel> buildModel(GroupByTypes groupByType, Map groupedByExperimentalData) {// Map<ADID/PID, List<Activity>>
        List<TableModel> tableModels = []

        //Create a list of single-row TableModel tables, each table represents a collection of experiments grouped by assay or project
        for (Long resourceId in groupedByExperimentalData.keySet()) {
            List<WebQueryValue> singleRowData = []

            List<Activity> exptDataList = groupedByExperimentalData[resourceId]
            //The first cell (column) is the resource (assay or a project)
            WebQueryValue resource = findResourceByTypeAndId(groupByType, resourceId)
            singleRowData << resource
            //For each grouped-by resource item (assay/project), add all the grouped-by experimental data,
            // converting result types into the appropriate value-types.
            //Use Map<exptDataId, List<WebQueryValue> to store the sections of each exptData (curve, single-point, etc.). For example: ["14359.26749233", [StructureValue, ConcentrationResponsePlotValue].
            Map<String, WebQueryValue> exptDataValues = [:]
            for (Activity exptData in exptDataList) {
                String key = exptData.exptDataId //e.g., "14359.26749233"
                //Add the experiment itself (for an experiment's description)
                ExperimentShow experimentShow = this.queryService.experimentRestService.getExperimentById(exptData.bardExptId)
                WebQueryValue experiment = new ExperimentValue(value: experimentShow)
                singleRowData << experiment
                //TODO - add all result types
            }

            TableModel tableModel = new TableModel(data: [singleRowData])
            tableModels << tableModel
        }
        return tableModels
    }


    WebQueryValue findResourceByTypeAndId(GroupByTypes groupByType, Long resourceId) {
        WebQueryValue resource

        switch (groupByType) {
            case GroupByTypes.ASSAY:
                List<AssayAdapter> assayAdapters = queryService.findAssaysByADIDs([resourceId]).assayAdapters
                if (assayAdapters.size() == 1) {
                    resource = new AssayValue(value: assayAdapters.first())
                } else {
                    Log.error("Could not find Assay with ADID=${resourceId}")
                    resource = new AssayValue()
                }
                break;
            case GroupByTypes.PROJECT:
                List<ProjectAdapter> projectAdapters = queryService.findProjectsByPIDs([resourceId]).projectAdapters
                if (projectAdapters.size() == 1) {
                    resource = new ProjectValue(value: projectAdapters.first())
                } else {
                    Log.error("Could not find Project with PID=${resourceId}")
                    resource = new ProjectValue()
                }
                break;
            default:
                throw new RuntimeException("Group-by type ${groupByType} is not supported")
        }

        return resource
    }

}
