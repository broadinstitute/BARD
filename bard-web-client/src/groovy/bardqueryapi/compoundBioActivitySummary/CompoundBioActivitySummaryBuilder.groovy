package bardqueryapi.compoundBioActivitySummary

import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair

import bard.core.adapter.AssayAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.assays.Assay
import bard.core.util.FilterTypes
import org.apache.log4j.Logger
import bard.core.rest.spring.experiment.*
import bardqueryapi.*

/**
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 3/6/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
class CompoundBioActivitySummaryBuilder {
    IQueryService queryService
    static final Logger log = Logger.getLogger(CompoundBioActivitySummaryBuilder.class)

    CompoundBioActivitySummaryBuilder(IQueryService queryService) {
        this.queryService = queryService
    }

    public TableModel buildModel(GroupByTypes groupByType,
                                 Map groupedByExperimentalData, // Map<ADID/PID, List<Activity>>
                                 List<Assay> testedAssays,
                                 List<Assay> hitAssays,
                                 List<FilterTypes> filterTypes,
                                 Map<Long, List<ExperimentSearch>> experimentsMap,
                                 List<Long> sortedKeys,
                                 Double yNormMin = null,
                                 Double yNormMax = null) {

        TableModel tableModel = new TableModel()
        //Setup the headers
        tableModel.columnHeaders = [new StringValue(value: "${groupByType == GroupByTypes.ASSAY ? 'Assay Definition' : 'Project'}"), new StringValue(value: 'Experiments')]

        //Create a list rows, each row represents a collection of experiments grouped by a resource (assay or project)
        for (Long resourceId in sortedKeys) {
            List<WebQueryValue> singleRowData = []

            //The first cell (column) is the resource (assay or a project)
            WebQueryValue resource = findResourceByTypeAndId(groupByType, resourceId, testedAssays, hitAssays, filterTypes)
            if (!resource) {
                log.error("Could not map resource ${groupByType}: ${resourceId}")
                continue
            }
            singleRowData << resource

            List<Activity> exptDataList = groupedByExperimentalData[resourceId]
            //For each grouped-by resource item (assay/project), add all the grouped-by experimental data, converting result types into the appropriate value-types.
            //Use:
            // 1. A list of 'box' maps to collect all the experiments data, grouped by experiment (experiment being the key)
            // 2. A map for each experiment-level data, where the experiment is the key and the results are in a list
            // 3. A list of result type values (curves, single-points, etc.)
            for (Activity exptData in exptDataList) {
                String key = exptData.exptDataId //e.g., "14359.26749233"
                //Represents an experiment box in the Compound Bio Activity Summary panel. Each 'box' includes
                // an experiment summary title and then a list of WebQueryValue result types (curves, single-points, etc.)
                Map<WebQueryValue, List<WebQueryValue>> experimentBox = [:]
                //Add the experiment itself (for an experiment's description title)
                ExperimentSearch experimentSearch = experimentsMap[exptData.bardExptId].first()
                WebQueryValue experiment = new ExperimentValue(value: experimentSearch)

                List<WebQueryValue> results = convertExperimentResultsToValues(exptData, yNormMin, yNormMax)

                experimentBox.put(experiment, results)
                //Cast the experimentBox to a MapValue type
                MapValue experimentBoxValue = new MapValue(value: experimentBox)
                //Add the experimnet box to the row's list of value
                singleRowData << experimentBoxValue
            }

            tableModel.data << singleRowData
        }
        return tableModel
    }


    WebQueryValue findResourceByTypeAndId(GroupByTypes groupByType,
                                          Long resourceId,
                                          List<Assay> testedAssays,
                                          List<Assay> hitAssays,
                                          List<FilterTypes> filterTypes) {

        WebQueryValue resource

        switch (groupByType) {
            case GroupByTypes.ASSAY:
                List<AssayAdapter> assayAdapters
                //For assays, we can use the testedAssays/hitAssays properties in the compoundSummary resource.
                List<Assay> assays = filterTypes.contains(FilterTypes.TESTED) ? testedAssays : hitAssays
                assayAdapters = assays.unique().findAll {Assay assay -> assay.id == resourceId}.collect {Assay assay -> return new AssayAdapter(assay)}

                if (assayAdapters.size() == 1) {
                    resource = new AssayValue(value: assayAdapters.first())
                } else {
                    log.error("Could not find Assay with ADID=${resourceId}")
                    resource = new AssayValue()
                }
                break;
            case GroupByTypes.PROJECT:
                List<ProjectAdapter> projectAdapters
                try {
                    projectAdapters = queryService.findProjectsByPIDs([resourceId]).projectAdapters
                }
                catch (Exception exp) {
                    Log.error("Could not find Project: ${resourceId}")
                    return resource
                }
                if (projectAdapters.size() == 1) {
                    resource = new ProjectValue(value: projectAdapters.first())
                } else {
                    Log.error("Could not find Project with PID=${resourceId}")
                    resource = new ProjectValue()
                }
                break;
            default:
                throw new RuntimeException("Group-by type ${groupByType} is not supported")
        }

        return resource
    }

    /**
     * Map an experiment (exptData) into a list of table-model 'Values'.
     * The main result data is in the experiment's priority elements.
     * @param exptData
     * @return
     */
    static List<WebQueryValue> convertExperimentResultsToValues(Activity exptData, Double yNormMin = null, Double yNormMax = null) {
        List<WebQueryValue> values = []
        String respClss = exptData?.resultData?.responseClass
        ResponseClassEnum responseClass = respClss ? ResponseClassEnum.toEnum(respClss) : null

        for (PriorityElement priorityElement in exptData?.resultData?.priorityElements) {
            switch (responseClass) {
                case ResponseClassEnum.SP:
                    //The result-type is a single-point, key/value pair.
                    Pair<String, String> pair = new ImmutablePair<String, String>(priorityElement.dictionaryLabel, priorityElement.value)
                    LinkValue dictionaryElement
                    if (priorityElement.dictElemId) {
                        dictionaryElement = new LinkValue(value: "/bardwebclient/dictionaryTerms/#${priorityElement.dictElemId}")
                    }
                    PairValue pairValue = new PairValue(value: pair, dictionaryElement: dictionaryElement)
                    values << pairValue
                    break;
                case ResponseClassEnum.CR_SER:
                    //the result type is a curve.
                    if (priorityElement.concentrationResponseSeries) {
                        //Add the concentration/value series
                        ConcentrationResponseSeries concentrationResponseSeries = priorityElement.concentrationResponseSeries
                        List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
                        ActivityConcentrationMap doseResponsePointsMap = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponsePoints)
                        CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
                        Pair<StringValue, StringValue> title = new ImmutablePair<StringValue, StringValue>(new StringValue(value: priorityElement.dictionaryLabel), new StringValue(value: priorityElement.value))
                        LinkValue dictionaryElement
                        if (priorityElement.dictElemId) {
                            dictionaryElement = new LinkValue(value: "/bardwebclient/dictionaryTerms/#${priorityElement.dictElemId}")
                        }
                        ConcentrationResponseSeriesValue concentrationResponseSeriesValue = new ConcentrationResponseSeriesValue(value: doseResponsePointsMap,
                                title: new PairValue(value: title, dictionaryElement: dictionaryElement),
                                curveFitParameters: curveFitParameters,
                                slope: priorityElement.getSlope(),
                                responseUnit: concentrationResponseSeries.responseUnit,
                                testConcentrationUnit: concentrationResponseSeries.testConcentrationUnit,
                                yNormMin: yNormMin,
                                yNormMax: yNormMax)
                        concentrationResponseSeriesValue.yAxisLabel = concentrationResponseSeriesValue.responseUnit
                        concentrationResponseSeriesValue.xAxisLabel = concentrationResponseSeriesValue.testConcentrationUnit
                        values << concentrationResponseSeriesValue
                    }
                    break;
                default:
                    log.info("Response-class not supported: ${responseClass}")
            }
        }

        return values
    }
}
