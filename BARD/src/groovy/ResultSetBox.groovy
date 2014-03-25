import bard.db.experiment.JsonResult

/**
 * Created by ddurkin on 3/20/14.
 */
class ResultSetBox {

    ResultSetPipeline resultSetPipeline

    ResultSetPipelinePath resultSetPipelinePath

    Map<Long, List<JsonResult>> sidToJsonResultMap = [:].withDefault { sid -> [] }


    Map<ResultSetPipelinePath,SidJsonResult> resultsPerMeasureMap = [:].withDefault { path -> new SidJsonResult()}
}
