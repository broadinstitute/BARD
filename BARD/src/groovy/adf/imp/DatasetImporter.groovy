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

package adf.imp

import adf.exp.AbstractResultTree
import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bard.db.experiment.JsonSubstanceResults
import bard.db.experiment.ResultsService
import bard.db.experiment.results.Cell
import bard.db.registration.AssayContext
import bard.db.registration.AttributeType

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 4/15/14
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
class DatasetImporter {
    static class DatasetRow {
        Dataset dataset;
        int rowIndex;
        List<DatasetRow> children = [];

        public DatasetRow(Dataset dataset, int rowIndex) {
            this.dataset = dataset;
            this.rowIndex = rowIndex;
        }
    }

    Map<Long, DatasetRow> indexByResultId(List<Dataset> datasets) {
        Map<Long, DatasetRow> byResultId = [:]
        datasets.each { dataset ->
            int resultIdColumnIndex = dataset.resultIdIndex
            dataset.rows.eachWithIndex {  row, i ->
                long resultId = Long.parseLong(row[resultIdColumnIndex])

                byResultId[resultId] = new DatasetRow(dataset, i)
            }
        }

        return byResultId;
    }

    List<DatasetRow> constructTree(Map<Long, DatasetRow> byResultId) {
        List<DatasetRow> roots = []

        byResultId.each { resultId, row ->
            String parentResultIdStr = row.dataset.rows[row.rowIndex] [row.dataset.parentResultIdIndex]
            if (parentResultIdStr == "") {
                roots.add(row)
            } else {
                Long parentResultId = Long.parseLong(parentResultIdStr);
                DatasetRow parent = byResultId[parentResultId]
                parent.children.add(row)
            }
        }

        return roots;
    }

    // Matches AC10,AC40,AC50,CC50,EC20,EC30,EC50,EC80,IC50,IC80,IC90,TC50,MIC
    static final Pattern CONCENTRATION_BASED_RESULT_TYPE = Pattern.compile("(?:[ACEIT]C[0-9]0)|MIC");

    int findRootResultIndex(List<DatasetColumn> resultTypes) {
        // TODO: Encode relationship in dictionary to avoid hardcoded types
        for(int i=0;i<resultTypes.size();i++) {
            DatasetColumn column = resultTypes.get(i)
            Matcher matcher = CONCENTRATION_BASED_RESULT_TYPE.matcher(column.attribute.label)
            if(matcher.matches()) {
                return i;
            }
        }
        // just pick the first if we can't find anything better
        return 0;
    }

    JsonResult translateRowToResult(DatasetRow row) {
        if(row.dataset.resultTypes.size() == 0) {
            throw new RuntimeException("Unexpected empty dataset");
        }

        int rootResultIndex = findRootResultIndex(row.dataset.resultTypes)
        JsonResult result = translateRowToResult(row, row.dataset.resultTypes[rootResultIndex]);
        for(int i=0;i<row.dataset.resultTypes.size();i++) {
            if(i == rootResultIndex)
                continue;

            result.related.add(translateRowToResult(row, row.dataset.resultTypes[i]))
        }
        return result
    }

    JsonResult translateRowToResult(DatasetRow row, DatasetColumn column) {
        JsonResult result = new JsonResult()

        result.resultTypeId = column.attribute.id;
        result.statsModifierId = column.statsModifier?.id;
        result.resultType = column.attribute.label;

        String valueString = row.dataset.rows[row.rowIndex][column.index]
        Cell cell = ResultsService.parseAnything(valueString)

        result.valueNum = cell.value;
        result.valueMin = cell.minValue;
        result.valueMax = cell.maxValue;
        result.valueDisplay = cell.valueDisplay
        result.qualifier = cell.qualifier;

        // TODO: We don't have enough information to get the relationship
        result.relationship = "supported by";
        result.related = row.children.collect { translateRowToResult(it) }
        result.contextItems = row.dataset.contextItems.collect { translateContextItem(row, it) };

        return result
    }

