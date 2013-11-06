package bard.db.experiment

import bard.db.experiment.results.ImportSummary
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.registration.Panel
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
        Panel panel = null;

        if(eids.size() != 1) {
            Set expectedEids = map.getPanelEids() as Set
            if((eids as Set) != expectedEids) {
                throw new RuntimeException("${aid} in DB multiple times: ${eids} but result map only mentioned the following eids: ${expectedEids}")
            }

            panel = derivePanel(eids)
        }

        statusCallback("Recreating measures...")

        if(map.allRecords.size() == 0) {
            throw new RuntimeException("Skipping ${aid} -> ${refs*.experiment*.id.join(', ')} because we're missing resultmapping")
        }

        for(eid in eids) {
            Experiment experiment = Experiment.get(eid)
            try {
                pubchemReformatService.recreateMeasures(experiment, map)
            } catch (Exception ex) {
                throw new RuntimeException("Exception while creating measures", ex)
            }
        }

        refs = ExternalReference.findAllByExtAssayRef("aid=${aid}")

        ImportSummary firstResults = null
        for(eid in eids) {
            def pubchemFile = "${pubchemFileDir}/${aid}.csv"
            def capFile = "${convertedFileDir}/exp-${aid}-${eid}.csv"

            if (forceConvertPubchem || !(new File(capFile).exists())) {
                statusCallback("Converting pubchem file...")
                log.warn("Converting pubchem file ${pubchemFile} -> ${capFile}")
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
            if(firstResults == null)
                firstResults = results

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

        if(panel != null) {
            PanelExperiment panelExperiment = createPanelExperiment(panel, eids);
            log.info("Created an experiment panel ${panelExperiment.id}")
        }

        return firstResults;
    }

    PanelExperiment createPanelExperiment(Panel panel, Collection<Long> eids) {
        PanelExperiment panelExperiment = new PanelExperiment();
        eids.each {
            panelExperiment.addToExperiments(Experiment.get(it))
        }
        panelExperiment.panel = panel

        panelExperiment.save(failOnError: true)

        return panelExperiment;
    }

    Panel derivePanel(Collection<Long> eids) {
        // find the unique set of assays used by these experiments.
        Set<Assay> assays = new HashSet( eids.collect { Experiment.get(it).assay } )

        // the unique set of panels that those assays are in
        Set<Panel> panels = assays.collectMany { it.panelAssays.panel }

        // now, find a panel which has exactly this set of assays in it
        Set<Panel> matching = panels.findAll { panel ->
            return new HashSet(panel.panelAssays.collect { it.assay }).equals(assays)
        }

        if(matching.size() == 0) {
            throw new RuntimeException("Could not find panel which contain assays ${assays}");
        }

        if(matching.size() > 1) {
            throw new RuntimeException("Found multiple panels ${matching} which contain assays ${assays}");
        }

        return matching.first()
    }
}
