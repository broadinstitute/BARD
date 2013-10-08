package bardqueryapi.compoundBioActivitySummary

import bard.core.rest.spring.experiment.ActivityConcentrationMap
import bard.core.rest.spring.experiment.CurveFitParameters
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

    static final Logger log = Logger.getLogger(PreviewResultsSummaryBuilder.class)
    /*
    Curve fit parameters
       919	Published	Hill coefficient
       920	Published	Hill s0
       921	Published	Hill sinf
       953	Published	log CC50
       958	Published	log MIC
       960	Published	log AC50
       962	Published	log EC50
       968	Published	log IC50
       1355	Published	log GI50
       1385	Published	log TGI
       */
    private static List<Long> FIT_PARAM_DICT_ELEM = [
            new Long(919),
            new Long(920),
            new Long(921),
            new Long(953),
            new Long(958),
            new Long(960),
            new Long(962),
            new Long(968),
            new Long(1355),
            new Long(1385)
    ]
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
    //Percent measures
    private static List<Long> EFFICACY_PERCENT_MEASURES = [
            new Long(982),
            new Long(986),
            new Long(991),
            new Long(992),
            new Long(994),
            new Long(996),
            new Long(998),
            new Long(1004),
            new Long(1005),
            new Long(1006),
            new Long(1007),
            new Long(1008),
            new Long(1009),
            new Long(1010),
            new Long(1011),
            new Long(1012),
            new Long(1013),
            new Long(1014),
            new Long(1016),
            new Long(1322),
            new Long(1324),
            new Long(1330),
            new Long(1340),
            new Long(1359),
            new Long(1361),
            new Long(1415),
            new Long(1416),
            new Long(1417),
            new Long(1448),
            new Long(1483),
            new Long(1484),
            new Long(1485),
            new Long(1486),
            new Long(1487)
    ]
    /**
     * Convert the list of jsonResults to Dose Response Curve values
     * @param jsonResults
     * @return
     */
    static Map convertToDoseResponse(List<JsonResult> jsonResults) {

        List<Double> concentrations = []
        List<Double> activities = []
        List<WebQueryValue> childElements = []
        CurveFitParameters curveFitParameters = new CurveFitParameters()
        String responseUnits = ""
        String concentrationUnits = ""
        for (JsonResult jsonResult : jsonResults) {
            final Long resultTypeId = jsonResult.resultTypeId

            if (FIT_PARAM_DICT_ELEM.contains(resultTypeId)) {  //TODO: refactor read from Result_Type_Tree
                switch (resultTypeId) {
                    case 919:
                        curveFitParameters.setHillCoef(jsonResult.valueNum)
                        break
                    case 920:
                        curveFitParameters.setS0(jsonResult.valueNum)
                        break
                    case 921:
                        curveFitParameters.setSInf(jsonResult.valueNum)
                        break
                    default:
                        curveFitParameters.setLogEc50(jsonResult.valueNum)
                        break
                }
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



    static void processJsonResults(final JsonResult jsonResult, List<WebQueryValue> childElements = [], List<WebQueryValue> values = [], List<WebQueryValue> rootElements = [], Double yNormMin = null,
                                   Double yNormMax = null) {

        final Long dictionaryId = jsonResult.resultTypeId
        if (HIGH_PRIORITY_DICT_ELEM.contains(dictionaryId)) { //TODO: refactor to read from Result_Type_Tree . We need the elements that could contain dose curves
            Pair<StringValue, StringValue> title =
                new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueDisplay))
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
            values << concentrationResponseSeriesValue

            //add context items , they are child elements
            final List<JsonResultContextItem> resultContextItems = jsonResult.contextItems
            if (resultContextItems) {
                childElements << contextItemsToChildElements(resultContextItems)
            }

        } else if (EFFICACY_PERCENT_MEASURES.contains(dictionaryId)) {
            Pair<StringValue, StringValue> pair =
                new ImmutablePair<StringValue, StringValue>(new StringValue(value: jsonResult.resultType), new StringValue(value: jsonResult.valueDisplay))
            LinkValue dictionaryElement

            if (dictionaryId) {
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

        for (JsonResult jsonResult : rootElements) {

            final long dictionaryId = jsonResult.resultTypeId

            if (dictionaryId == 896) { //this is the outcome result type. TODO: refactor, read from database
                outcome = new StringValue(value: jsonResult.valueDisplay)

                for (JsonResult relatedElements : jsonResult.related) {
                    processJsonResults(relatedElements, childElements, values, rootElementNodes, yNormMin, yNormMax)
                    if (relatedElements.contextItems) {
                        childElements << contextItemsToChildElements(relatedElements.contextItems)
                    }
                }
            } else {
                processJsonResults(jsonResult, childElements, values, rootElementNodes, yNormMin, yNormMax)
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
