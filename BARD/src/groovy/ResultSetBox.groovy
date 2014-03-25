import bard.db.experiment.JsonResult

/**
 * Created by ddurkin on 3/20/14.
 */
class ResultSetBox {

    ResultSetPipeline resultSetPipeline

    ResultSetPipelinePath resultSetPipelinePath


    List<Long> sids = []

    Map<ResultSetPipelinePath,Map<Long,List<JsonResult>>> resultsPerMeasureMap = [:].withDefault { path -> [:]}

    Integer getNumberOfRows(){
            resultsPerMeasureMap.values().collect{SidJsonResult sidJsonResult ->
                sidJsonResult.jsonResultList.size()
            }.max()
    }
}
