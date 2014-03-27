import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bard.db.experiment.JsonSubstanceResults
import org.apache.commons.lang3.StringUtils

/**
 * Created by ddurkin on 3/20/14.
 */
class ResultSetPipeline {

    Map<ResultSetPipelinePath, ResultSetBox> resultSetBoxMap = [:].withDefault { path ->
        new ResultSetBox(resultSetPipeline: this)
    }

    void parse(JsonSubstanceResults jsr) {
        final Long sid = jsr.sid
        final List<JsonResult> rootElement = jsr.rootElem
        parseJsonResult(jsr.rootElem, sid)
    }

    private void parseJsonResult(List<JsonResult> jsonResultList, ResultSetPipelinePath parentPath = null, Long sid) {
        for (JsonResult jr in jsonResultList) {
            final ResultSetPipelinePath path = new ResultSetPipelinePath(jr, parentPath)
            add(jr, path, sid)
            printJsonResult(jr, path)
            add(jr.contextItems, path, sid)
            parseJsonResult(jr.related, path, sid)
        }
    }

    void add(JsonResult jr, ResultSetPipelinePath path, Long sid) {

        add(new ResultJsonContainer(jr), path, sid)
    }

    void add(JsonResultContextItem jrci, ResultSetPipelinePath path, Long sid) {

        add(new ResultJsonContainer(jrci), path, sid)
    }

    void add(List<JsonResultContextItem> jrciList, ResultSetPipelinePath parentPath, Long sid) {

        List<JsonResultContextItem> uniqueJrciList = jrciList.unique { a, b -> a.attribute<=>b.attribute }
        for (JsonResultContextItem uniqueJrci in uniqueJrciList) {
            printJsonResultContextItem(uniqueJrci, new ResultSetPipelinePath(uniqueJrci, parentPath))
            add(new ResultJsonContainer(uniqueJrci), new ResultSetPipelinePath(uniqueJrci, parentPath), sid)
        }
    }

    void add(ResultJsonContainer resultJsonContainer, ResultSetPipelinePath path, Long sid) {

        ResultSetPipelinePath keyPathForBox = path.parent ?: path
        // hack to separate out percent activity
        if (path.resultTypeId == 986L) {
            keyPathForBox = path
        }

        ResultSetBox resultSetBox = this.resultSetBoxMap.get(keyPathForBox)
        resultSetBox.resultSetPipelinePath = path
        if (resultSetBox.sids.contains(sid) == false) {
            resultSetBox.sids.add(sid)
        }
        Map<Long, List<ResultJsonContainer>> sidToResultJsonContainerResultMap = resultSetBox.resultsPerMeasureMap.get(path)
        List<ResultJsonContainer> resultJsonContainerList = sidToResultJsonContainerResultMap.get(sid) ?: []
        resultJsonContainerList.add(resultJsonContainer)
        sidToResultJsonContainerResultMap.put(sid, resultJsonContainerList)
    }


    void printlnResultSetPipeline() {
        Map<ResultSetPipelinePath, ResultSetBox> map = this.resultSetBoxMap.sort { a, b -> b.key?.path?.size()<=>a.key?.path?.size() }

        for (ResultSetPipelinePath path in map.keySet()) {
            ResultSetBox box = map.get(path)
            println("**************************************************")
            println(box.sids)
            List<List<String>> rows = []
            Set<ResultSetPipelinePath> columnHeaders = box.resultsPerMeasureMap.keySet()
            final List<String> columnHeaderStrings = columnHeaders.collect { it.toString() }
            columnHeaderStrings.add(0, 'sid')
            rows.add(columnHeaderStrings)

            final int maxResultRows = box.getNumberOfRows()
            for (Long sid in box.sids) {

                for (int i = 0; i < maxResultRows; i++) {
                    List<String> rowValues = []
                    columnHeaders.eachWithIndex { ResultSetPipelinePath columnHeader, int index ->
                        if (index == 0) {
                            rowValues.add(sid)
                        }
                        final Map<Long, List<ResultJsonContainer>> cellData = box.resultsPerMeasureMap.get(columnHeader)
                        final List<ResultJsonContainer> resultJsonContainerList = cellData.get(sid)
                        final String valueDisplay = resultJsonContainerList?.getAt(i)?.valueDisplay
                        rowValues.add(valueDisplay)
                    }
                    rows.add(rowValues)
                }
            }

            for (List<String> row in rows) {
                println(row.join(','))
            }
        }

    }

    private void printJsonResult(JsonResult jr, ResultSetPipelinePath path) {
        String offset = buildOffset(path)
        final String relationShip = StringUtils.trimToEmpty(jr.relationship).padRight(16)
        println("${offset}${jr.resultType.padRight(30)} ${relationShip} ${jr.valueDisplay}")
    }

    private void printJsonResultContextItem(JsonResultContextItem jrci, ResultSetPipelinePath path) {
        final String offset = buildOffset(path)
        println("${offset}attribute: ${jrci.attribute.padRight(30)} -> value: ${jrci.valueDisplay}")
    }


    private String buildOffset(ResultSetPipelinePath path) {
        String offset = ""
        path.getPath().size().times {
            offset += "    "
        }
        offset
    }


}
