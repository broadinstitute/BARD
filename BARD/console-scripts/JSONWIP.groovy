import bard.db.experiment.JsonSubstanceResults
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import org.apache.commons.lang3.StringUtils

import java.util.zip.GZIPInputStream

/**
 * Created by ddurkin on 3/18/14.
 */


GZIPInputStream gis = new java.util.zip.GZIPInputStream(new File('/Users/ddurkin/dev/bard/BARD-df/BARD/exp-745-20131119-032846.json.gz').newInputStream())
final ObjectMapper mapper = new ObjectMapper()
final ObjectReader objectReader = mapper.reader(JsonSubstanceResults)
gis.eachLine {String line ->
    if(StringUtils.isNotBlank(line)){
        println(line)
        JsonSubstanceResults substanceResults = objectReader.readValue(line)
    }
}
