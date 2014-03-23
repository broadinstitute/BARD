import bard.db.experiment.ResultsService
import bard.db.experiment.results.ImportSummary
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created by dlahr on 3/15/14.
 */


final File uploadFileDir = new File("c:/Local/i_drive/projects/bard/dataMigration/NCI60/data/output")

SpringSecurityUtils.reauthenticate("dlahr", null)

ResultsService resultsService = ctx.resultsService

for (File uploadFile : uploadFileDir.listFiles()) {
    Integer experimentId = readExperimentId(uploadFile)

    println("$experimentId $uploadFile")

    FileInputStream fileInputStream = new FileInputStream(uploadFile)

    ImportSummary importSummary = resultsService.importResults(experimentId, fileInputStream)

    if (importSummary.errors.size() > 0) {
        println("errors with upload:")
        println(importSummary.errors.join("\n"))
    }
}


return


Integer readExperimentId(File uploadFile) {
    BufferedReader reader = new BufferedReader(new FileReader(uploadFile))

    String[] split = reader.readLine().split(",")
    Integer eid = Integer.valueOf(split[2])

    reader.close()

    return eid
}