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

package bardqueryapi.experiment

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.experiment.*
import bard.db.dictionary.OntologyDataAccessService
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.JsonSubstanceResults
import bardqueryapi.*
import bardqueryapi.compoundBioActivitySummary.CompoundBioActivitySummaryBuilder
import bardqueryapi.compoundBioActivitySummary.PreviewExperimentResultsSummaryBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication

class ExperimentBuilder {
    GrailsApplication grailsApplication
    CompoundRestService compoundRestService
    OntologyDataAccessService ontologyDataAccessService

    List<WebQueryValue> buildHeader(final boolean hasDoseCurve) {

        List<WebQueryValue> columnHeaders = []
        columnHeaders.add(new StringValue(value: "SID"))
        columnHeaders.add(new StringValue(value: "CID"))
        columnHeaders.add(new StringValue(value: "Structure"))
        columnHeaders.add(new StringValue(value: "Outcome"))
        columnHeaders.add(new StringValue(value: "Priority Elements"))
        if (hasDoseCurve) {
            columnHeaders.add(new StringValue(value: "Dose Response"))
        }
        columnHeaders.add(new StringValue(value: "Supplemental Information"))

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
     * @param compoundAdapterMap
     * @return
     */
    Map<Boolean, List<WebQueryValue>> addRowForResultsPreview(final JsonSubstanceResults substanceResults,
                                                              final Set<String> priorityElements,
                                                              final Double yNormMin,
                                                              final Double yNormMax, CompoundAdapter compoundAdapter,
                                                              PreviewExperimentResultsSummaryBuilder previewExperimentResultsSummaryBuilder) {

        //A row is a list of table cells, each implements WebQueryValue.
        List<WebQueryValue> rowData = []
        boolean hasDosePoints = false
        //SID
        Long sid = substanceResults.sid
        LinkValue sidValue = new LinkValue(value: "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${sid.toString()}",
                text: sid.toString(),
                imgFile: 'pubchem.png',
                imgAlt: 'PubChem')

        rowData.add(sidValue)
        //CID - Look up CID and Structure image
        if (compoundAdapter) {
            Long cid = compoundAdapter?.id
            def grailsApplicationTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            String linkValue = grailsApplicationTagLib.createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: cid.toString()])
            LinkValue cidValue = new LinkValue(value: linkValue, text: cid.toString())
            rowData.add(cidValue)

            StructureValue structureValue =
                new StructureValue(
                        cid: cid,
                        sid: sid,
                        smiles: compoundAdapter?.compound?.smiles,
                        name: compoundAdapter?.name,
                        numActive: compoundAdapter?.numberOfActiveAssays,
                        numAssays: compoundAdapter?.numberOfAssays
                )
            rowData.add(structureValue)
        } else {
            rowData.add(new StringValue(value: 'Not Available'))
            rowData.add(new StringValue(value: 'Not Available'))
        }




        Map resultsMap = [:]
        resultsMap.priorityElements = priorityElements
        resultsMap.priorityElementValues = []
        resultsMap.yNormMin = yNormMin
        resultsMap.yNormMax = yNormMax
        resultsMap.outcome = null
        resultsMap.experimentalValues = []
        resultsMap.childElements = []

        previewExperimentResultsSummaryBuilder.convertExperimentResultsToTableModelCellsAndRows(resultsMap,substanceResults.rootElem)


        StringValue outcome = resultsMap.outcome
        rowData.add(outcome)


        final List<StringValue> summaryResults = resultsMap.priorityElementValues as List
        rowData.add(new ListValue(value: summaryResults.sort()))


        List<WebQueryValue> experimentValues = previewExperimentResultsSummaryBuilder.sortWebQueryValues(resultsMap.experimentalValues)
        //if the result type is a concentration series, we want to add the normalization values to each curve.
        experimentValues.findAll({ WebQueryValue experimentResult ->
            experimentResult instanceof ConcentrationResponseSeriesValue
        }).each { ConcentrationResponseSeriesValue concResSer ->
            concResSer.yNormMax = yNormMax
            concResSer.yNormMin = yNormMin
        }


