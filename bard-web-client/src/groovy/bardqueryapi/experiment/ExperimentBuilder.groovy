package bardqueryapi.experiment

import bard.core.SearchParams
import molspreadsheet.MolecularSpreadSheetService
import bard.core.rest.spring.experiment.*
import bardqueryapi.*
import bard.core.util.ExperimentalValueUtil

class ExperimentBuilder {


    List<WebQueryValueModel> buildHeader(final String priorityDisplay, final boolean hasPlot, final boolean hasChildElements) {
        List<WebQueryValueModel> columnHeaders = []
        columnHeaders.add(new WebQueryValueModel("SID"))
        columnHeaders.add(new WebQueryValueModel("CID"))
        columnHeaders.add(new WebQueryValueModel("Structure"))
        columnHeaders.add(new WebQueryValueModel("Outcome"))
        columnHeaders.add(new WebQueryValueModel(priorityDisplay))
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
                                    final Double yNormMax) {
        List<WebQueryValueModel> rowData = new ArrayList<WebQueryValueModel>()

        Long sid = activity.sid
        WebQueryValueModel valueModel = new WebQueryValueModel(sid)
        rowData.add(valueModel)


        Long cid = activity.cid
        valueModel = new WebQueryValueModel(cid)
        rowData.add(valueModel)

        String structure = ""
        valueModel = new WebQueryValueModel(structure)
        rowData.add(valueModel)


        ResultData resultData = activity?.resultData
        String outcome = resultData.outcome
        valueModel = new WebQueryValueModel(outcome)
        rowData.add(valueModel)

        PriorityElement priorityElement = null
        String display = ""
        if (resultData?.hasPriorityElements()) {
            priorityElement = resultData.priorityElements.get(0)  //we assume that there is only one priority element
            display = priorityElement?.toDisplay()
        }
        valueModel = new WebQueryValueModel(display)
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
        if (priorityElement?.hasChildElements()
        ) {
            for (ActivityData activityData : priorityElement.childElements) {
                if (activityData.toDisplay()) {
                    childElements.add(activityData.toDisplay())
                }
            }
            valueModel = new WebQueryValueModel(childElements)
            rowData.add(valueModel)
        }
        if (resultData.hasPlot()) {
            final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.concentrationResponseSeries
            Map m = stuff(concentrationResponseSeries, normalizeYAxis,
                    cid, priorityElement.getSlope(),
                    priorityElement.testConcentrationUnit,
                    yNormMin, yNormMax, display)
            valueModel = new WebQueryValueModel(m)
            rowData.add(valueModel)
        }
        return rowData;

    }

    void addRows(final List<Activity> activities,
                 final WebQueryTableModel webQueryTableModel,
                 final NormalizeAxis normalizeYAxis,
                 final Double yNormMin,
                 final Double yNormMax) {
        for (Activity activity : activities) {
            final List<WebQueryValueModel> rowData = addRow(activity)
            webQueryTableModel.addRowData(rowData)
        }
    }

    public WebQueryTableModel buildModel(Map experimentDetails) {

//        return [total: totalNumberOfRecords, activities: activities,
//                experiment: experimentShow, hasPlot: experimentDetails.hasPlot,
//                priorityDisplay: experimentDetails.priorityDisplay,
//                dictionaryId: experimentDetails.dictionaryId,
//                hasChildElements: experimentDetails.hasChildElements,
//
//        ]
        final WebQueryTableModel webQueryTableModel = new WebQueryTableModel()

        final boolean hasPlot = experimentDetails.hasPlot
        final String priorityDisplay = experimentDetails.priorityDisplay
        final boolean hasChildElements = experimentDetails.hasChildElements
        final Double yNormMin = experimentDetails.yNormMin
        final Double yNormMax = experimentDetails.yNormMax
        final NormalizeAxis normalizeYAxis = experimentDetails.normalizeAxis
        webQueryTableModel.setColumnHeaders(buildHeader(priorityDisplay, hasPlot, hasChildElements))
        final List<Activity> activities = experimentDetails.activities

        addRows(activities, webQueryTableModel, normalizeYAxis, yNormMin, yNormMax)
        return webQueryTableModel
    }

    private Map stuff(ConcentrationResponseSeries concentrationResponseSeries,
                      NormalizeAxis normalizeAxis,
                      Long cid,
                      Double slope,
                      String testConcentrationUnit,
                      Double yNormMin,
                      Double yNormMax, String priorityDisplay) {

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
                valueMap.put("params", mapParams)

            }
            else {
                Map mapParams = [
                        sinf: curveFitParameters?.getSInf(),
                        s0: curveFitParameters?.getS0(),
                        slope: slope,
                        hillSlope: curveFitParameters?.getHillCoef(),
                        concentrations: doseResponsePointsMap.concentrations,
                        activities: doseResponsePointsMap.activities,
                        yAxisLabel: "${concentrationResponseSeries?.getYAxisLabel()}"
                ]
                valueMap.put("params", mapParams)


            }
            List<String> curveParams = []

            curveParams.add(priorityDisplay ?: '' + ": ${slope}")
            curveParams.add("sInf: " + new ExperimentalValueUtil(curveFitParameters.sInf, false).toString())
            curveParams.add("s0: " + new ExperimentalValueUtil(curveFitParameters.s0, false).toString())
            curveParams.add("HillSlope: " + new ExperimentalValueUtil(curveFitParameters.hillCoef, false).toString())
            valueMap.put("curveParams", curveParams)
        }
    }

}


