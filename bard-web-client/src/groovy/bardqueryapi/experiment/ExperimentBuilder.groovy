package bardqueryapi.experiment

import bard.core.adapter.CompoundAdapter
import bard.core.util.ExperimentalValueUtil
import bardqueryapi.NormalizeAxis
import bardqueryapi.WebQueryTableModel
import bardqueryapi.WebQueryValueModel
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import bard.core.rest.spring.experiment.*

class ExperimentBuilder {


    List<WebQueryValueModel> buildHeader(List<String> priorityDisplays, List<String> dictionaryIds, final boolean hasPlot, final boolean hasChildElements) {
        List<WebQueryValueModel> columnHeaders = []
        columnHeaders.add(new WebQueryValueModel("SID"))
        columnHeaders.add(new WebQueryValueModel("CID"))
        columnHeaders.add(new WebQueryValueModel("Structure"))
        columnHeaders.add(new WebQueryValueModel("Outcome"))
        columnHeaders.add(new WebQueryValueModel([priorityDisplays: priorityDisplays, dictionaryIds: dictionaryIds]))
        columnHeaders.add(new WebQueryValueModel("Experiment Descriptors"))
        if (hasChildElements) {
            columnHeaders.add(new WebQueryValueModel("Child Elements"))
        }
        if (hasPlot) {
            columnHeaders.add(new WebQueryValueModel("Concentration Response Series"))
            columnHeaders.add(new WebQueryValueModel("Concentration Response Plot"))
            columnHeaders.add(new WebQueryValueModel("Misc Data"))
        }
        return columnHeaders
    }

    List<WebQueryValueModel> addRow(final Activity activity,
                                    final NormalizeAxis normalizeYAxis,
                                    final Double yNormMin,
                                    final Double yNormMax,
                                    List<String> priorityDisplays,
                                    final Map<Long, CompoundAdapter> compoundAdapterMap) {

        List<WebQueryValueModel> rowData = new ArrayList<WebQueryValueModel>()

        Long sid = activity.sid
        WebQueryValueModel valueModel = new WebQueryValueModel(sid)
        rowData.add(valueModel)


        Long cid = activity.cid
        valueModel = new WebQueryValueModel(cid)
        rowData.add(valueModel)

        final CompoundAdapter compoundAdapter = compoundAdapterMap.get(cid)
        Map<String, String> structure = [sid: sid.toString(), cid: cid.toString(),
                smiles: compoundAdapter?.structureSMILES, cname: compoundAdapter?.name,
                numberOfActiveAssays: compoundAdapter?.numberOfActiveAssays, numberOfAssays: compoundAdapter?.numberOfAssays]

        valueModel = new WebQueryValueModel(structure)
        rowData.add(valueModel)


        ResultData resultData = activity?.resultData
        String outcome = resultData.outcome
        valueModel = new WebQueryValueModel(outcome)
        rowData.add(valueModel)

        List<String> displayList = []
        if (resultData?.hasPriorityElements()) {
            int i = 0
            displayList = resultData.priorityElements.collect {PriorityElement priorityElement -> "${priorityDisplays[i++]}: ${priorityElement.toDisplay()}"}
        }
        valueModel = new WebQueryValueModel(displayList)
        rowData.add(valueModel)

        List<String> displayElements = []
        if (!resultData.isMapped()) {
            displayElements.add("TIDs not yet mapped to a result hierarchy")
        }
        for (RootElement rootElement : resultData.rootElements) {
            if (rootElement.toDisplay()) {
                displayElements.add(rootElement.toDisplay())
            }
        }
        valueModel = new WebQueryValueModel(displayElements)
        rowData.add(valueModel)

        //if display elements add
        List<String> childElements = []
        for (PriorityElement priorityElement in resultData.priorityElements) {
            if (priorityElement?.hasChildElements()) {
                for (ActivityData activityData : priorityElement.childElements) {
                    if (activityData.toDisplay()) {
                        childElements.add(activityData.toDisplay())
                    }
                }
            }
        }
        if (childElements) {
            valueModel = new WebQueryValueModel(childElements)
            rowData.add(valueModel)
        }

        if (resultData.hasPlot()) {
            List<Map> listOfConcRespMap = []
            for (PriorityElement priorityElement in resultData.priorityElements) {
                final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.concentrationResponseSeries
                if (concentrationResponseSeries) {
                    Map concRespMap = concentrationResponseMap(concentrationResponseSeries, normalizeYAxis,
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

    public WebQueryTableModel buildModel(Map experimentDetails) {
        final WebQueryTableModel webQueryTableModel = new WebQueryTableModel()

        webQueryTableModel.additionalProperties.put("experimentName", experimentDetails?.experiment?.name)
        webQueryTableModel.additionalProperties.put("bardExptId", experimentDetails?.experiment?.bardExptId)
        webQueryTableModel.additionalProperties.put("capExptId", experimentDetails?.experiment?.capExptId)
        webQueryTableModel.additionalProperties.put("bardAssayId", experimentDetails?.experiment?.bardAssayId)
        webQueryTableModel.additionalProperties.put("capAssayId", experimentDetails?.experiment?.capAssayId)
        webQueryTableModel.additionalProperties.put("total", experimentDetails?.total)
        webQueryTableModel.additionalProperties.put("actives", experimentDetails?.actives)
        webQueryTableModel.additionalProperties.put("confidenceLevel",experimentDetails?.experiment?.confidenceLevel)

        Map<Long, CompoundAdapter> compoundAdapterMap = experimentDetails?.compoundAdaptersMap
        final boolean hasPlot = experimentDetails.hasPlot
        List<String> priorityDisplays = experimentDetails.priorityDisplays
        final boolean hasChildElements = experimentDetails.hasChildElements
        Double yNormMin = null
        Double yNormMax = null
        final NormalizeAxis normalizeYAxis = experimentDetails.normalizeYAxis
        webQueryTableModel.setColumnHeaders(buildHeader(priorityDisplays, experimentDetails?.dictionaryIds, hasPlot, hasChildElements))
        final List<Activity> activities = experimentDetails.activities
        if (normalizeYAxis == NormalizeAxis.Y_NORM_AXIS) {
            yNormMin = experimentDetails.yNormMin
            yNormMax = experimentDetails.yNormMax

        }

        addRows(activities, webQueryTableModel, normalizeYAxis, yNormMin, yNormMax, priorityDisplays, compoundAdapterMap)

        return webQueryTableModel
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
        Map doseResponsePointsMap = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponsePoints)
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


