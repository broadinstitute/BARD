package bard.db.experiment

import bard.db.experiment.results.ImportSummary
import bard.db.registration.ExternalReference
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 7/29/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
class PubchemImportService {
    GrailsApplication grailsApplication
    def pubchemReformatService
    ResultsService resultsService

    @PreAuthorize("hasRole('ROLE_BARD_ADMINISTRATOR')")
    ImportSummary recreateMeasuresAndLoad(boolean forceConvertPubchem, int aid, Closure statusCallback) {
        statusCallback("Starting...")

        String pubchemPrefix
        String pubchemFileDir
        String convertedFileDir

        pubchemPrefix = grailsApplication.config.bard.pubchemPrefix
        pubchemFileDir = "${pubchemPrefix}/pubchem-files"
        convertedFileDir = "${pubchemPrefix}/converted-files"

        PubchemReformatService.ResultMap map
        try {
            map = pubchemReformatService.loadMap(aid)
        } catch (Exception ex) {
            throw new RuntimeException("Exception while loading result map", ex)
        }

        List<ExternalReference> refs = ExternalReference.findAllByExtAssayRef("aid=${aid}")
        if(refs.size() == 0) {
            throw new RuntimeException("skipping ${aid} because it was not in the database at all")
        }

        List eids = refs.collectAll { it.experiment.id }
        if(eids.size() != 1) {
            Set expectedEids = map.getPanelEids() as Set
            if((eids as Set) != expectedEids) {
                throw new RuntimeException("${aid} in DB multiple times: ${eids} but result map only mentioned the following eids: ${expectedEids}")
            }
        }

        statusCallback("Recreating measures...")

        if(map.allRecords.size() == 0) {
            throw new RuntimeException("Skipping ${aid} -> ${ref.experiment.id} because we're missing resultmapping")
        }

        try {
            pubchemReformatService.recreateMeasures(ref.experiment, map)
        } catch (Exception ex) {
            throw new RuntimeException("Exception while creating measures", ex)
        }

        refs = ExternalReference.findAllByExtAssayRef("aid=${aid}")

        for(eid in eids) {
            def pubchemFile = "${pubchemFileDir}/${aid}.csv"
            def capFile = "${convertedFileDir}/exp-${aid}-${eid}.csv"

            if (forceConvertPubchem || !(new File(capFile).exists())) {
                statusCallback("Converting pubchem file...")
                log.error("Converting pubchem file ${pubchemFile} -> ${capFile}")
                try {
                    pubchemReformatService.convert(eid, pubchemFile, capFile)
                } catch (Exception ex) {
                    throw new RuntimeException("Exception while converting", ex)
                }
            }

            statusCallback("Starting import...")
            // disable DB operations to speed this up for the migration process
            ResultsService.ImportOptions options = new ResultsService.ImportOptions()
            options.validateSubstances = false
            options.writeResultsToDb = false
            options.skipExperimentContexts = true
            options.statusCallback = statusCallback
            ImportSummary results = resultsService.importResults(eid, new FileInputStream(capFile), options)
            log.info("errors from loading ${aid}: ${results.errors.size()}")
            for(e in results.errors) {
                log.info("\t${e}")
            }

            if(results.errors.size() > 0) {
                log.error("failed to load: ${aid}")
            } else {
                log.info("successfully loaded: ${aid}")
                log.info("imported: ${results.resultsCreated} results")
            }
        }

        return results;
    }
}
