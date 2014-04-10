package adf

import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem

/**
 * Created by ddurkin on 3/20/14.
 */
class Box {
//    ResultSetPipeline resultSetPipeline
//
//    Path resultSetPipelinePath

    final List<ResultKey> columns;
    final List<ResultKey> contextItems;

    // the set of context items/result types present in this box
    public Box(List<ResultKey> columns, List<ResultKey> contextItems) {
        this.columns = columns
        this.contextItems = contextItems
    }

    public List<String> getColumnNames() {
        return columns.collect { it.toString() } + this.contextItems.collect { it.toString() }
    }

//    public void addResultType(JsonResult result) {
//        columns.add(new ResultKey(result))
//    }
//
//    public void removeResultType(JsonResult result) {
//        columns.remove(new ResultKey(result))
//    }
//
//    public void addContextItem(JsonResultContextItem contextItem) {
//        columns.add(new ResultKey(contextItem))
//    }

    public String toString() {
        return "Box<${columns.toString()}>"
    }
}
