import bard.db.experiment.JsonResult

/**
 * Created by ddurkin on 3/20/14.
 */
class ResultSetPipeline {

    Map<ResultSetPipelinePath, ResultSetBox> resultSetBoxMap = [:].withDefault { path ->
        new ResultSetBox(resultSetPipeline: this)
    }

    void add(JsonResult jr, ResultSetPipelinePath path, Long sid){

//        println("${offset}${path.getPath().join(' > ')}")


        ResultSetPipelinePath keyPathForBox = path.parent ?: path
        if(path.resultTypeId == 986L && path.statsModifierId == null){
            keyPathForBox = path
        }
        ResultSetBox resultSetBox = this.resultSetBoxMap.get(keyPathForBox)
        resultSetBox.resultSetPipelinePath = path
        if(resultSetBox.sids.contains(sid) == false){
            resultSetBox.sids.add(sid)
        }

        Map<Long,List<JsonResult>> sidToJsonResultMap = resultSetBox.resultsPerMeasureMap.get(path)

        List<JsonResult> jsonResultList = sidToJsonResultMap.get(sid) ?: []
        jsonResultList.add(jr)
        sidToJsonResultMap.put(sid, jsonResultList)
    }

}
