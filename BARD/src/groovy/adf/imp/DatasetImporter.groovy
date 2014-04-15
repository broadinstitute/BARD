package adf.imp

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bard.db.experiment.JsonSubstanceResults
import bard.db.experiment.ResultsService
import bard.db.experiment.results.Cell

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

        result.relationship;
        result.related = row.children.collect { translateRowToResult(it) }
        if(column == 0) {
            result.contextItems = row.dataset.contextItems.collect { translateContextItem(row, it) };
        }

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
        // item.valueElementId
        // item.extValueId;

        return item;
    }

    JsonSubstanceResults translateRowsToResults(long sid, List<DatasetRow> rootRows) {
        List<JsonResult> results = rootRows.collect { translateRowToResult(it) };
        new JsonSubstanceResults(sid: sid, rootElem: results)
    }
}
