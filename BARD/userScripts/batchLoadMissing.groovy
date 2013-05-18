import bard.db.experiment.Experiment
import bard.db.experiment.ResultsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.experiment.PubchemReformatService
import bard.db.registration.ExternalReference
import java.text.SimpleDateFormat
import org.apache.commons.lang.exception.ExceptionUtils

pubchemPrefix=System.getProperty("pubchemPrefix", "/cbplat/bard/pubchem_files")


pubchemFileDir = "${pubchemPrefix}/pubchem-files"
convertedFileDir = "${pubchemPrefix}/converted-files"
aidListFilename = System.getProperty("aidFile")
if(aidListFilename == null) {
	throw new RuntimeException("Need filename to read AIDs from.  Specify with -DaidFile=filename")
}

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

copyResultMap = { aid -> 
    ExternalReference.withSession { session -> 
        session.createSQLQuery("""
        BEGIN
          delete from result_map;
          insert into result_map select * from vw_result_map where aid = ${aid};
        END;
        """).executeUpdate()
   }
}

recreateMeasuresAndLoad = { aid ->
    ExternalReference ref = ExternalReference.findByExtAssayRef("aid=${aid}")

    if(ref == null) {
       log("skipping ${aid} because it was not in the database at all")
       return
    }

    if (ref.experiment == null) {
        log("Skipping ${aid} because it looks like its a summary aid")
        return
    }

    if(!forceReloadResults && ref.experiment.experimentFiles.size() > 0) {
        log("Skipping ${aid} -> ${ref.experiment.id} because results exist")
        return
    }

    copyResultMap(aid)

    def map
    try {
        map = pubchemReformatService.loadMap(Long.parseLong(aid))
    } catch (Exception ex) {
                log("failed to load result map: ${aid}")
                log("Exception while loading result map: ${ExceptionUtils.getStackTrace(ex)}")
                return
    }

    if(map.allRecords.size() == 0) {
        log("Skipping ${aid} -> ${ref.experiment.id} because we're missing resultmapping")
        return
    } else {
//        log("Result map for ${aid}")
//        for(r in map.allRecords) {
//           log("   ${r}")
//        }
    }

    log("Creating measures for ${aid} -> ${ref.experiment.id}")
    try {
	recreateMeasures(aid)
    } catch (Exception ex) {
	log("failed to execute transfer result map: ${aid}")
	log("Exception while creating measures: ${ExceptionUtils.getStackTrace(ex)}")
	return
    }

    ref = ExternalReference.findByExtAssayRef("aid=${aid}")

    def pubchemFile = "${pubchemFileDir}/${aid}.csv"
    def capFile = "${convertedFileDir}/exp-${aid}-${ref.experiment.id}.csv"

    if (forceConvertPubchem || !(new File(capFile).exists())) {
        log("Converting pubchem file ${pubchemFile} -> ${capFile}")
	try {
	        pubchemReformatService.convert(ref.experiment.id, pubchemFile, capFile)
	} catch (Exception ex) {
		log("failed to convert pubchem file: ${aid}")
		log("Exception while converting: ${ExceptionUtils.getStackTrace(ex)}")
		return
	}
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

recreateMeasures = { aid ->
    ExternalReference.withSession { session -> 
        def lastErrorId = session.createSQLQuery("""select max(error_log_id) from error_log""").setCacheable(false).uniqueResult()

        session.createSQLQuery("""
        BEGIN
          result_map_util.transfer_result_map('${aid}');
        END;
        """).executeUpdate()

	def errors = session.createSQLQuery("""select err_msg || ' '|| ERR_COMMENT from error_log where error_log_id > ${lastErrorId}""").setCacheable(false).list()
	if(errors.size() > 0) {
		throw new RuntimeException("result_map_util.transfer_result_map('${aid}') failed: ${errors}")
	}
	session.clear()
    }
}

readAids = { filename ->
    return new File(filename).readLines()
}

aids = readAids(aidListFilename)
log("Processing ${aids.size} AIDs from ${aidListFilename}")

for(aid in aids) {
  if(Thread.currentThread().isInterrupted()) 
      break
      
  log("loading ${aid}")
  recreateMeasuresAndLoad(aid)
}
