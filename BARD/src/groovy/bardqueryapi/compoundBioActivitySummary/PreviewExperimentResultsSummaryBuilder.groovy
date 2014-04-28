/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import org.apache.log4j.Logger

import java.text.DecimalFormat

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 *
 */
class PreviewExperimentResultsSummaryBuilder {

    static final String CONCENTRATION_END_POINT = "RESULT TYPE> CONCENTRATION ENDPOINT"
    static final String CONCENTRATION_RESPONSE_CURVE = "CONCENTRATION-RESPONSE CURVE"
    static final String PUBCHEM_OUTCOME = "PUBCHEM OUTCOME"
    static final String RESPONSE_ENDPOINT = "RESPONSE ENDPOINT"
    static final String PERCENT_RESPONSE = "PERCENT RESPONSE"
    static final String SCREENING_CONCENTRATION_PATH = "project management> experiment> result detail> screening concentration"

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
            if (resultTypeTree.fullPath.toUpperCase().contains(CONCENTRATION_RESPONSE_CURVE)) {
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
            if (resultTypeTree.label.toUpperCase().equals(PUBCHEM_OUTCOME)) {
                return true
            }
        }
        return false
    }
    /**
     * Here we only look for things with 'percent response' or 'percent endpoint' in its path.
     * @param resultTypeId
     * @return true/false
     */
    protected boolean isPercentResponse(Long resultTypeId) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTreeToCache(resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.fullPath.toUpperCase().contains(RESPONSE_ENDPOINT) &&
                    resultTypeTree.fullPath.toUpperCase().contains(PERCENT_RESPONSE)) {
                return true
            }
        }
        return false
    }

    final static DecimalFormat TO_THREE_SIG_FIGURES_FORMAT = new DecimalFormat("0.000");
    /**
     * Set the curve fit parameters
     * Note that if we were to add a parameter that is not accounted for here
     * we can likely not fit the curve
     * @param curveFitParameters
     * @param resultTypeTree
     * @param jsonResult
     */
    protected void setCurveFitParameters(CurveFitParameters curveFitParameters, ResultTypeTree resultTypeTree, JsonResult jsonResult, Map resultsMap) {

        if (!jsonResult.valueNum) {
            //turn this into a child element
            resultsMap.childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
            return
        }
        Double curveParam = Double.valueOf(TO_THREE_SIG_FIGURES_FORMAT.format(jsonResult.valueNum))
        switch (resultTypeTree.label.toUpperCase()) {
            case "HILL COEFFICIENT":
                curveFitParameters.setHillCoef(curveParam)
                break
            case "HILL S0":
                curveFitParameters.setS0(curveParam)
                break
            case "HILL SINF":
                curveFitParameters.setSInf(curveParam)
                break
            default: //TODO: We call everything else logEc50. Need to fix this. This is actually the slope
                curveFitParameters.setLogEc50(curveParam)
                break
        }
    }


    protected void handlePotentialScreeningConcentration(final JsonResult jsonResult,
                                                         final Map resultsMap,
                                                         final CurveFitParameters curveFitParameters,
                                                         List<Double> concentrations = [],
                                                         List<Double> activities = [],
                                                         String responseUnits = "",
                                                         String concentrationUnits = "") {


        for (JsonResultContextItem resultContextItem : jsonResult.getContextItems()) {
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
                if (concentration > 0) {
                    activities.add(activity.doubleValue())
                    concentrations.add(concentration.doubleValue())
                }
            } else {
                resultsMap.childElements << new StringValue(value: resultContextItem.attribute + ":" + resultContextItem.valueDisplay)
            }
        }
        resultsMap.activityConcentrationMap = new ActivityConcentrationMap(activities: activities, concentrations: concentrations)
        resultsMap.curveFitParameters = curveFitParameters
        resultsMap.responseUnits = responseUnits
        resultsMap.concentrationUnits = concentrationUnits
    }
    /**
     *
     * @param jsonResults
     * @param resultsMap
     */
    protected void convertResultsToDoseResponse(List<JsonResult> jsonResults, Map resultsMap) {

        CurveFitParameters curveFitParameters = new CurveFitParameters()
        List<Double> concentrations = []
        List<Double> activities = []
        String responseUnits = ""
        String concentrationUnits = ""

        for (JsonResult jsonResult : jsonResults) {

            final Long resultTypeId = jsonResult.resultTypeId
            String resultType = jsonResult.resultType

            if (resultsMap.priorityElements.contains(resultType?.trim())) {
                resultsMap.priorityElementValues << new StringValue(value: resultType + ":" + jsonResult.valueDisplay)
            }
            if (isCurveFitParameter(resultTypeId)) {
                setCurveFitParameters(curveFitParameters, resultTypeTreeCache.get(resultTypeId), jsonResult, resultsMap)
            } else if (jsonResult.contextItems) {
                handlePotentialScreeningConcentration(jsonResult, resultsMap, curveFitParameters, concentrations, activities, responseUnits, concentrationUnits)
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

        convertResultsToDoseResponse(jsonResult.related, resultsMap)

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
    /**
     * Convert context items to child elements
     * @param jsonResultContextItems
     * @return
     */
    protected List<WebQueryValue> contextItemsToChildElements(List<JsonResultContextItem> jsonResultContextItems) {
        List<WebQueryValue> childElements = []
        for (JsonResultContextItem jsonResultContextItem : jsonResultContextItems) {
            childElements << new StringValue(value: jsonResultContextItem.attribute + ":" + jsonResultContextItem.valueDisplay)
        }
        return childElements
    }

    /**
     * if the attribute id maps to an item in the dictionary that is a screening concentration
     * @param attributeId
     * @return
     */
    protected boolean isScreeningConcentration(Long attributeId) {
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptors(SCREENING_CONCENTRATION_PATH, null)
        for (Descriptor descriptor : descriptors) {
            if (descriptor.element.id == attributeId) {
                return true
            }
        }
        return false
    }
    /**
     *
     * For it to be a dose curve
     * The result type must be a "concentration response type"
     *
     * It must have children
     *
     * One of the children must have a result type of response endpoint
     *
     * And that child must have at least one context one of which must be a screening concentration
     *
     * e.g
     *{result type = ac50, children = [ {result type = result endpoint, contexts = [ { attribute = "screening concentration", value= 10 } ] }*
     * @param jsonResult
     * @return
     */
    boolean isConcentrationResponseEndPoint(JsonResult jsonResult) {
        ResultTypeTree resultTypeTree = addOrFindResultTypeTreeToCache(jsonResult.resultTypeId)
        if (resultTypeTree) {
            if (resultTypeTree.fullPath.toUpperCase().contains(CONCENTRATION_END_POINT)) {

                final List<JsonResult> related = jsonResult.related
                if (!related) { //if it has no child nodes
                    return false
                }
                for (JsonResult relatedResult : related) {
                    if (isPercentResponse(relatedResult.resultTypeId)) {
                        final List<JsonResultContextItem> resultContextItems = relatedResult.contextItems
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
    public void convertExperimentResultsToTableModelCellsAndRows(final Map resultsMap,
                                                                 final List<JsonResult> rootElements) {

        for (JsonResult jsonResult : rootElements) {
            convertSingleExperimentResult(resultsMap, jsonResult)
        }
    }

    /**
     *
     * @param resultsMap
     * @param jsonResult
     */
    protected void convertSingleExperimentResult(final Map resultsMap, final JsonResult jsonResult) {

        final long resultTypeId = jsonResult.resultTypeId
        String display = jsonResult.valueDisplay
        String resultType = jsonResult.resultType

        //add this to the list of priority element values
        if (resultsMap.priorityElements.contains(resultType.trim())) {
            resultsMap.priorityElementValues << new StringValue(value: resultType + ":" + display)
        }
        if (isConcentrationResponseEndPoint(jsonResult)) {

            handleConcentrationResponsePoints(jsonResult, resultsMap)
            //go ahead and do dose related stuff
        } else if (isPubChemOutcome(resultTypeId)) { //what we actually need is everything at the root of the measures tree
            // that is not a Concentration/response endpoint so that we can display it in a column
            StringValue outcome = new StringValue(value: jsonResult.valueDisplay)
            resultsMap.outcome = outcome

            handleContextItemsAndRelatedResults(resultsMap, jsonResult)
        } else {
            //treat as child element
            if (!resultsMap.priorityElements.contains(jsonResult.resultType)) {
                resultsMap.childElements << new StringValue(value: jsonResult.resultType + ":" + jsonResult.valueDisplay)
            }
            handleContextItemsAndRelatedResults(resultsMap, jsonResult)
        }
    }

    protected void handleContextItemsAndRelatedResults(final Map resultsMap, final JsonResult jsonResult) {
        if (jsonResult.contextItems) {
            resultsMap.childElements << contextItemsToChildElements(jsonResult.contextItems)
        }
        if (jsonResult.related) {
            convertExperimentResultsToTableModelCellsAndRows(resultsMap, jsonResult.related)
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
