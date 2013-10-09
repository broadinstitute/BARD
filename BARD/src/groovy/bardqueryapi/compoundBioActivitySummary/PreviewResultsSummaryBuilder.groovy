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
 * TODO: START WARNING!!!!!
 * NOTE THAT THIS CLASS SHOULD BE REFACTORED TO READ THE RESULT TYPE HIERARCHY
 * FROM THE RESULT_TYPE_TREE IN CAP. RIGHT NOW THE DICTIONARY IDs ARE HARD-CODED AND IF THAT CHANGES THIS FUNCTIONALITY WILL BREAK
 * ALSO NEWLY ADDED RESULT TYPES MAY NOT BE ACCOUNTED FOR. END WARNING!!!!!
 *
 * Created with IntelliJ IDEA.
 * User: jasiedu
 *
 */
class PreviewResultsSummaryBuilder {
    private Map<Long, ResultTypeTree> resultTypeTreeMap = [:]
    static final Logger log = Logger.getLogger(PreviewResultsSummaryBuilder.class)

    /**
     * So called high priority elements
     */
    private static List<Long> HIGH_PRIORITY_DICT_ELEM = [
            new Long(917),
            new Long(952),
            new Long(956),
            new Long(959),
            new Long(961),
            new Long(963),
            new Long(964),
            new Long(965),
            new Long(966),
            new Long(983),
            new Long(984),
            new Long(993),
            new Long(997),
            new Long(1000),
            new Long(1002),
            new Long(1008),
            new Long(1015),
            new Long(1378),
            new Long(1387)
    ]


    protected boolean isCurveFitParameter(final Long resultTypeId) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTree(resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.fullPath.contains("concentration endpoint") ||
                    resultTypeTree.fullPath.contains("concentration-response curve")) {
                return true
            }
        }
        return false
    }

    protected ResultTypeTree addOrFindResultTypeTree(final Long resultTypeId) {
        ResultTypeTree resultTypeTree = this.resultTypeTreeMap.get(resultTypeId)
        if (!resultTypeTree) {
            Element element = Element.get(resultTypeId)
            if (element) {
                resultTypeTree = ResultTypeTree.findByElementAndLeaf(element, true)
                this.resultTypeTreeMap.put(resultTypeId, resultTypeTree)
            }
        }
        return resultTypeTree
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
    protected boolean isPercentResponse(Long resultTypeId) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTree(resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.fullPath.toUpperCase().contains("RESPONSE ENDPOINT") &&
                    resultTypeTree.fullPath.toUpperCase().contains("PERCENT RESPONSE")) {
                return true
            }
        }
        return false
    }
    /**
     * Set the curve fit parameters
     * Note that if we were to add a parameter that is not accounted for here, it will
     * we can likely not fit the curve
     * @param curveFitParameters
     * @param resultTypeTree
     * @param jsonResult
     */
    protected void setCurveFitParameters(CurveFitParameters curveFitParameters, ResultTypeTree resultTypeTree, JsonResult jsonResult) {
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
    protected Map convertToDoseResponse(List<JsonResult> jsonResults) {

        List<Double> concentrations = []
        List<Double> activities = []
        List<WebQueryValue> childElements = []
        CurveFitParameters curveFitParameters = new CurveFitParameters()
        String responseUnits = ""
        String concentrationUnits = ""
        for (JsonResult jsonResult : jsonResults) {
            final Long resultTypeId = jsonResult.resultTypeId
            if (isCurveFitParameter(resultTypeId)) {
                setCurveFitParameters(curveFitParameters, resultTypeTreeMap.get(resultTypeId), jsonResult)
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


    protected ConcentrationResponseSeriesValue handleHighPriorityElements(final JsonResult jsonResult, List<WebQueryValue> childElements = [], Double yNormMin = null,
                                                                          Double yNormMax = null) {
        final Long dictionaryId = jsonResult.resultTypeId
        Pair<StringValue, StringValue> title =
            new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueNum))
        LinkValue dictionaryElement

        if (dictionaryId) {
            dictionaryElement = new LinkValue(value: "/BARD/dictionaryTerms/#${jsonResult.resultTypeId}")
        }


        String qualifier = jsonResult.qualifier ?: ""

        Map concentrationResponseMap = convertToDoseResponse(jsonResult.related)
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

        //add context items , they are child elements
        final List<JsonResultContextItem> resultContextItems = jsonResult.contextItems
        if (resultContextItems) {
            childElements << contextItemsToChildElements(resultContextItems)
        }
        return concentrationResponseSeriesValue

    }

    protected void processJsonResults(final JsonResult jsonResult, List<WebQueryValue> childElements = [], List<WebQueryValue> values = [], Double yNormMin = null,
                                      Double yNormMax = null) {

        final Long resultTypeId = jsonResult.resultTypeId
        if (HIGH_PRIORITY_DICT_ELEM.contains(resultTypeId)) { //TODO: refactor to read from Result_Type_Tree . We need the elements that could contain dose curves
            values << handleHighPriorityElements(jsonResult, childElements, yNormMin, yNormMax)
        } else if (isPercentResponse(resultTypeId)) {
            values << handleEfficacyMeasures(jsonResult)
        } else {  //everything else is a child element
            childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
        }
    }

    protected PairValue handleEfficacyMeasures(final JsonResult jsonResult) {
        final Long dictionaryId = jsonResult.resultTypeId

        Pair<StringValue, StringValue> pair =
            new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueDisplay))
        LinkValue dictionaryElement

        if (dictionaryId) {
            dictionaryElement = new LinkValue(value: "/BARD/dictionaryTerms/#${jsonResult.resultTypeId}")
        }
        return new PairValue(value: pair, dictionaryElement: dictionaryElement)

    }

    protected List<WebQueryValue> contextItemsToChildElements(List<JsonResultContextItem> jsonResultContextItems) {
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
    public Map convertCapExperimentResultsToValues(List<JsonResult> rootElements,
                                                   Double yNormMin = null,
                                                   Double yNormMax = null) {


        List<WebQueryValue> values = []
        List<WebQueryValue> childElements = []

        StringValue outcome = null

        for (JsonResult jsonResult : rootElements) {

            final long dictionaryId = jsonResult.resultTypeId
            if (dictionaryId == 896) { //this is the outcome result type. TODO: refactor, read from database
                outcome = new StringValue(value: jsonResult.valueDisplay)

                for (JsonResult relatedElements : jsonResult.related) {
                    processJsonResults(relatedElements, childElements, values, yNormMin, yNormMax)
                    if (relatedElements.contextItems) {
                        childElements << contextItemsToChildElements(relatedElements.contextItems)
                    }
                }
            } else {
                processJsonResults(jsonResult, childElements, values, yNormMin, yNormMax)
                if (jsonResult.contextItems) {
                    childElements << contextItemsToChildElements(jsonResult.contextItems)
                }
            }
        }

        final List<WebQueryValue> sortedValues = sortWebQueryValues(values)
        return [experimentalvalues: sortedValues, outcome: outcome, childElements: childElements]
    }
    /**
     *
     * extract the units from a given string. Assume that the units are space separated from the value
     *
     */
    protected String parseUnits(String displayValue) {
        final String[] split = displayValue.trim().split(" ")
        if (split.length > 1) {
            return split[1].trim()
        }
        return ""

    }

    protected List<WebQueryValue> sortWebQueryValues(List<WebQueryValue> unsortedValues) {
        return unsortedValues.sort { WebQueryValue valueInst ->
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
                case StringValue.class.simpleName:
                    return 4
                    break;
                default:
                    return 99
            }
        }
    }
}
