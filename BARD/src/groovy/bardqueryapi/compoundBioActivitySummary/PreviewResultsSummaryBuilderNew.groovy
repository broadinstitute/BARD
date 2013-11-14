package bardqueryapi.compoundBioActivitySummary

import bard.core.rest.spring.experiment.ActivityConcentrationMap
import bard.core.rest.spring.experiment.CurveFitParameters
import bard.db.dictionary.Element
import bard.db.dictionary.ResultTypeTree
import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bardqueryapi.*
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import org.apache.log4j.Logger

/**
 *
 * Created with IntelliJ IDEA.
 * User: jasiedu
 *
 */
class PreviewResultsSummaryBuilderNew {

    static final Logger log = Logger.getLogger(PreviewResultsSummaryBuilderNew.class)
    /**
     * Set the curve fit parameters
     * Note that if we were to add a parameter that is not accounted for here, it will
     * we can likely not fit the curve
     * @param curveFitParameters
     * @param resultTypeTree
     * @param jsonResult
     */
    static void setCurveFitParameters(CurveFitParameters curveFitParameters, ResultTypeTree resultTypeTree, JsonResult jsonResult) {
        switch (resultTypeTree.label.toUpperCase()) {
            case "HILL COEFFICIENT":
                curveFitParameters.setHillCoef(jsonResult.valueNum)
                break
            case "HILL S0":
                curveFitParameters.setS0(jsonResult.valueNum)
                break
            case "HILL SINF":
                curveFitParameters.setSInf(jsonResult.valueNum)
                break
            default: //We call everything else logEc50. Need to fix this. This is actually the slope
                curveFitParameters.setLogEc50(jsonResult.valueNum)
                break
        }
    }
    /**
     * Convert the list of jsonResults to Dose Response Curve values
     * @param jsonResults
     * @return
     */
    static Map convertToDoseResponse(Map<Long, ResultTypeTree> resultTypeIdToLabelMap, List<JsonResult> jsonResults) {

        List<Double> concentrations = []
        List<Double> activities = []
        List<WebQueryValue> childElements = []
        CurveFitParameters curveFitParameters = new CurveFitParameters()
        String responseUnits = ""
        String concentrationUnits = ""
        for (JsonResult jsonResult : jsonResults) {
            final Long resultTypeId = jsonResult.resultTypeId

            if (isCurveFitParameter(resultTypeIdToLabelMap, resultTypeId)) {
                setCurveFitParameters(curveFitParameters, resultTypeIdToLabelMap.get(resultTypeId), jsonResult)
            } else if (jsonResult.valueNum && jsonResult.contextItems) {
                final float activity = jsonResult.valueNum
                final List<JsonResultContextItem> items = jsonResult.getContextItems()
                final JsonResultContextItem resultContextItem = items.get(0)
                final float concentration = resultContextItem.valueNum


                if (!responseUnits) {
                    responseUnits = jsonResult.resultType
                }

                //get concentration units
                String valueDisplay = resultContextItem.valueDisplay
                if (!concentrationUnits) {
                    concentrationUnits = parseUnits(valueDisplay)
                }

                activities.add(activity.doubleValue())
                concentrations.add(concentration.doubleValue())
            } else {
                childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
            }

        }
        return [
                activityConcentrationMap: new ActivityConcentrationMap(activities: activities, concentrations: concentrations),
                curveFitParameters: curveFitParameters, responseUnit: responseUnits,
                testConcentrationUnit: concentrationUnits,
                childElements: childElements
        ]

    }
    /**
     *
     * extract the units from a given string. Assume that the units are space separated from the value
     *
     */
    static String parseUnits(String displayValue) {
        final String[] split = displayValue.trim().split(" ")
        if (split.length > 1) {
            return split[1].trim()
        }
        return ""

    }

