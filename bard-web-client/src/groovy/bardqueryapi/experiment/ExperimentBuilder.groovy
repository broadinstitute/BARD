package bardqueryapi.experiment

import bard.core.adapter.CompoundAdapter
import bardqueryapi.compoundBioActivitySummary.CompoundBioActivitySummaryBuilder
import org.codehaus.groovy.grails.commons.ApplicationHolder
import bard.core.rest.spring.experiment.*
import bardqueryapi.*

class ExperimentBuilder {

    List<WebQueryValue> buildHeader(final boolean hasPlot, final boolean hasChildElements) {
        List<WebQueryValue> columnHeaders = []
        columnHeaders.add(new StringValue(value: "SID"))
        columnHeaders.add(new StringValue(value: "CID"))
        columnHeaders.add(new StringValue(value: "Structure"))
        columnHeaders.add(new StringValue(value: "Outcome"))
        columnHeaders.add(new StringValue(value: "Results"))
        columnHeaders.add(new StringValue(value: "Experiment Descriptors"))
        if (hasChildElements) {
            columnHeaders.add(new StringValue(value: "Child Elements"))
        }
        return columnHeaders
    }

    /**
     *
     * Builds the tableModel's row based on the result set and types.
     *
     * @param activity
     * @param normalizeYAxis
     * @param yNormMin
     * @param yNormMax
     * @param compoundAdapterMap
     * @return
     */
    List<WebQueryValue> addRow(final Activity activity,
                               final NormalizeAxis normalizeYAxis,
                               final Double yNormMin,
                               final Double yNormMax,
                               final Map<Long, CompoundAdapter> compoundAdapterMap) {

        //A row is a list of table cells, each implements WebQueryValue.
        List<WebQueryValue> rowData = []

        //SID
        Long sid = activity.sid
        LinkValue sidValue = new LinkValue(value: "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${sid.toString()}",
                text: sid.toString(),
                imgFile: 'pubchem.png',
                imgAlt: 'PubChem')
        rowData.add(sidValue)

        //CID
        Long cid = activity.cid
        def grailsApplicationTagLib = ApplicationHolder.application.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
        String linkValue = grailsApplicationTagLib.createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: cid.toString()])
        LinkValue cidValue = new LinkValue(value: linkValue, text: cid.toString())

        rowData.add(cidValue)

        //Structure image
        final CompoundAdapter compoundAdapter = compoundAdapterMap.get(cid)
        if (compoundAdapter) {
            StructureValue structureValue =
                new StructureValue(
                        cid: cid,
                        sid: sid,
                        smiles: compoundAdapter?.compound?.smiles,
                        name: compoundAdapter?.name,
                        numActive: compoundAdapter?.numberOfActiveAssays,
                        numAssays: compoundAdapter?.numberOfAssays
                )
            rowData.add(structureValue)
        }
        else {
            //Add an empty cell
            rowData.add(new StringValue(value: 'Not Available'))
        }

        //Outcome
        ResultData resultData = activity?.resultData
        StringValue outcome = new StringValue(value: resultData.outcome)
        rowData.add(outcome)

        //Convert the experimental data to result types (curves, key/value pairs, etc.)
        List<WebQueryValue> experimentValues = CompoundBioActivitySummaryBuilder.convertExperimentResultsToValues(activity, yNormMin, yNormMax)
        //if the result type is a concentration series, we want to add the normalization values to each curve.
        experimentValues.findAll({WebQueryValue experimentResult ->
            experimentResult instanceof ConcentrationResponseSeriesValue
        }).each { ConcentrationResponseSeriesValue concResSer ->
            concResSer.yNormMax = yNormMax
            concResSer.yNormMin = yNormMin
        }
        if (experimentValues) {
            ListValue listValue = new ListValue(value: experimentValues)
            rowData.add(listValue)
        }

        //Add all rootElements from the JsonResponse
        List<StringValue> rootElements = []
        if (!resultData.isMapped()) {
            rootElements.add(new StringValue(value: "TIDs not yet mapped to a result hierarchy"))
        }
        for (RootElement rootElement : resultData.rootElements) {
            if (rootElement.toDisplay()) {
                rootElements.add(new StringValue(value: rootElement.toDisplay()))
            }
        }
        rowData.add(new ListValue(value: rootElements))

        //Add all childElements of the priorityElements, if any.
        List<WebQueryValue> childElements = []
        for (PriorityElement priorityElement in resultData.priorityElements) {
            if (priorityElement?.hasChildElements()) {
                for (ActivityData activityData : priorityElement.childElements) {
                    if (activityData.toDisplay()) {
                        childElements << new StringValue(value: activityData.toDisplay())
                    }
                }
            }
        }
        if (childElements) {
            rowData.add(new ListValue(value: childElements))
        }


        return rowData;
    }


    void addRows(final List<Activity> activities,
                 final TableModel tableModel,
                 final NormalizeAxis normalizeYAxis,
                 final Double yNormMin,
                 final Double yNormMax,
                 final Map<Long, CompoundAdapter> compoundAdapterMap) {
        for (Activity activity : activities) {
            final List<WebQueryValue> rowData = addRow(activity, normalizeYAxis, yNormMin, yNormMax, compoundAdapterMap)
            tableModel.addRowData(rowData)
        }
    }

    public TableModel buildModel(Map experimentDetails) {
        final TableModel tableModel = new TableModel()

        tableModel.additionalProperties.put("experimentName", experimentDetails?.experiment?.name)
        tableModel.additionalProperties.put("bardExptId", experimentDetails?.experiment?.bardExptId)
        tableModel.additionalProperties.put("capExptId", experimentDetails?.experiment?.capExptId)
        tableModel.additionalProperties.put("bardAssayId", experimentDetails?.experiment?.bardAssayId)
        tableModel.additionalProperties.put("capAssayId", experimentDetails?.experiment?.capAssayId)
        tableModel.additionalProperties.put("total", experimentDetails?.total)
        tableModel.additionalProperties.put("actives", experimentDetails?.actives)
        tableModel.additionalProperties.put("confidenceLevel", experimentDetails?.experiment?.confidenceLevel)



        Map<Long, CompoundAdapter> compoundAdapterMap = experimentDetails?.compoundAdaptersMap
        final boolean hasPlot = experimentDetails.hasPlot
        final boolean hasChildElements = experimentDetails.hasChildElements
        Double yNormMin = null
        Double yNormMax = null
        final NormalizeAxis normalizeYAxis = experimentDetails.normalizeYAxis
        tableModel.setColumnHeaders(buildHeader(hasPlot, hasChildElements))
        final List<Activity> activities = experimentDetails.activities
        if (normalizeYAxis == NormalizeAxis.Y_NORM_AXIS) {
            yNormMin = experimentDetails.yNormMin
            yNormMax = experimentDetails.yNormMax

        }

        addRows(activities, tableModel, normalizeYAxis, yNormMin, yNormMax, compoundAdapterMap)

        return tableModel
    }
}


