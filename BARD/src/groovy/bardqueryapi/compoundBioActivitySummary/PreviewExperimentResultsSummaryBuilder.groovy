package bardqueryapi.compoundBioActivitySummary

import bard.core.rest.spring.experiment.ActivityConcentrationMap
import bard.core.rest.spring.experiment.CurveFitParameters
import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.db.dictionary.ResultTypeTree
import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bardqueryapi.*
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 *
 */
class PreviewExperimentResultsSummaryBuilder {
    //Local cache, for mapping result type ids to the ResultType tree. We should consider making it application wide

    private Map<Long, ResultTypeTree> resultTypeTreeCache = [:]
    static final Logger log = Logger.getLogger(PreviewExperimentResultsSummaryBuilder.class)
    OntologyDataAccessService ontologyDataAccessService

    /**
     * Whether this result type represents a dose response curve parameter
     * @param resultTypeId
     * @return true/false
     *
     *  We assume that if you have a concentration-response curve or concentration end point in your path
     *  you are a curve fit parameter. This is not always true for concentration endpoint (for e.g logEC50),
     *  but in that case the context in which it is being used helps
     */
    protected boolean isCurveFitParameter(final Long resultTypeId) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTreeToCache(resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.fullPath.toUpperCase().contains("CONCENTRATION-RESPONSE CURVE")) {
                return true
            }
        }
        return false
    }
    /**
     * Lets find the ResultTypeTree from the cache, if it does not exists lets get it from the database
     * and add it to the local cache
     * @param resultTypeId
     * @return
     */
    protected ResultTypeTree addOrFindResultTypeTreeToCache(final Long resultTypeId) {
        ResultTypeTree resultTypeTree = this.resultTypeTreeCache.get(resultTypeId)
        if (!resultTypeTree) {
            Element element = Element.get(resultTypeId)
            if (element) {
                resultTypeTree = ResultTypeTree.findByElement(element)
                this.resultTypeTreeCache.put(resultTypeId, resultTypeTree)
            }
        }
        return resultTypeTree
    }
    /**
     * If this resultTypeId references a pubchem outcome.
     * @param resultTypeId
     * @return true/false
     */
    protected boolean isPubChemOutcome(Long resultTypeId) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTreeToCache(resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.label.toUpperCase().equals("PUBCHEM OUTCOME")) {
                return true
            }
        }
        return false
    }
    /**
     * Here we only look for things with 'percent response' or 'percent endpoint' in its path.
     * NOTE that this is not a scaleable solution at all.
     *
     * We are thinking of changing the model so that the jsonresult object contains enough information for
     * us to not have to resort to this hack. However, this should work for most cases, since most pubchem assays
     * fits this hack
     * @param resultTypeId
     * @return true/false
     */
    protected boolean isPercentResponse(Long resultTypeId) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTreeToCache(resultTypeId)
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
     * Convert the list of jsonResults to Dose Response Curve points
     * @param jsonResults
     * @return
     */
    protected void convertToDoseResponse(List<JsonResult> jsonResults, Map resultsMap) {

        List<Double> concentrations = []
        List<Double> activities = []
        CurveFitParameters curveFitParameters = new CurveFitParameters()
        String responseUnits = ""
        String concentrationUnits = ""
        for (JsonResult jsonResult : jsonResults) {
            final Long resultTypeId = jsonResult.resultTypeId
            String resultType = jsonResult.resultType

            if (resultsMap.priorityElements.contains(resultType?.trim())) {
                resultsMap.priorityElementValues.add(new StringValue(value: jsonResult.valueDisplay))
            }
            if (isCurveFitParameter(resultTypeId)) {
                setCurveFitParameters(curveFitParameters, resultTypeTreeCache.get(resultTypeId), jsonResult)
            } else if (jsonResult.contextItems) {
                final List<JsonResultContextItem> items = jsonResult.getContextItems()

                for (JsonResultContextItem resultContextItem : items) {
                    if (isScreeningConcentration(resultContextItem.attributeId)) { //this is a screening concentration
                        final float activity = jsonResult.valueNum
                        if (!responseUnits) {
                            responseUnits = jsonResult.resultType
                        }
                        final float concentration = resultContextItem.valueNum
                        //get concentration units
                        String valueDisplay = resultContextItem.valueDisplay
                        if (!concentrationUnits) {
                            concentrationUnits = parseUnits(valueDisplay)
                        }

                        activities.add(activity.doubleValue())
                        concentrations.add(concentration.doubleValue())
                    } else {
                        if (!resultsMap.priorityElements.contains(resultType.trim())) {
                            resultsMap.childElements << new StringValue(value: resultContextItem.attribute + ":" + resultContextItem.valueDisplay)
                        }
                    }
                }
                resultsMap.activityConcentrationMap = new ActivityConcentrationMap(activities: activities, concentrations: concentrations)
                resultsMap.curveFitParameters = curveFitParameters
                resultsMap.responseUnits = responseUnits
                resultsMap.concentrationUnits = concentrationUnits
            } else {
                if (!resultsMap.priorityElements.contains(resultType.trim())) {
                    resultsMap.childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
                }
            }
            if (jsonResult.related) {
                convertExperimentResultsToTableModelCellsAndRows(resultsMap, jsonResult.related)
            }
        }

    }

    protected void handleConcentrationResponsePoints(final JsonResult jsonResult, final Map resultsMap) {

        final Long dictionaryId = jsonResult.resultTypeId
        Pair<StringValue, StringValue> title =
            new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueNum))
        LinkValue dictionaryElement

        if (dictionaryId) {
            dictionaryElement = new LinkValue(value: "/BARD/dictionaryTerms/#${jsonResult.resultTypeId}")
        }


        String qualifier = jsonResult.qualifier ?: ""

        convertToDoseResponse(jsonResult.related, resultsMap)

        ConcentrationResponseSeriesValue concentrationResponseSeriesValue =
            new ConcentrationResponseSeriesValue(value: resultsMap.activityConcentrationMap,
                    title: new PairValue(value: title, dictionaryElement: dictionaryElement),
                    curveFitParameters: resultsMap.curveFitParameters,
                    slope: jsonResult.valueNum,
                    responseUnit: resultsMap.responseUnits,
                    testConcentrationUnit: resultsMap.concentrationUnits,
                    qualifier: qualifier,
                    yNormMin: resultsMap.yNormMin,
                    yNormMax: resultsMap.yNormMax)
        concentrationResponseSeriesValue.yAxisLabel = resultsMap.responseUnits
        concentrationResponseSeriesValue.xAxisLabel = resultsMap.concentrationUnits ? "Concentration (log [${resultsMap.concentrationUnits}])" : ""

        //add context items , they are child elements
        final List<JsonResultContextItem> resultContextItems = jsonResult.contextItems
        if (resultContextItems) {

            resultsMap.childElements << contextItemsToChildElements(resultContextItems)
        }
        resultsMap.experimentalValues << concentrationResponseSeriesValue

    }

    protected void handleEfficacyMeasures(final JsonResult jsonResult, final Map resultsMap) {
        final Long dictionaryId = jsonResult.resultTypeId

        Pair<StringValue, StringValue> pair =
            new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueDisplay))
        LinkValue dictionaryElement

        if (dictionaryId) {
            dictionaryElement = new LinkValue(value: "/BARD/dictionaryTerms/#${jsonResult.resultTypeId}")
        }
        resultsMap.experimentalValues << [new PairValue(value: pair, dictionaryElement: dictionaryElement) ]
        if (jsonResult.contextItems) {
            resultsMap.childElements << contextItemsToChildElements(jsonResult.contextItems)
        }
    }

    protected List<WebQueryValue> contextItemsToChildElements(List<JsonResultContextItem> jsonResultContextItems) {
        List<WebQueryValue> childElements = []
        for (JsonResultContextItem jsonResultContextItem : jsonResultContextItems) {
            childElements << new StringValue(value: jsonResultContextItem.attribute + ":" + jsonResultContextItem.valueDisplay)
        }
        return childElements
    }



    boolean isScreeningConcentration(Long attributeId) {
        String startOfFullPath = "project management> experiment> result detail> screening concentration"
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptors(startOfFullPath, null)
        for (Descriptor descriptor : descriptors) {
            if (descriptor.element.id == attributeId) {
                return true
            }
        }
        return false
    }

    boolean isConcentrationResponseEndPoint(JsonResult jsonResult) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTreeToCache(jsonResult.resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.fullPath.toUpperCase().contains("RESULT TYPE> CONCENTRATION ENDPOINT")) {

                final List<JsonResult> related = jsonResult.related
                if (!related) { //if it has no child nodes
                    return false
                }
                for (JsonResult relatedResult : related) {
                    if (isPercentResponse(relatedResult.resultTypeId)) {
                        final List<JsonResultContextItem> resultContextItems = relatedResult.contextItems
                        if (!resultContextItems) {
                            return false
                        }
                        for (JsonResultContextItem jsonResultContextItem : resultContextItems) {
                            if (isScreeningConcentration(jsonResultContextItem.attributeId)) { //this is a screening concentration
                                return true
                            }
                        }

                    }
                }
            }
        }
        return false
    }
    /**
     * resultsMap.yNormMin = null
     * resultsMap.yNormMax = null
     * @param rootElements
     * @param yNormMin
     * @param yNormMax
     * @return
     */
    public void convertExperimentResultsToTableModelCellsAndRows(Map resultsMap,
                                                                 List<JsonResult> rootElements) {

        for (JsonResult jsonResult : rootElements) {
            convertSingleExperimentResult(resultsMap, jsonResult)
        }
    }

    /**
     *
     * @param rootElements
     * @param yNormMin
     * @param yNormMax
     * @return
     */
    protected void convertSingleExperimentResult(Map resultsMap,
                                                 JsonResult jsonResult) {
        final long resultTypeId = jsonResult.resultTypeId
        String display = jsonResult.valueDisplay
        String resultType = jsonResult.resultType
        if (resultsMap.priorityElements.contains(resultType.trim())) {
            resultsMap.priorityElementValues.add(new StringValue(value: resultType + ":" + display))
        }
        if (isConcentrationResponseEndPoint(jsonResult)) {

            handleConcentrationResponsePoints(jsonResult, resultsMap)
            //go ahead and do dose related stuff
        } else if (isPercentResponse(resultTypeId)) {
            handleEfficacyMeasures(jsonResult, resultsMap)
            if (jsonResult.related) {
                convertExperimentResultsToTableModelCellsAndRows(resultsMap, jsonResult.related)
            }
        } else if (isPubChemOutcome(resultTypeId)) { //what we actually need is everything at the root of the measures tree
            // that is not a Concentration/response endpoint so that we can display it in a column
            StringValue outcome = new StringValue(value: jsonResult.valueDisplay)
            resultsMap.outcome = outcome
            if (jsonResult.related) {
                convertExperimentResultsToTableModelCellsAndRows(resultsMap, jsonResult.related)
            }
        } else {
            //treat as child element
            if (!resultsMap.priorityElements.contains(jsonResult.resultType)) {
                resultsMap.childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
            }
            if (jsonResult.contextItems) {
                resultsMap.childElements << contextItemsToChildElements(jsonResult.contextItems)
            }
            if (jsonResult.related) {
                convertExperimentResultsToTableModelCellsAndRows(resultsMap, jsonResult.related)
            }
        }
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