    static boolean isCurveFitParameter(Map<Long, ResultTypeTree> resultTypeIdToLabelMap, Long resultTypeId) {
        ResultTypeTree resultTypeTree = resultTypeIdToLabelMap.get(resultTypeId)
        if (!resultTypeTree) {
            Element element = Element.get(resultTypeId)
            resultTypeTree = ResultTypeTree.findByElement(element)
            resultTypeIdToLabelMap.put(resultTypeId, resultTypeTree)
        }
        if (resultTypeTree.leaf) {
            if (resultTypeTree.fullPath.toUpperCase().contains("GRAPHICAL CALCULATION") ||
                    resultTypeTree.fullPath.toUpperCase().contains("CONCENTRATION ENDPOINT")) {
                return true
            }
        }
        return false
    }
    /**
     * This signifies to us that we have points on a curve that we need to deal with
     * @param resultTypeIdToLabelMap
     * @param resultTypeId
     * @return
     */
    static boolean isNonLeafPoint(Map<Long, ResultTypeTree> resultTypeIdToLabelMap, Long resultTypeId) {
        ResultTypeTree resultTypeTree = resultTypeIdToLabelMap.get(resultTypeId)
        if (!resultTypeTree) {
            Element element = Element.get(resultTypeId)
            resultTypeTree = ResultTypeTree.findByElement(element)
            resultTypeIdToLabelMap.put(resultTypeId, resultTypeTree)
        }
        if (!resultTypeTree.leaf) {
            if (resultTypeTree.fullPath.toUpperCase().contains("RESPONSE ENDPOINT") ||
                    resultTypeTree.fullPath.contains("CONCENTRATION ENDPOINT")) {
                //TODO: We only handle response and concentration endpoints in the show experiment page
                return true
            }
        }
        return false
    }
    /**
     * Here we only look for things with 'percent response' in its path.
     * NOTE that this is not a scaleable solution at all.
     *
     * We are thinking of changing the model so that the jsonresult object contains enough information for
     * us to not have to resort to this hack. However, this should work for most cases, since most pubchem assays
     * fits this hack
     * @param resultTypeIdToLabelMap
     * @param resultTypeId
     * @return
     */
    static boolean isPercentResponse(Map<Long, ResultTypeTree> resultTypeIdToLabelMap, Long resultTypeId) {
        ResultTypeTree resultTypeTree = resultTypeIdToLabelMap.get(resultTypeId)
        if (!resultTypeTree) {
            Element element = Element.get(resultTypeId)
            resultTypeTree = ResultTypeTree.findByElement(element)
            resultTypeIdToLabelMap.put(resultTypeId, resultTypeTree)
        }
        if (resultTypeTree.fullPath.toUpperCase().contains("PERCENT RESPONSE")) {
            return true
        }

        return false
    }

    static void processJsonResults(final JsonResult jsonResult, List<WebQueryValue> childElements = [], List<WebQueryValue> values = [],
                                   Map<Long, ResultTypeTree> resultTypeIdToResultTypeMap,
                                   Double yNormMin = null,
                                   Double yNormMax = null) {

        final Long resultTypeId = jsonResult.resultTypeId
        if (isNonLeafPoint(resultTypeIdToResultTypeMap, resultTypeId) && jsonResult.related) { //We have likely found something with a dose response curve
            Pair<StringValue, StringValue> title =
                new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueDisplay))
            LinkValue dictionaryElement

            if (resultTypeId) {
                dictionaryElement = new LinkValue(value: "/BARD/dictionaryTerms/#${jsonResult.resultTypeId}")
            }


            String qualifier = jsonResult.qualifier ?: ""

            Map concentrationResponseMap = convertToDoseResponse(resultTypeIdToResultTypeMap, jsonResult.related)
            childElements << concentrationResponseMap.childElements
            ActivityConcentrationMap doseResponsePointsMap = concentrationResponseMap.activityConcentrationMap
            CurveFitParameters curveFitParameters = concentrationResponseMap.curveFitParameters
            String responseUnits = concentrationResponseMap.responseUnit
            String concentrationUnits = concentrationResponseMap.testConcentrationUnit

            ConcentrationResponseSeriesValue concentrationResponseSeriesValue = new ConcentrationResponseSeriesValue(value: doseResponsePointsMap,
                    title: new PairValue(value: title, dictionaryElement: dictionaryElement),
                    curveFitParameters: curveFitParameters,
                    slope: jsonResult.valueNum,
                    responseUnit: responseUnits,
                    testConcentrationUnit: concentrationUnits,
                    qualifier: qualifier,
                    yNormMin: yNormMin,
                    yNormMax: yNormMax)
            concentrationResponseSeriesValue.yAxisLabel = responseUnits
            concentrationResponseSeriesValue.xAxisLabel = concentrationUnits ? "Concentration (log [${concentrationUnits}])" : ""
            values << concentrationResponseSeriesValue