        if (experimentValues) {
            ListValue listValue = new ListValue(value: experimentValues)
            rowData.add(listValue)
            hasDosePoints = true
        }
        List<StringValue> supplementalInformation = []
        final List<WebQueryValue> elements = resultsMap.childElements
        for (def childElement : elements) {
            if (childElement instanceof StringValue) {
                supplementalInformation << childElement
            } else if (childElement instanceof List) {
                for (StringValue stringValue : childElement) {
                    supplementalInformation << stringValue
                }
            }
        }

        rowData.add(new ListValue(value: supplementalInformation.sort()))
        return [hasDosePoints: hasDosePoints, rowData: rowData];
    }
    /**
     *
     * Builds the tableModel's row based on the result set and types.
     *
     * @param activity
     * @param normalizeYAxis
     * @param yNormMin
     * @param yNormMax
     * @param compoundAdapterMap
     * @return
     */
    List<WebQueryValue> addRow(final Activity activity,
                               final Double yNormMin,
                               final Double yNormMax,
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
        if (cid) {
            def grailsApplicationTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            String linkValue = grailsApplicationTagLib.createLink(controller: 'bardWebInterface', action: 'showCompound', params: [cid: cid.toString()])
            LinkValue cidValue = new LinkValue(value: linkValue, text: cid.toString())

            rowData.add(cidValue)
        } else {
            rowData.add(new StringValue(value: 'No CID exists'))
        }

        //Structure image
        final CompoundAdapter compoundAdapter = compoundAdapterMap.get(cid)
        if (compoundAdapter) {
            StructureValue structureValue =
                new StructureValue(
                        cid: cid,
                        sid: sid,
                        smiles: compoundAdapter?.compound?.smiles,
                        name: compoundAdapter?.name,
                        numActive: compoundAdapter?.numberOfActiveAssays,
                        numAssays: compoundAdapter?.numberOfAssays
                )
            rowData.add(structureValue)
        } else {
            //Add an empty cell
            rowData.add(new StringValue(value: 'Not Available'))
        }

        //Outcome
        ResultData resultData = activity?.resultData
        StringValue outcome = new StringValue(value: resultData.outcome)
        rowData.add(outcome)

        //Priority elements go here
        final List<StringValue> summaryResults = priorityElementsToValueList(resultData.priorityElements)
        rowData.add(new ListValue(value: summaryResults.sort()))

        //Convert the experimental data to result types (curves, key/value pairs, etc.)
        List<WebQueryValue> experimentValues = CompoundBioActivitySummaryBuilder.convertExperimentResultsToValues(activity, yNormMin, yNormMax)
        //if the result type is a concentration series, we want to add the normalization values to each curve.
        experimentValues.findAll({ WebQueryValue experimentResult ->
            experimentResult instanceof ConcentrationResponseSeriesValue
        }).each { ConcentrationResponseSeriesValue concResSer ->
            concResSer.yNormMax = yNormMax
            concResSer.yNormMin = yNormMin
        }
        if (experimentValues) {
            ListValue listValue = new ListValue(value: experimentValues)
            rowData.add(listValue)
        }
        //Add all childElements of the priorityElements, if any.
        List<WebQueryValue> childElements = []

        for (RootElement rootElement : resultData.rootElements) {
            if (rootElement.toDisplay()) {
                childElements << new StringValue(value: rootElement.toDisplay())
                if (rootElement.childElements) {
                    addChildElements(rootElement.childElements, childElements)
                }
            }
        }
        for (PriorityElement priorityElement in resultData.priorityElements) {
            if (priorityElement?.hasChildElements()) {
                addChildElements(priorityElement.childElements, childElements)
            }
        }
        if (childElements) {
            rowData.add(new ListValue(value: childElements))
        }


        return rowData;
    }

    void addChildElements(final List<ActivityData> activitiesData, final List<WebQueryValue> childElements) {
        for (ActivityData activityData : activitiesData) {
            if (activityData.toDisplay()) {
                childElements << new StringValue(value: activityData.toDisplay())
            }
        }
    }

    List<WebQueryValue> priorityElementsToValueList(final List<PriorityElement> priorityElements) {
        final List<WebQueryValue> summaryResults = []
        for (PriorityElement priorityElement : priorityElements) {
            summaryResults << CompoundBioActivitySummaryBuilder.createPairValueFromPriorityElement(priorityElement)
        }

        return summaryResults;
    }

    void addRows(final List<Activity> activities,
                 final TableModel tableModel,
                 final NormalizeAxis normalizeYAxis,
                 final Double yNormMin,
                 final Double yNormMax,
                 final Map<Long, CompoundAdapter> compoundAdapterMap) {
        for (Activity activity : activities) {

            final List<WebQueryValue> rowData = addRow(activity, yNormMin, yNormMax, compoundAdapterMap)
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
        Double yNormMin = null
        Double yNormMax = null
        final NormalizeAxis normalizeYAxis = experimentDetails.normalizeYAxis
        final List<Activity> activities = experimentDetails.activities
        boolean hasDoseCurve = false
        if (activities) {
            hasDoseCurve = CompoundBioActivitySummaryBuilder.hasDoseCurve(activities.get(0))
        }
        tableModel.setColumnHeaders(buildHeader(hasDoseCurve))



        if (normalizeYAxis == NormalizeAxis.Y_NORM_AXIS) {
            yNormMin = experimentDetails.yNormMin
            yNormMax = experimentDetails.yNormMax

        }

        addRows(activities, tableModel, normalizeYAxis, yNormMin, yNormMax, compoundAdapterMap)

        return tableModel
    }

    public TableModel buildModelForPreview(Experiment experiment, List<JsonSubstanceResults> jsonSubstanceResultList) {
        final TableModel tableModel = new TableModel()
        int numberOfActives = 0 //loop through and extract from the the list
        tableModel.additionalProperties.put("experimentName", experiment?.experimentName)
        tableModel.additionalProperties.put("bardExptId", experiment?.ncgcWarehouseId)
        tableModel.additionalProperties.put("capExptId", experiment.id)
        tableModel.additionalProperties.put("bardAssayId", experiment.assay.ncgcWarehouseId)
        tableModel.additionalProperties.put("capAssayId", experiment.assay.id)
        tableModel.additionalProperties.put("actives", numberOfActives)
        tableModel.additionalProperties.put("confidenceLevel", experiment?.confidenceLevel)

        Double yNormMin = null
        Double yNormMax = null


        Set<String> priorityElements = [] as Set

        for (ExperimentMeasure experimentMeasure : experiment.experimentMeasures) {
            if (experimentMeasure.priorityElement) {
                priorityElements.add(experimentMeasure.displayLabel)
            }
        }
        boolean hasDosePoints = false
        PreviewExperimentResultsSummaryBuilder previewResultsSummaryBuilder = new PreviewExperimentResultsSummaryBuilder()
        previewResultsSummaryBuilder.ontologyDataAccessService = this.ontologyDataAccessService

        for (JsonSubstanceResults jsonSubstanceResults : jsonSubstanceResultList) {
            Long sid = jsonSubstanceResults.getSid()
            CompoundAdapter compoundAdapter = null
            try {
                final Compound compound = this.compoundRestService.getCompoundBySid(sid)
                compoundAdapter = new CompoundAdapter(compound)
            } catch (Exception ee) {
                log.error(ee,ee)
            }
            final Map<Boolean, List<WebQueryValue>> preview = addRowForResultsPreview(jsonSubstanceResults, priorityElements, yNormMin, yNormMax, compoundAdapter,previewResultsSummaryBuilder)
            if (!hasDosePoints) {
                hasDosePoints = preview.hasDosePoints
            }
            final List<WebQueryValue> rowData = preview.rowData
            tableModel.addRowData(rowData)
        }
        tableModel.additionalProperties.put("total", tableModel.rowCount)

        tableModel.setColumnHeaders(buildHeader(hasDosePoints))
        return tableModel
    }
}


