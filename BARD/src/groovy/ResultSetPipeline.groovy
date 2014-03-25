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

        ResultSetBox resultSetBox = this.resultSetBoxMap.get(path.parent ?: path)
        resultSetBox.resultSetPipelinePath = path
        resultSetBox.sidToJsonResultMap.get(sid).add(jr)

        SidJsonResult sidJsonResult = resultSetBox.resultsPerMeasureMap.get(path)
        sidJsonResult.sid = sid
        sidJsonResult.jsonResultList.add(jr)
    }

}