            //add context items , they are child elements
            final List<JsonResultContextItem> resultContextItems = jsonResult.contextItems
            if (resultContextItems) {
                childElements << contextItemsToChildElements(resultContextItems)
            }

        } else if (isPercentResponse(resultTypeIdToResultTypeMap, resultTypeId)) {
            Pair<StringValue, StringValue> pair =
                new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueDisplay))
            LinkValue dictionaryElement

            if (resultTypeId) {
                dictionaryElement = new LinkValue(value: "/BARD/dictionaryTerms/#${jsonResult.resultTypeId}")
            }
            PairValue pairValue = new PairValue(value: pair, dictionaryElement: dictionaryElement)
            values << pairValue
        } else {  //everything else is a child element
            childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
        }
    }

    static List<WebQueryValue> contextItemsToChildElements(List<JsonResultContextItem> jsonResultContextItems) {
        List<WebQueryValue> childElements = []
        for (JsonResultContextItem jsonResultContextItem : jsonResultContextItems) {
            childElements << new StringValue(value: jsonResultContextItem.attribute + ":" + jsonResultContextItem.valueDisplay)
        }
        return childElements
    }
    /**
     * Map an experiment (exptData) into a list of table-model 'Values'.
     * The main result data is in the experiment's priority elements.
     * @param exptData
     * @param outcomeFacetMap a pass-through collection to generate facets that match the different priority-element types (i.e., result-types) we have in the experiment data.
     * @return
     */
    static Map convertCapExperimentResultsToValues(List<JsonResult> rootElements,
                                                   Double yNormMin = null,
                                                   Double yNormMax = null) {


        List<WebQueryValue> values = []
        List<WebQueryValue> childElements = []
        List<WebQueryValue> rootElementNodes = []

        StringValue outcome = null

        //We use this as a cache, so we do not look in the database everytime for a result type id
        Map<Long, ResultTypeTree> resultTypeIdToResultTypeTreeMap = [:]

        for (JsonResult jsonResult : rootElements) {

            final long resultTypeId = jsonResult.resultTypeId
            ResultTypeTree resultTypeTree = resultTypeIdToResultTypeTreeMap.get(resultTypeId)
            if (!resultTypeTree) {
                Element element = Element.get(resultTypeId)
                resultTypeTree = ResultTypeTree.findByElement(element)
                resultTypeIdToResultTypeTreeMap.put(resultTypeId, resultTypeTree)

            }
            //This is a controlled vocabulary so we do not expect the term to change
            if (resultTypeTree.label.toUpperCase()=='PUBCHEM OUTCOME') { //pubchem outcome. Now process the children. Code is still volatile, changes to the show experiment page will break this
                outcome = new StringValue(value: jsonResult.valueDisplay)

                for (JsonResult relatedElements : jsonResult.related) {
                    processJsonResults(relatedElements, childElements, values, resultTypeIdToResultTypeTreeMap, yNormMin, yNormMax)
                    if (relatedElements.contextItems) {
                        childElements << contextItemsToChildElements(relatedElements.contextItems)
                    }
                }
            } else {
                processJsonResults(jsonResult, childElements, values, resultTypeIdToResultTypeTreeMap, yNormMin, yNormMax)
                if (jsonResult.contextItems) {
                    childElements << contextItemsToChildElements(jsonResult.contextItems)
                }
            }
        }

        final List<WebQueryValue> sortedValues = values.sort { WebQueryValue valueInst ->
            //Sort based on the WebQueryValue specific type
            switch (valueInst.class.simpleName) {
                case ConcentrationResponseSeriesValue.class.simpleName:
                    return 1
                    break;
                case ListValue.class.simpleName:
                    return 2
                    break;
                case PairValue.class.simpleName:
                    return 3
                    break;
                case java.lang.StringValue.class.simpleName:
                    return 4
                    break;
                default:
                    return 99
            }
        }
        return [experimentalvalues: sortedValues, outcome: outcome, childElements: childElements, rootElements: rootElementNodes]
    }
}
