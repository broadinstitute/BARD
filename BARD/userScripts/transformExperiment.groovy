import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

String experimentIdStr = System.getProperty("expId")
String experimentFile = System.getProperty("expFile")
if (experimentIdStr == null || experimentFile == null) {
    throw new RuntimeException("Need to run with -DexpId=EXPERIMENT and -DexpFile=FILENAME")
}

Long experimentId = Long.parseLong(experimentIdStr);

SpringSecurityUtils.reauthenticate('integrationTestUser', null)

ResultsService resultsService = ctx.resultsService
assert resultsService != null
Experiment experiment = Experiment.get(experimentId)
assert experiment != null
try {
    ResultsService.ImportSummary results = resultsService.importResults(experiment, new FileInputStream(experimentFile))
    println("errors: ${results.errors.size()}")
    for(e in results.errors) {
        println("\t${e}")
    }
    println("imported: ${results.resultsCreated}")
} catch (Exception ex) {
    ex.printStackTrace()
}



