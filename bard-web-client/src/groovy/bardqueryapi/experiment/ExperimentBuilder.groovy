package bardqueryapi.experiment

import bard.core.adapter.CompoundAdapter

import bard.core.rest.spring.experiment.*
import bardqueryapi.*
import bardqueryapi.compoundBioActivitySummary.CompoundBioActivitySummaryBuilder
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ExperimentBuilder {

    List<WebQueryValue> buildHeader(List<String> priorityDisplays, List<String> dictionaryIds, final boolean hasPlot, final boolean hasChildElements) {
        List<WebQueryValue> columnHeaders = []
        columnHeaders.add(new StringValue(value: "SID"))
        columnHeaders.add(new StringValue(value: "CID"))
        columnHeaders.add(new StringValue(value: "Structure"))
        columnHeaders.add(new StringValue(value: "Outcome"))
        columnHeaders.add(new StringValue(value: "Results"))
//        columnHeaders.add(new MapValue(value: [(new StringValue(value: "priorityDisplays")): priorityDisplays,
//                (new StringValue(value: "dictionaryIds")): dictionaryIds]))
        columnHeaders.add(new StringValue(value: "Experiment Descriptors"))
        if (hasChildElements) {
            columnHeaders.add(new StringValue(value: "Child Elements"))
        }
//        if (hasPlot) {
//            columnHeaders.add(new StringValue(value: "Concentration Response Series"))
//            columnHeaders.add(new StringValue(value: "Concentration Response Plot"))
//            columnHeaders.add(new StringValue(value: "Misc Data"))
//        }
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
     * @param priorityDisplays
     * @param compoundAdapterMap
     * @return
     */
    List<WebQueryValue> addRow(final Activity activity,
                               final NormalizeAxis normalizeYAxis,
                               final Double yNormMin,
                               final Double yNormMax,
                               List<String> priorityDisplays,
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
        StructureValue structureValue =
            new StructureValue(
                    cid: cid,
                    sid: sid,
                    smiles: compoundAdapter.structureSMILES,
                    name: compoundAdapter.name,
                    numActive: compoundAdapter.numberOfActiveAssays,
                    numAssays: compoundAdapter.numberOfAssays
            )

        rowData.add(structureValue)

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

//    List<Pair> extractActivityToConcentratonList(ConcentrationResponseSeries concentrationResponseSeries) {
//        List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
//
//        final String testConcentrationUnit = concentrationResponseSeries.testConcentrationUnit
//        List<Pair> activityToConcentration = []
//        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
//            final String displayActivity = concentrationResponsePoint.displayActivity()
//            final String concentration = concentrationResponsePoint.displayConcentration(testConcentrationUnit)
//            Pair<String, String> concentrationDisplayTuple = new ImmutablePair<String, String>(displayActivity, concentration)
//            activityToConcentration.add(concentrationDisplayTuple)
//        }
//        return activityToConcentration
//    }

    void addRows(final List<Activity> activities,
                 final TableModel tableModel,
                 final NormalizeAxis normalizeYAxis,
                 final Double yNormMin,
                 final Double yNormMax,
                 List<String> priorityDisplays,
                 final Map<Long, CompoundAdapter> compoundAdapterMap) {
        for (Activity activity : activities) {
            final List<WebQueryValue> rowData = addRow(activity, normalizeYAxis, yNormMin, yNormMax, priorityDisplays, compoundAdapterMap)
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
        List<String> priorityDisplays = experimentDetails.priorityDisplays
        final boolean hasChildElements = experimentDetails.hasChildElements
        Double yNormMin = null
        Double yNormMax = null
        final NormalizeAxis normalizeYAxis = experimentDetails.normalizeYAxis
        tableModel.setColumnHeaders(buildHeader(priorityDisplays, experimentDetails?.dictionaryIds, hasPlot, hasChildElements))
        final List<Activity> activities = experimentDetails.activities
        if (normalizeYAxis == NormalizeAxis.Y_NORM_AXIS) {
            yNormMin = experimentDetails.yNormMin
            yNormMax = experimentDetails.yNormMax

        }

        addRows(activities, tableModel, normalizeYAxis, yNormMin, yNormMax, priorityDisplays, compoundAdapterMap)

        return tableModel
    }

//    private Map concentrationResponseMap(final ConcentrationResponseSeries concentrationResponseSeries,
//                                         final NormalizeAxis normalizeAxis,
//                                         final Long cid,
//                                         final Double slope,
//                                         final String testConcentrationUnit,
//                                         final Double yNormMin,
//                                         final Double yNormMax,
//                                         final String priorityDisplay) {
//
//        List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
//        ActivityConcentrationMap doseResponsePointsMap = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponsePoints)
//        CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
//        Map valueMap = [:]
//
//
//        if (!concentrationResponsePoints.isEmpty()) {
//            valueMap.put("title", "Plot for CID ${cid}")
//
//            if (normalizeAxis == NormalizeAxis.Y_NORM_AXIS) {
//                Map mapParams = [
//                        sinf: curveFitParameters?.getSInf(),
//                        s0: curveFitParameters?.getS0(),
//                        slope: slope,
//                        hillSlope: curveFitParameters?.getHillCoef(),
//                        concentrations: doseResponsePointsMap.concentrations,
//                        activities: doseResponsePointsMap.activities,
//                        yAxisLabel: "${concentrationResponseSeries?.getYAxisLabel()}",
//                        xAxisLabel: "Log(Concentration) ${testConcentrationUnit}",
//                        yNormMin: "${yNormMin}",
//                        yNormMax: "${yNormMax}"
//                ]
//                valueMap.put("plot", mapParams)
//
//            }
//            else {
//                Map mapParams = [
//                        sinf: curveFitParameters?.getSInf(),
//                        s0: curveFitParameters?.getS0(),
//                        slope: slope,
//                        hillSlope: curveFitParameters?.getHillCoef(),
//                        concentrations: doseResponsePointsMap.concentrations,
//                        activities: doseResponsePointsMap.activities,
//                        yAxisLabel: "${concentrationResponseSeries?.getYAxisLabel()}",
//                        xAxisLabel: "Log(Concentration) ${testConcentrationUnit}"
//                ]
//                valueMap.put("plot", mapParams)
//
//
//            }
//            List<String> curveParams = []
//            if (priorityDisplay) {
//                curveParams.add("${priorityDisplay} : ${slope}")
//            }
//            curveParams.add("sInf: " + new ExperimentalValueUtil(curveFitParameters.sInf, false).toString())
//            curveParams.add("s0: " + new ExperimentalValueUtil(curveFitParameters.s0, false).toString())
//            curveParams.add("HillSlope: " + new ExperimentalValueUtil(curveFitParameters.hillCoef, false).toString())
//            valueMap.put("curveFitParams", curveParams)
//
//            valueMap.put("miscData", concentrationResponseSeries.miscData)
//        }
//        return valueMap
//    }

}


