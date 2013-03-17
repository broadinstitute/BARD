import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.hibernate.Session

import java.util.zip.GZIPOutputStream

List sids = []

String experimentIdStr = System.getProperty("experimentId")
if (experimentIdStr == null) {
    throw new RuntimeException("Need to run with -DexperimentId=EXPERIMENT")
}

Long experimentId = Long.parseLong(experimentIdStr);

ctx.resultsExportService.dumpFromDb(experimentId)