    JsonResultContextItem translateContextItem(DatasetRow row, DatasetColumn column) {
        JsonResultContextItem item = new JsonResultContextItem()

        item.attribute = column.attribute.label
        item.attributeId = column.attribute.id

        String valueString = row.dataset.rows[row.rowIndex][column.index]
        Cell cell = ResultsService.parseAnything(valueString)

        item.qualifier = cell.qualifier;
        item.valueNum = cell.value;
        item.valueMin = cell.minValue;
        item.valueMax = cell.maxValue;
        item.valueDisplay = cell.valueDisplay;

        return item;
    }

    JsonSubstanceResults translateRowsToResults(long sid, List<DatasetRow> rootRows) {
        List<JsonResult> results = rootRows.collect { translateRowToResult(it) };
        new JsonSubstanceResults(sid: sid, rootElem: results)
    }

    public void recreateMeasures(Experiment experiment, Collection<AbstractResultTree.Node> nodes) {
        ExperimentMeasure.withSession { s -> s.flush() }

        // delete all existing experiment measures
        for (ExperimentMeasure experimentMeasure in new ArrayList(experiment.experimentMeasures)) {
            experiment.removeFromExperimentMeasures(experimentMeasure)
            experimentMeasure.delete()
        }

        ExperimentMeasure.withSession { s -> s.flush() }

        createMeasure(experiment, nodes, null)
    }

    public void createMeasure(Experiment experiment, Collection<AbstractResultTree.Node> nodes, ExperimentMeasure parent) {
        nodes.each {
            HierarchyType parentChildRelationship = null;

            // TODO: Do not have the information needed for setting this correctly
            if(parent != null)
                parentChildRelationship = HierarchyType.SUPPORTED_BY;

            Element resultType = Element.get(it.path.result.resultTypeId)
            Element statsModifier = null;
            if(it.path.result.statsModifierId != null)
                statsModifier = Element.get(it.path.result.statsModifierId)

            ExperimentMeasure measure = new ExperimentMeasure(experiment: experiment,
                    resultType: resultType,
                    statsModifier: statsModifier,
                    parentChildRelationship: parentChildRelationship)

            if(parent != null) {
                parent.addToChildMeasures(measure)
            }

            measure.save()

            // TODO: This is a best effort attempt, but could pick wrong context
            it.contextItems.each { ctxItem ->
                // Check to see if measure is already linked to a context with the context item
                def existingLink = measure.assayContextExperimentMeasures.find { ctxem ->
                    contextContainsNonFixedContextItem(ctxItem.resultTypeId, ctxem.assayContext)
                }

                if(existingLink == null) {
                    // if it's not already linked, we need to find such a context
                    AssayContext targetContext = experiment.assay.contexts.find { ctx ->
                        contextContainsNonFixedContextItem(ctxItem.resultTypeId, ctx)
                    }

                    if(targetContext == null) {
                        log.warn("Could not find assay context in ADID ${experiment.assay.id} for attribute ${ctxItem.resultTypeId}")
                        return
                    }

                    // create a new link
                    AssayContextExperimentMeasure link = new AssayContextExperimentMeasure()
                    targetContext.addToAssayContextExperimentMeasures(link)
                    measure.addToAssayContextExperimentMeasures(link)
                    link.save()
                }
            }

            // recurse to create child measures
            createMeasure(experiment, it.children, measure)
        }
    }

    boolean contextContainsNonFixedContextItem(Long attributeId, AssayContext context) {
        return (context.assayContextItems.find {
            it.attributeType != AttributeType.Free && it.attributeElement.id == attributeId
        }) != null
    }

    void eachJsonSubstanceResult(DatasetParser parser, Closure callback) {
        while (parser.hasNext()) {
            // for each sample
            Batch b = parser.readNext()
            Map<Long, DatasetImporter.DatasetRow> byResultId = indexByResultId(b.datasets)
            List<DatasetImporter.DatasetRow> rootRows = constructTree(byResultId);
            JsonSubstanceResults result = translateRowsToResults(b.sid, rootRows)
            callback.call(result)
        }
    }

}
