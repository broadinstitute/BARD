package bardqueryapi.experiment

import bard.core.adapter.CompoundAdapter
import bard.core.util.ExperimentalValueUtil
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import bard.core.rest.spring.experiment.*
import bardqueryapi.*

class ExperimentBuilder {


    List<WebQueryValue> buildHeader(List<String> priorityDisplays, List<String> dictionaryIds, final boolean hasPlot, final boolean hasChildElements) {
        List<WebQueryValue> columnHeaders = []
        columnHeaders.add(new StringValue("SID"))
        columnHeaders.add(new StringValue("CID"))
        columnHeaders.add(new StringValue("Structure"))
        columnHeaders.add(new StringValue("Outcome"))
        columnHeaders.add(new MapValue([(new StringValue("priorityDisplays")): priorityDisplays,
                (new StringValue("dictionaryIds")): dictionaryIds]))
        columnHeaders.add(new StringValue("Experiment Descriptors"))
        if (hasChildElements) {
            columnHeaders.add(new StringValue("Child Elements"))
        }
        if (hasPlot) {
            columnHeaders.add(new StringValue("Concentration Response Series"))
            columnHeaders.add(new StringValue("Concentration Response Plot"))
            columnHeaders.add(new StringValue("Misc Data"))
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
        ListValue rowData = new ListValue()

        Long sid = activity.sid
        StringValue sidValue = new StringValue(value: sid.toString())
        rowData.value.add(sidValue)


        Long cid = activity.cid
        StringValue cidValue = new StructureValue(value: cid.toString())
        rowData.value.add(cidValue)

        final CompoundAdapter compoundAdapter = compoundAdapterMap.get(cid)
        StructureValue structureValue =
            new StructureValue(
                    cid: cid.toString(),
                    sid: sid.toString(),
                    smiles: compoundAdapter.structureSMILES,
                    name: compoundAdapter.name,
                    numActive: compoundAdapter.numberOfActiveAssays,
                    numAssays: compoundAdapter.numberOfAssays
            )

        rowData.value.add(structureValue)

        //Get the result-set.
        ResultData resultData = activity?.resultData
        String outcome = StringValue(value: resultData.outcome)
        rowData.value.add(outcome)

        //If the result-type is a curve, we collect all the curve values and parameters.
        if (resultData.hasPlot()) {
            ListValue listOfConcRespMap = new ListValue()
            for (PriorityElement priorityElement in resultData.priorityElements) {
                final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.concentrationResponseSeries
                if (concentrationResponseSeries) {
                    MapValue concRespMap = new MapValue()
                    concRespMap.value = concentrationResponseMap(concentrationResponseSeries, normalizeYAxis,
                            cid, priorityElement.getSlope(),
                            priorityElement.testConcentrationUnit,
                            yNormMin, yNormMax, priorityElement.getDictionaryLabel())

                    final List<Pair> activityToConcentratonList = extractActivityToConcentratonList(concentrationResponseSeries)
                    concRespMap.put("activityToConcentratonList", activityToConcentratonList)
                    final String dictionaryLabel = concentrationResponseSeries.dictionaryLabel
                    final String dictionaryDescription = concentrationResponseSeries.dictionaryDescription
                    concRespMap.put("dictionaryLabel", dictionaryLabel)
                    concRespMap.put("dictionaryDescription", dictionaryDescription)

                    final long dictElemId = concentrationResponseSeries?.dictElemId
                    concRespMap.put("dictElemId", dictElemId)
                    listOfConcRespMap << concRespMap
                }
            }
            if (listOfConcRespMap) {
                valueModel = new WebQueryValueModel([ConcentrationResponseSeriesList: listOfConcRespMap] as Map)
                rowData.add(valueModel)
            }
        }
        else {
            //If the result-type is not a curve, we simply display the priority elements as a list of key/value dictionary pairs.
            ListValue resultPairList = new ListValue()
            if (resultData?.hasPriorityElements()) {
                resultPairList.value = resultData.priorityElements.collect {PriorityElement priorityElement ->
                    DictionaryElementValue left = new DictionaryElementValue(value: priorityElement.pubChemDisplayName, dictionaryElementId: priorityElement.dictElemId)
                    StringValue right = new StringValue(priorityElement.toDisplay())
                    Pair<WebQueryValue, WebQueryValue> immutablePair = new ImmutablePair<WebQueryValue, WebQueryValue>(left, right)
                    PairValue pairValue = new PairValue(value: immutablePair)
                    return pairValue
                }
            }
            rowData.value.add(resultPairList)
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
        rowData.value.add(rootElements)


        //Add all childElements of the priorityElements, if any.
        ListValue childElements = new ListValue()
        for (PriorityElement priorityElement in resultData.priorityElements) {
            if (priorityElement?.hasChildElements()) {
                for (ActivityData activityData : priorityElement.childElements) {
                    if (activityData.toDisplay()) {
                        childElements.value.add(new StringValue(activityData.toDisplay()))
                    }
                }
            }
        }
        if (childElements) {
            rowData.value.add(childElements)
        }


        return rowData;
    }

    List<Pair> extractActivityToConcentratonList(ConcentrationResponseSeries concentrationResponseSeries) {
        List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints

        final String testConcentrationUnit = concentrationResponseSeries.testConcentrationUnit
        List<Pair> activityToConcentration = []
        for (ConcentrationResponsePoint concentrationResponsePoint : concentrationResponsePoints) {
            final String displayActivity = concentrationResponsePoint.displayActivity()
            final String concentration = concentrationResponsePoint.displayConcentration(testConcentrationUnit)
            Pair<String, String> concentrationDisplayTuple = new ImmutablePair<String, String>(displayActivity, concentration)
            activityToConcentration.add(concentrationDisplayTuple)
        }
        return activityToConcentration
    }

    void addRows(final List<Activity> activities,
                 final WebQueryTableModel webQueryTableModel,
                 final NormalizeAxis normalizeYAxis,
                 final Double yNormMin,
                 final Double yNormMax,
                 List<String> priorityDisplays,
                 final Map<Long, CompoundAdapter> compoundAdapterMap) {
        for (Activity activity : activities) {
            final List<WebQueryValueModel> rowData = addRow(activity, normalizeYAxis, yNormMin, yNormMax, priorityDisplays, compoundAdapterMap)
            webQueryTableModel.addRowData(rowData)
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

    private Map concentrationResponseMap(final ConcentrationResponseSeries concentrationResponseSeries,
                                         final NormalizeAxis normalizeAxis,
                                         final Long cid,
                                         final Double slope,
                                         final String testConcentrationUnit,
                                         final Double yNormMin,
                                         final Double yNormMax,
                                         final String priorityDisplay) {

        List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
        ActivityConcentrationMap doseResponsePointsMap = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponsePoints)
        CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
        Map valueMap = [:]


        if (!concentrationResponsePoints.isEmpty()) {
            valueMap.put("title", "Plot for CID ${cid}")

            if (normalizeAxis == NormalizeAxis.Y_NORM_AXIS) {
                Map mapParams = [
                        sinf: curveFitParameters?.getSInf(),
                        s0: curveFitParameters?.getS0(),
                        slope: slope,
                        hillSlope: curveFitParameters?.getHillCoef(),
                        concentrations: doseResponsePointsMap.concentrations,
                        activities: doseResponsePointsMap.activities,
                        yAxisLabel: "${concentrationResponseSeries?.getYAxisLabel()}",
                        xAxisLabel: "Log(Concentration) ${testConcentrationUnit}",
                        yNormMin: "${yNormMin}",
                        yNormMax: "${yNormMax}"
                ]
                valueMap.put("plot", mapParams)

            }
            else {
                Map mapParams = [
                        sinf: curveFitParameters?.getSInf(),
                        s0: curveFitParameters?.getS0(),
                        slope: slope,
                        hillSlope: curveFitParameters?.getHillCoef(),
                        concentrations: doseResponsePointsMap.concentrations,
                        activities: doseResponsePointsMap.activities,
                        yAxisLabel: "${concentrationResponseSeries?.getYAxisLabel()}",
                        xAxisLabel: "Log(Concentration) ${testConcentrationUnit}"
                ]
                valueMap.put("plot", mapParams)


            }
            List<String> curveParams = []
            if (priorityDisplay) {
                curveParams.add("${priorityDisplay} : ${slope}")
            }
            curveParams.add("sInf: " + new ExperimentalValueUtil(curveFitParameters.sInf, false).toString())
            curveParams.add("s0: " + new ExperimentalValueUtil(curveFitParameters.s0, false).toString())
            curveParams.add("HillSlope: " + new ExperimentalValueUtil(curveFitParameters.hillCoef, false).toString())
            valueMap.put("curveFitParams", curveParams)

            valueMap.put("miscData", concentrationResponseSeries.miscData)
        }
        return valueMap
    }

}


