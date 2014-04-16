import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

ResultsService resultsService = ctx.resultsService

String experimentId = System.getProperty("expId")
if(experimentId == null) {
    throw new RuntimeException("need parameter -DexpId=X which identifies which experiment to import results for")
}

String fileDir = System.getProperty("fileDir")
if(fileDir == null) {
    throw new RuntimeException("need parameter -DfileDir=X which identifies the directory containing the files to load")
}

String username = System.getProperty("username")
if(username == null) {
    throw new RuntimeException("Need parameter -Dusername=X which identifies the user the import will be run as")
}

List<String> filenames = new File(fileDir).listFiles().collect { it.absolutePath }

println("For experiment ${experimentId} importing the following files: ${filenames}")

SpringSecurityUtils.reauthenticate(username, null)

resultsService.importTabular(Long.parseLong(experimentId), filenames)
