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
        ResultSetPipeline resultSetPipeline = new ResultSetPipeline()
        for (int i in 0..1) {
            final JsonSubstanceResults jsr = jsonSubstanceResultsList[i]
            resultSetPipeline.parse(jsr)
        }
        resultSetPipeline
    }
}