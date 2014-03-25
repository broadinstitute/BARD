import bard.db.experiment.JsonResult
import bard.db.experiment.JsonResultContextItem
import bard.db.experiment.JsonSubstanceResults
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext

import java.util.zip.GZIPInputStream

/**
 * Created by ddurkin on 3/19/14.
 */
class JsonWipImpl {

    final GrailsWebApplicationContext ctx


    public JsonWipImpl(GrailsWebApplicationContext ctx) {
        this.ctx = ctx
    }

    def run() {
        List<JsonSubstanceResults> jsonSubstanceResultsList = []

        GZIPInputStream gis = new java.util.zip.GZIPInputStream(new File('/Users/ddurkin/dev/bard/BARD-df/BARD/exp-1462-20131027-144131.json.gz').newInputStream())
        final ObjectMapper mapper = new ObjectMapper()
        final ObjectReader objectReader = mapper.reader(JsonSubstanceResults)
        gis.eachLine { String line ->
            if (StringUtils.isNotBlank(line)) {
                jsonSubstanceResultsList << objectReader.readValue(line)
            }
        }
        println(jsonSubstanceResultsList.size())
        final JsonSubstanceResults jsr = jsonSubstanceResultsList[0]
        println("sid:$jsr.sid")
        def jsonUtil = new JsonUtil7(sid: jsr.sid)
        jsonUtil.printJsonResult(jsr.rootElem)
        jsonUtil
    }
}

class JsonUtil7 {
    Long sid
    ResultSetPipeline resultSetPipeline = new ResultSetPipeline()

    private void printJsonResult(List<JsonResult> jsonResultList, String offset = "", ResultSetPipelinePath parentPath = null, String keyPath = "") {

        final List<JsonResult> supportedByRelated = jsonResultList.findAll() { it.relationship == 'supported by' }
        doPrintJsonResult(supportedByRelated, offset, parentPath, keyPath)

        final List<JsonResult> calculateByRelated = jsonResultList.findAll() { it.relationship == 'calculated by' }
        doPrintJsonResult(calculateByRelated, offset, parentPath, keyPath)

        doPrintJsonResult(jsonResultList - (supportedByRelated + calculateByRelated), offset, parentPath, keyPath)
    }

    private void doPrintJsonResult(List<JsonResult> jsonResultList, String offset, ResultSetPipelinePath parentPath, String keyPath = "") {

        for (JsonResult jr in jsonResultList) {
            final ResultSetPipelinePath path = new ResultSetPipelinePath(jr, parentPath)
            this.resultSetPipeline.add(jr, path, sid)

            final String relationShip = StringUtils.trimToEmpty(jr.relationship).padRight(16)
            String key = "resultTypeId:${jr.resultTypeId} statsModifier: ${jr.statsModifierId} relationShip: ${relationShip}"
            //println("${offset} ${keyPath} ${key}")
            println("${offset}${jr.resultType.padRight(30)} ${relationShip} ${jr.valueDisplay}") // priorityElement: ?")
            printJsonResultContextItem(jr.contextItems, "    " + offset)
            printJsonResult(jr.related, "    " + offset, path, "${keyPath} ${key}".toString())
        }
    }

    private void printJsonResultContextItem(List<JsonResultContextItem> jsonResultContextItemList, String offset = "") {
        for (JsonResultContextItem jrci in jsonResultContextItemList) {
            println("${offset}attribute: ${jrci.attribute.padRight(30)} -> value: ${jrci.valueDisplay}")
        }
    }

    void printlnResultSetPipeline() {
        println("foo")
        Map<ResultSetPipelinePath, ResultSetBox> map = this.resultSetPipeline.resultSetBoxMap.sort { a, b -> b.key?.path?.size()<=>a.key?.path?.size() }

        for (ResultSetPipelinePath path in map.keySet()) {
            ResultSetBox box = map.get(path)
            println("**************************************************")
            List<List<String>> rows = []
            Set<ResultSetPipelinePath> columnHeaders = box.resultsPerMeasureMap.keySet()
            final List<String> columnHeaderStrings = columnHeaders.collect { it.resultType }
            columnHeaderStrings.add(0, 'sid')
            rows.add(columnHeaderStrings)

            final int maxResultRows = box.resultsPerMeasureMap.values()*.jsonResultList*.size().max()

            for (int i = 0; i < maxResultRows; i++) {
                List<String> rowValues = []
                columnHeaders.eachWithIndex{ ResultSetPipelinePath columnHeader, int index ->
                    final SidJsonResult column = box.resultsPerMeasureMap.get(columnHeader)
                    if(index==0){
                        rowValues.add(column.sid)
                    }
                    final String valueDisplay = column.jsonResultList[i]?.valueDisplay
                    rowValues.add(StringUtils.trimToEmpty(valueDisplay).padLeft(columnHeader.resultType.size()))
                }
                rows.add(rowValues)
            }
            for(List<String> row in rows){
                println(row.join(','))
            }
        }

    }
}
