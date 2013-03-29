import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.experiment.PubchemReformatService
import bard.db.registration.ExternalReference
import java.text.SimpleDateFormat

pubchemFileDir = "/Users/pmontgom/data/pubchem-conversion/pubchem-files"
convertedFileDir = "/Users/pmontgom/data/pubchem-conversion/converted-files"
aidListFilename = "/Users/pmontgom/data/pubchem-conversion/topmarginal-2.txt"

boolean forceReloadResults = false;
boolean forceRecreateMeasures = true;
boolean forceConvertPubchem = true;

SpringSecurityUtils.reauthenticate('resultloader', null)
PubchemReformatService pubchemReformatService = ctx.pubchemReformatService
assert pubchemReformatService != null
ResultsService resultsService = ctx.resultsService

dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss")

FileWriter logWriter = new FileWriter("resultload-${dateFormat.format(new Date())}.log", true)

log = {msg -> 
   println(msg)
   logWriter.write(dateFormat.format(new Date())+" "+msg+"\n")
   logWriter.flush()
}

recreateMeasuresAndLoad = { aid ->
    ExternalReference ref = ExternalReference.findByExtAssayRef("aid=${aid}")

    if (ref.experiment == null) {
        log("Skipping ${aid} because it looks like its a summary aid")
        return
    }

    if(!forceReloadResults && ref.experiment.experimentFiles.size() > 0) {
        log("Skipping ${aid} -> ${ref.experiment.id} because results exist")
        return
    }

    def map = pubchemReformatService.loadMap(Long.parseLong(aid))

    if(map.allRecords.size() == 0) {
        log("Skipping ${aid} -> ${ref.experiment.id} because we're missing resultmapping")
        return
    } else {
        log("Result map for ${aid}")
        for(r in map.allRecords) {
           log("   ${r}")
        }
    }

    log("Creating measures for ${aid} -> ${ref.experiment.id}")
    recreateMeasures(aid)

    def pubchemFile = "${pubchemFileDir}/${aid}.csv"
    def capFile = "${convertedFileDir}/exp-${aid}-${ref.experiment.id}.csv"

    if (forceConvertPubchem || !(new File(capFile).exists())) {
        log("Converting pubchem file ${pubchemFile} -> ${capFile}")
        pubchemReformatService.convert(ref.experiment.id, pubchemFile, capFile)
    }

    log("Importing ${aid}...")

    // disable DB operations to speed this up for the migration process
    ResultsService.ImportOptions options = new ResultsService.ImportOptions()
    options.validateSubstances = false
    options.writeResultsToDb = false
    ResultsService.ImportSummary results = resultsService.importResults(ref.experiment, new FileInputStream(capFile), options)
    log("errors from loading ${aid}: ${results.errors.size()}")
    for(e in results.errors) {
        log("\t${e}")
    }

    if(results.errors.size() > 0) {
      log("failed to load: ${aid}")
    } else {
      log("successfully loaded: ${aid}")
      log("imported: ${results.resultsCreated} results")
    }
}

copyResultMap = {
    log("copying result map from southern.result_map")
    ExternalReference.withSession { session -> 
        session.createSQLQuery("""
        BEGIN
          delete from result_map;
          insert into result_map select * from southern.result_map@barddev;
        END;
        """).executeUpdate()
    }
}

recreateMeasures = { aid ->
    ExternalReference.withSession { session -> 
        session.createSQLQuery("""
        BEGIN
          result_map_util.transfer_result_map('${aid}');
        END;
        """).executeUpdate()
    }
}

readAids = { filename ->
    return new File(filename).readLines()
}

aids = readAids(aidListFilename)
log("Processing ${aids.size} AIDs from ${aidListFilename}")

copyResultMap()
for(aid in aids) {
  if(Thread.currentThread().isInterrupted()) 
      break
      
  log("loading ${aid}")
  recreateMeasuresAndLoad(aid)
}
