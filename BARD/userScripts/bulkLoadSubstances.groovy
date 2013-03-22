import au.com.bytecode.opencsv.CSVReader
import bard.db.experiment.BulkSubstanceService
import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

String experimentFile = System.getProperty("expFile")
if (experimentFile == null) {
    throw new RuntimeException("Need to run with -DexpFile=FILENAME")
}

SpringSecurityUtils.reauthenticate('integrationTestUser', null)

BulkSubstanceService bulkSubstanceService = ctx.bulkSubstanceService
assert bulkSubstanceService != null

CSVReader reader = new CSVReader(new FileReader(experimentFile))
boolean foundStart = false;
List<Long> sids = []
while(true) {
    String[] line = reader.readNext()
    if (line == null) break
    if (foundStart) {
        sids.add(Long.parseLong(line[0]))
    }
    else if (line[1] == "Substance")
        foundStart = true;
}
assert foundStart

try {
    def missing = bulkSubstanceService.findMissingSubstances(sids)
    bulkSubstanceService.insertSubstances(missing, "bulk-substance-load")
} catch (Exception ex) {
    ex.printStackTrace()
}



